package com.andromedacodelab.HighCbaCamp.controller;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/hello")
    public String getGreeting() {
        return "Hello HighCbaCamp!";
    }

    @GetMapping(path = "/{bookingId}")
    public Reservation findReservationByBookingId(@PathVariable(value = "bookingId") int bookingId) {
         return  reservationService.findReservationByBookingId(bookingId);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<String> createReservation(String email, String firstName, String lastName,
                                         LocalDateTime arrival, LocalDateTime departure,
                                         Set<Guest> companions) {
        Reservation reservation = reservationService.createReservation(email, firstName, lastName, arrival, departure, companions);
        return ResponseEntity.ok("Reservation created succesfully with bookingId: " + reservation.getBookingId());
    }

    @PutMapping(path = "/update")
    public ResponseEntity<Reservation> updateReservation(@RequestBody Reservation reservation, int bookingId) {

        Reservation updatedReservation = reservationService.updateReservation(reservation, bookingId);
        return ResponseEntity.ok(updatedReservation);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<Object> deleteReservation(int id) {
        reservationService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
