package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.exception.InvalidDateRangeException;
import com.andromedacodelab.HighCbaCamp.exception.InvalidReservationStatusException;
import com.andromedacodelab.HighCbaCamp.exception.ReservationCancelledException;
import com.andromedacodelab.HighCbaCamp.exception.ReservationNotFoundException;
import com.andromedacodelab.HighCbaCamp.exception.ReservationOutOfTermException;
import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.ReservationStatus;
import com.andromedacodelab.HighCbaCamp.model.builder.GuestBuilder;
import com.andromedacodelab.HighCbaCamp.model.builder.ReservationBuilder;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import com.andromedacodelab.HighCbaCamp.repository.ReservationStatusesRepository;
import com.andromedacodelab.HighCbaCamp.util.ReservationWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository;
    private ReservationStatusesRepository reservationStatusesRepository;
    private GuestService guestService;
    private AvailabilityService availabilityService;
    private static final Integer PENDING_STATUS = 1;
    private static final Integer CONFIRMED_STATUS = 2;
    private static final Integer ARRIVED_STATUS = 3;
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
        return getReservation(bookingId);
    }

    /**
     * This method is intended for create a Reservation
     *
     * @param email     Represents the e-mail of the user who is the reservation holder
     * @param firstName is the first name of the reservation holder
     * @param lastName  is the last name of the reservation holder
     * @param arrival   represents the starting day of the reservation
     * @param departure represents the ending day of the reservation
     * @param guests    the guest companions who are not reservation holders
     * @return Reservation object with all details
     */
    public Reservation createReservation(String email, String firstName, String lastName,
                                         LocalDateTime arrival, LocalDateTime departure,
                                         Set<Guest> guests) {
        /* Checks if the provided date range is available to create a reservation*/
        if (!availabilityService.isReservationDateRangeAvailable(arrival, departure)) {
            throw new InvalidDateRangeException();
        }

        validateDateRangeConstraints(arrival, departure);

        Guest reservationHolderGuest = new GuestBuilder().withFirstName(firstName).withLastName(lastName)
                .withEmail(email).withIsReservationHolder(true).build();
        guests.add(reservationHolderGuest);

        /* Check if the guests already exist in the DB, if not create them with GuestService*/
        doGuestExistInRecords(guests);

        /* Reservation status id 2 = CONFIRMED*/
        ReservationStatus status = reservationStatusesRepository.getOne(CONFIRMED_STATUS);

        Reservation reservation = new ReservationBuilder().withArrivalDate(arrival)
                .withDepartureDate(departure).withStatus(status).withGuests(guests).build();
        try {
            reservationRepository.save(reservation);
        } catch (Exception ex) {
            throw new RuntimeException("Reservation could not be created");
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

        /* Creates the guests that don't exist in the DB*/
        doGuestExistInRecords(reservation.getGuests());

        Reservation oldReservation = getReservation(reservation.getBookingId());

        if (CANCELLED_STATUS.equals(oldReservation.getStatus().getId())) {
            throw new ReservationCancelledException();
        }

        /* Removes guests that don't belong to the reservation anymore*/
        oldReservation.setGuests(reservation.getGuests());

        // TODO look for the status name in the reservation_statuses table and return the ID to be assigned to the new reservation


        if (IntStream.range(1, 4).noneMatch(s -> s == reservation.getStatus().getId())) {
            throw new InvalidReservationStatusException();
        } else if (!reservation.getStatus().equals(oldReservation.getStatus())) {
            oldReservation.setStatus(reservation.getStatus());
        }

        /* Validates if the date range is the same than the persisted reservation*/
        validateChangesInDateRange(reservation, oldReservation);

        return reservationRepository.save(oldReservation);
    }

    public Reservation updateReservationStatus(Integer bookingId, Integer newStatusId) {
        Reservation oldReservation = getReservation(bookingId);

        ReservationStatus newReservationStatus = reservationStatusesRepository.getOne(newStatusId);

        if (IntStream.range(1, 4).noneMatch(s -> s == newStatusId)) throw new InvalidReservationStatusException();

        oldReservation.setStatus(newReservationStatus);

        return reservationRepository.save(oldReservation);
    }

    /**
     * Validates the following scenarios:
     * 1. The campsite can be reserved for a maximum of 3 days
     * 2. The campsite can be reserved for a minimum of 1 day ahead of arrival and up to 1 month in advance
     *
     * @param arrival
     * @param departure
     */
    private void validateDateRangeConstraints(LocalDateTime arrival, LocalDateTime departure) {
        LocalDate now = LocalDate.now();

        if ((arrival.toLocalDate().isEqual(now.minusDays(1)) || arrival.toLocalDate().isBefore(now.minusDays(1)))
                && (arrival.toLocalDate().isEqual(now.minusMonths(1)) || arrival.toLocalDate().isBefore(now.minusMonths(1)))) {
            throw new ReservationOutOfTermException();
        }
    }

    /**
     * Checks two reservation dates are the same ignoring hours of day.
     *
     * @param newReservationDate
     * @param oldReservationDate
     * @return true if the dates are equal
     */
    private boolean validateReservationDatesAreEqual(LocalDateTime newReservationDate, LocalDateTime oldReservationDate) {
        return newReservationDate.toLocalDate().isEqual(oldReservationDate.toLocalDate());
    }

    /**
     * Deletes reservation by id
     * @param id that belongs to the reservation to delete
     */
    public void delete(Integer id) {
        reservationRepository.deleteById(id);
    }

    /**
     * Validates if the guests already exists, if no then creates new one(s)
     * @param guests set of reservation guests
     */
    public void doGuestExistInRecords(Set<Guest> guests) {
        guests.stream().filter(g -> (!guestService.guestExists(g))).forEach(guest -> guestService.create(guest));
    }

    /**
     * Looks for a reservation by id
     * @param bookingId the reservation unique identifier
     * @return the corresponding reservation object
     */
    private Reservation getReservation(Integer bookingId) {
        return reservationRepository.findById(bookingId)
                .orElseThrow(() -> new ReservationNotFoundException(bookingId));
    }

    /**
     *  Checks if the arrival/departure dates changed. If so,checks the availability again to update them if possible
     * @param reservation the new reservation
     * @param oldReservation the old reservation
     */
    private void validateChangesInDateRange(Reservation reservation, Reservation oldReservation) {
        if (!validateReservationDatesAreEqual(reservation.getArrival(), oldReservation.getArrival())) {
            /* Update the old reservation ARRIVAL date*/
            if (!availabilityService.isReservationDateRangeAvailable(
                    reservation.getArrival(), oldReservation.getDeparture())) throw new InvalidDateRangeException();

            /* Then lock the dates for the new date range and unlock the old so the change can be persisted*/
            oldReservation.setArrival(reservation.getArrival());
        }
        if (validateReservationDatesAreEqual(reservation.getDeparture(), oldReservation.getDeparture())) {
            /* Update the old reservation DEPARTURE date*/
            if (!availabilityService.isReservationDateRangeAvailable(
                    oldReservation.getArrival(), reservation.getDeparture())) throw new InvalidDateRangeException();

            oldReservation.setDeparture(reservation.getDeparture());
        }
    }
}
