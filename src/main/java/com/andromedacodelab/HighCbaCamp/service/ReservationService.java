package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.builder.GuestBuilder;
import com.andromedacodelab.HighCbaCamp.model.builder.ReservationBuilder;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository;
    private GuestService guestService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, GuestService guestService) {
        this.reservationRepository = reservationRepository;
        this.guestService = guestService;
    }

    /**
     * This method is intended for search a Reservation by its id
     * @param bookingId the reservation unique id.
     * @return Reservation object with all details
     */
    public Reservation findReservationByBookingId(int bookingId)  {
        return reservationRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "The reservation with bookingId " + bookingId + " is not found"
                ));
    }

    /**
     * This method is intended for create a Reservation
     * @param email Represents the e-mail of the user who is the reservation holder
     * @param firstName is the first name of the reservation holder
     * @param lastName is the last name of the reservation holder
     * @param arrival represents the starting day of the reservation
     * @param departure represents the ending day of the reservation
     * @param guests the guest companions who are not reservation holders
     * @return Reservation object with all details
     */
    public Reservation createReservation(String email, String firstName, String lastName,
                                         LocalDateTime arrival, LocalDateTime departure,
                                         Set<Guest> guests) {
        Guest reservationHolderGuest = new GuestBuilder().withFirstName(firstName).withLastName(lastName)
                .withEmail(email).withIsReservationHolder(true).build();
        guests.add(reservationHolderGuest);

        // TODO check if the guests already exist in the DB, if not create them with GuestService
        if (!doGuestExistInRecords(guests)) {
            // add new guests to GUESTS table
        }

        // TODO check if the provided date range is available to create a reservation
        // TODO validate if the reservation is made one day ahead of selected start date
        // TODO validate if the reservation does not lasts more than one month
        Reservation reservation = new ReservationBuilder().withArrivalDate(arrival)
                .withDepartureDate(departure).withCompanions(guests).build();
        try {
            reservationRepository.save(reservation);
        } catch (Exception ex) {
            throw new RuntimeException("Reservation could not be created");
        }
        return reservation;
    }

    public Reservation updateReservation(Reservation reservation, int bookingId) {
        // TODO validate if the guests are all the same. If not remove/add the existing using
        // the updateGuests() method

        return new Reservation();
    }

    public void delete(int id) {
        reservationRepository.deleteById(id);
    }

    public void doGuestExistInRecords(Set<Guest> guests) {
        for (Guest guest : guests) {
            guestService.findGuest(guest);
            // check if guest has same first name, last name, email

        }
    }

    private void updateGuests(Set<Guest> newGuests, Set<Guest> oldGuests) {

    }
}
