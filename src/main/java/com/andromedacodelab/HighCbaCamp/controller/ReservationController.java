package com.andromedacodelab.HighCbaCamp.controller;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.service.ReservationService;
import com.andromedacodelab.HighCbaCamp.util.CampApiUtility;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @ResponseBody
    public ResponseEntity<String> createReservation(@RequestBody JSONObject request) {

        String arrival = request.get("arrival").toString();
        String departure = request.get("departure").toString();
        String firstName = request.get("firstName").toString();
        String lastName = request.get("lastName").toString();
        String email = request.get("email").toString();
        List guests = (List) request.get("guests");

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date dateArrival = new Date();
        Date dateDeparture = new Date();
        try {
             dateArrival = formatter.parse(arrival);
             dateDeparture = formatter.parse(departure);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Reservation reservation = reservationService.createReservation(email, firstName, lastName,
                CampApiUtility.convertToLocalDateTime(dateArrival),
                CampApiUtility.convertToLocalDateTime(dateDeparture),
                convertListToSet(guests));

        return ResponseEntity.ok("Reservation created successfully with bookingId: " + reservation.getBookingId());
    }

    private Set<Guest> convertListToSet(List<?> guests) {
        return guests.stream()
                .filter(Guest.class::isInstance)
                .map(Guest.class::cast)
                .collect(Collectors.toSet());
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

    /*@PutMapping(path = "/update-status")
    public ResponseEntity<Object> updateReservationStatus() {
        return ResponseEntity.ok();
    }*/
}
