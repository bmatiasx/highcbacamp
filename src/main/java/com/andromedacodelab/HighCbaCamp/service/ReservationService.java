package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.exception.InvalidDateRangeException;
import com.andromedacodelab.HighCbaCamp.exception.InvalidReservationStatusException;
import com.andromedacodelab.HighCbaCamp.exception.ReservationNotFoundException;
import com.andromedacodelab.HighCbaCamp.exception.ReservationOutOfTermException;
import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.ReservationStatus;
import com.andromedacodelab.HighCbaCamp.model.builder.GuestBuilder;
import com.andromedacodelab.HighCbaCamp.model.builder.ReservationBuilder;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import com.andromedacodelab.HighCbaCamp.repository.ReservationStatusesRepository;
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
    public Reservation findReservationByBookingId(int bookingId) {
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
        ReservationStatus status = reservationStatusesRepository.getOne(2);

        Reservation reservation = new ReservationBuilder().withArrivalDate(arrival)
                .withDepartureDate(departure).withStatus(status).withCompanions(guests).build();
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
     * @param reservation
     * @param bookingId
     * @return the reservation that was updated with its new state
     */
    public Reservation updateReservation(Reservation reservation, int bookingId) {
        /* Creates the guests that don't exist in the DB*/
        doGuestExistInRecords(reservation.getGuests());
        Reservation oldReservation = getReservation(bookingId);

        /* Removes guests that don't belong to the reservation anymore*/
        removeGuestsIfAny(reservation, oldReservation);

        if (IntStream.range(1, 4).noneMatch(s -> s == reservation.getStatus().getId())) {
            throw new InvalidReservationStatusException();
        } else if (!reservation.getStatus().equals(oldReservation.getStatus())) {
            oldReservation.setStatus(reservation.getStatus());
        }

        /* Validates if the date range is the same than the persisted reservation*/
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

        return reservationRepository.save(oldReservation);
    }

    /**
     *
     * @param reservation
     * @param oldReservation
     */
    private void removeGuestsIfAny(Reservation reservation, Reservation oldReservation) {
        // traverse the old reservation guests and if any of them don't exists in the new remove the element(s)
        for (Guest oldGuest : oldReservation.getGuests()) {
            if (!reservation.getGuests().contains(oldGuest)) {
                oldReservation.getGuests().remove(oldGuest);
            }
        }
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
     *
     * @param id
     */
    public void delete(int id) {
        reservationRepository.deleteById(id);
    }

    /**
     * @param guests
     */
    public void doGuestExistInRecords(Set<Guest> guests) {
        guests.stream().filter(g -> (!guestService.guestExists(g))).forEach(guest -> guestService.create(guest));
    }

    /**
     *
     * @param bookingId
     * @return
     */
    private Reservation getReservation(int bookingId) {
        return reservationRepository.findById(bookingId)
                .orElseThrow(() -> new ReservationNotFoundException(bookingId));
    }

    public Reservation updateReservationStatus(int bookingId, int newStatusId) {
        Reservation oldReservation = getReservation(bookingId);

        ReservationStatus newReservationStatus = reservationStatusesRepository.getOne(newStatusId);

        if (IntStream.range(1, 4).noneMatch(s -> s == newStatusId)) throw new InvalidReservationStatusException();

        oldReservation.setStatus(newReservationStatus);

        return reservationRepository.save(oldReservation);
    }
}
