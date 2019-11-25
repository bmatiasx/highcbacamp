package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.exception.ApiException;
import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.builder.GuestBuilder;
import com.andromedacodelab.HighCbaCamp.model.builder.ReservationBuilder;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation findReservationByBookingId(int bookingId)  {
        return reservationRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "The reservation with bookingId " + bookingId + " is not found"
                ));
    }

    public Reservation createReservation(String email, String firstName, String lastName,
                                         LocalDateTime arrival, LocalDateTime departure,
                                         Set<Guest> companions) {
        Guest guest = new GuestBuilder().withFirstName(firstName).withLastName(lastName)
                .withEmail(email).withIsReservationHolder(true).build();
        companions.add(guest);
        // TODO check if the guests already exist in the DB, if not create them with GuestService
        // TODO check if the provided date range is available to create a reservation
        // TODO validate if the reservation is made one day ahead of selected start date
        // TODO validate if the reservation does not lasts more than one month
        Reservation reservation = new ReservationBuilder().withArrivalDate(arrival)
                .withDepartureDate(departure).withCompanions(companions).build();
        try {
            reservationRepository.save(reservation);
        } catch (Exception ex) {
            throw new RuntimeException("Reservation could not be created");
        }
        return reservation;
    }

    public Reservation updateReservation(Reservation reservation, int bookingId) {

        // TODO validate if the guests are all the same. If not remove/add the existing.

        return new Reservation();
    }

    public void delete(int id) {
        reservationRepository.deleteById(id);
    }

    private void updateGuests(Set<Guest> newGuests, Set<Guest> oldGuests) {

    }
}
