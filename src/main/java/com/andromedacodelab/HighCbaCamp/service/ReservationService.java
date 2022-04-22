package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.exception.DateRangeNotAcceptedException;
import com.andromedacodelab.HighCbaCamp.exception.InvalidDateRangeException;
import com.andromedacodelab.HighCbaCamp.exception.InvalidReservationStatusException;
import com.andromedacodelab.HighCbaCamp.exception.NonExistentReservationForDeleteException;
import com.andromedacodelab.HighCbaCamp.exception.ReservationCancelledException;
import com.andromedacodelab.HighCbaCamp.exception.ReservationNotFoundException;
import com.andromedacodelab.HighCbaCamp.exception.ReservationOutOfTermException;
import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.ReservationStatus;
import com.andromedacodelab.HighCbaCamp.model.builder.ReservationBuilder;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import com.andromedacodelab.HighCbaCamp.repository.ReservationStatusesRepository;
import com.andromedacodelab.HighCbaCamp.util.ReservationWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Set;
import java.util.stream.IntStream;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationStatusesRepository reservationStatusesRepository;
    private final GuestService guestService;
    private final AvailabilityService availabilityService;
    private static final Integer CONFIRMED_STATUS = 2;
    private static final Integer CANCELLED_STATUS = 4;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, GuestService guestService,
                              AvailabilityService availabilityService,
                              ReservationStatusesRepository reservationStatusesRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationStatusesRepository = reservationStatusesRepository;
        this.guestService = guestService;
        this.availabilityService = availabilityService;
    }

    /**
     * This method is intended for search a Reservation by its id
     *
     * @param bookingId the reservation unique id.
     * @return Reservation object with all details
     */
    public Reservation findReservationByBookingId(Integer bookingId) {
        Reservation reservation = getReservation(bookingId);
        reservation.setDeparture(reservation.getDeparture());
        return reservation;
    }

    /**
     * Create a Reservation
     *
     * @param reservationWrapper which is a wrapper class to map from json to an actual domain object
     * @return Reservation object with all details
     */
    public Reservation createReservation(ReservationWrapper reservationWrapper) {
        Reservation reservation = new ReservationBuilder()
                .withArrivalDate(reservationWrapper.getArrival())
                .withDepartureDate(reservationWrapper.getDeparture())
                .withGuests(reservationWrapper.getGuests()).build();

        if (validateDateRangeConstraints(reservation.getArrival(), reservation.getDeparture())) {
            throw new DateRangeNotAcceptedException();
        }

        // Checks if the provided date range is available to create a reservation
        if (!availabilityService.isReservationDateRangeAvailable(
                reservation.getArrival(), reservation.getDeparture())) {
            throw new InvalidDateRangeException();
        }

        // Check if the guests already exist in the DB, if not create them with GuestService
        doGuestExistInRecords(reservation.getGuests());

        ReservationStatus status = reservationStatusesRepository.getOne(CONFIRMED_STATUS);
        reservation.setStatus(status);
        try {
            reservationRepository.save(reservation);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        return reservation;
    }

    /**
     * Update an existing reservation validating if the dates or the guests are the same.
     * If not replace/remove for the new ones or update the existing if needed
     *
     * @param reservationWrapped that contains the data to build a new object
     * @return the reservation that was updated with its new state
     */
    public Reservation updateReservation(ReservationWrapper reservationWrapped) {
        ReservationStatus status = reservationStatusesRepository.findByName(reservationWrapped.getStatusName());

        Reservation reservation = new ReservationBuilder()
                .withBookingId(reservationWrapped.getBookingId())
                .withArrivalDate(reservationWrapped.getArrival())
                .withDepartureDate(reservationWrapped.getDeparture())
                .withStatus(status)
                .withGuests(reservationWrapped.getGuests()).build();

        // Creates the guests that don't exist in the DB
        doGuestExistInRecords(reservation.getGuests());

        Reservation oldReservation = getReservation(reservation.getBookingId());

        if (CANCELLED_STATUS.equals(oldReservation.getStatus().getId())) {
            throw new ReservationCancelledException();
        }

        // Removes guests that don't belong to the reservation anymore
        oldReservation.setGuests(reservation.getGuests());

        if (IntStream.rangeClosed(1, 4).noneMatch(s -> s == reservation.getStatus().getId())) {
            throw new InvalidReservationStatusException();
        } else if (!reservation.getStatus().equals(oldReservation.getStatus())) {
            // Updates status if needed
            oldReservation.setStatus(reservation.getStatus());
        }

        // Validates if the date range is the same than the persisted reservation
        validateChangesInDateRange(reservation, oldReservation);
        try {
            reservationRepository.save(oldReservation);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        return oldReservation;
    }

    public Reservation updateReservationStatus(Integer bookingId, Integer newStatusId) {
        Reservation oldReservation = getReservation(bookingId);

        ReservationStatus newReservationStatus = reservationStatusesRepository.getOne(newStatusId);

        if (IntStream.rangeClosed(1, 4).noneMatch(s -> s == newStatusId)) throw new InvalidReservationStatusException();

        oldReservation.setStatus(newReservationStatus);
        try {
            return reservationRepository.save(oldReservation);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        return oldReservation;
    }

    /**
     * Validates the following scenarios:
     * 1. The campsite can be reserved for a maximum of 3 days
     * 2. The campsite can be reserved minimum of 1 day ahead of arrival and up to 1 month in advance
     *
     * @param arrival   date to validate
     * @param departure date to validate against the above one
     */
    public boolean validateDateRangeConstraints(LocalDate arrival, LocalDate departure) {
        LocalDate now = LocalDate.now();
        Period period = Period.between(arrival, departure);
        int dayDifference = period.getDays();

        if (arrival.isEqual(now.plusDays(1)) || arrival.isBefore(now.plusDays(1))
                || arrival.isBefore(now.plusMonths(1))) {
            throw new ReservationOutOfTermException();
        }
        return dayDifference > 4;
    }

    /**
     * Checks two reservation dates are the same ignoring hours of day.
     *
     * @param newReservationDate the new reservation date
     * @param oldReservationDate the old reservation date
     * @return true if the dates are equal
     */
    private boolean validateReservationDatesAreEqual(LocalDate newReservationDate, LocalDate oldReservationDate) {
        return newReservationDate.isEqual(oldReservationDate);
    }

    /**
     * Deletes reservation by id
     *
     * @param id that belongs to the reservation to delete
     */
    public void delete(Integer id) {
        try {
            reservationRepository.deleteById(id);
        } catch (NonExistentReservationForDeleteException ex) {
            throw new NonExistentReservationForDeleteException();
        }
    }

    /**
     * Validates if the guests already exists, if no then creates new one(s)
     *
     * @param guests set of reservation guests
     */
    private void doGuestExistInRecords(Set<Guest> guests) {
        for (Guest guest : guests) {
            // check if guest has same first name, last name, email
            if (!guestService.guestExists(guest)) {
                guestService.create(guest);
            } else {
                Integer existingId = guestService.findByExistingGuestId(
                        guest.getFirstName(), guest.getLastName(), guest.getEmail());
                guest.setId(existingId);
            }
        }
    }

    /**
     * Looks for a reservation by id
     *
     * @param bookingId the reservation unique identifier
     * @return the corresponding reservation object
     */
    private Reservation getReservation(Integer bookingId) {
        return reservationRepository.findById(bookingId)
                .orElseThrow(() -> new ReservationNotFoundException(bookingId));
    }

    /**
     * Checks if the arrival/departure dates changed. If so,checks the availability again to update them if possible
     *
     * @param reservation    the new reservation
     * @param oldReservation the old reservation
     */
    private void validateChangesInDateRange(Reservation reservation, Reservation oldReservation) {
        if (!validateReservationDatesAreEqual(reservation.getArrival(), oldReservation.getArrival())) {
            // Update the old reservation ARRIVAL date
            if (!availabilityService.isReservationDateRangeAvailable(
                    reservation.getArrival(), reservation.getDeparture())) throw new InvalidDateRangeException();

            // Then lock the dates for the new date range and unlock the old so the change can be persisted
            oldReservation.setArrival(reservation.getArrival());
        }
        if (!validateReservationDatesAreEqual(reservation.getDeparture(), oldReservation.getDeparture())) {
            // Update the old reservation DEPARTURE date
            if (!availabilityService.isReservationDateRangeAvailable(
                    oldReservation.getArrival(), reservation.getDeparture())) throw new InvalidDateRangeException();
            oldReservation.setDeparture(reservation.getDeparture());
        }
    }
}
