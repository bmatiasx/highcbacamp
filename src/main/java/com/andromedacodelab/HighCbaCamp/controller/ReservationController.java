package com.andromedacodelab.HighCbaCamp.controller;

import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.service.ReservationService;
import com.andromedacodelab.HighCbaCamp.util.ReservationWrapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.andromedacodelab.HighCbaCamp.util.CampApiUtil.extractReservationFromPutRequest;

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

    @GetMapping
    public Reservation findReservationByBookingId(@RequestParam("bookingId") Integer bookingId) {
        return  reservationService.findReservationByBookingId(bookingId);
    }

    @PostMapping(path = "/create")
    @ResponseBody
    public ResponseEntity<String> createReservation(@RequestBody JSONObject request) {

        ReservationWrapper reservationWrapper = extractReservationFromPutRequest(request, true);
        Reservation reservation = reservationService.createReservation(reservationWrapper);

        return ResponseEntity.ok("Reservation created successfully with bookingId: " + reservation.getBookingId());
    }

    @PutMapping(path = "/update")
    public ResponseEntity<String> updateReservation(@RequestBody JSONObject request) {

        ReservationWrapper newReservation = extractReservationFromPutRequest(request, false);
        Reservation updatedReservation = reservationService.updateReservation(newReservation);

        return ResponseEntity.ok("Updated reservation with bookingId: " + updatedReservation.getBookingId());
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<Object> deleteReservation(@RequestParam("bookingId") Integer bookingId) {
        reservationService.delete(bookingId);

        return ResponseEntity.ok("Deleted reservation with bookingId: " + bookingId);
    }

    @PutMapping(path = "/update/status")
    @ResponseBody
    public ResponseEntity<Object> updateReservationStatus(@RequestBody JSONObject request) {
        Integer bookingId = Integer.parseInt(request.get("bookingId").toString());
        Integer newStatusId = Integer.parseInt(request.get("statusId").toString());

        return ResponseEntity.ok("Reservation with bookingId " + bookingId + " status was updated to: " +
                reservationService.updateReservationStatus(bookingId, newStatusId).getStatus().getName());
    }
}
