package com.andromedacodelab.HighCbaCamp.controller;

import com.andromedacodelab.HighCbaCamp.exception.ReservationNotFoundException;
import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.builder.GuestBuilder;
import com.andromedacodelab.HighCbaCamp.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/reservation")
@Validated
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
    public Reservation findReservationByBookingId(@PathVariable(value = "bookingId") @Valid int bookingId) {
         return  reservationService.findReservationByBookingId(bookingId);
    }

    @PostMapping(path = "/create")
    public Reservation createReservation(String email, String firstName, String lastName,
                                         LocalDateTime arrival, LocalDateTime departure,
                                         Set<Guest> companions) {
        return reservationService.createReservation(email, firstName, lastName, arrival, departure, companions);
    }
}
