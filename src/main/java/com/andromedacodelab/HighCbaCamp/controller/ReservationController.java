package com.andromedacodelab.HighCbaCamp.controller;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.service.ReservationService;
import com.andromedacodelab.HighCbaCamp.util.CampApiUtility;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.YEAR_MONTH_DAY;

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

        DateFormat formatter = new SimpleDateFormat(YEAR_MONTH_DAY);
        Date dateArrival = new Date();
        Date dateDeparture = new Date();
        try {
             dateArrival = formatter.parse(arrival);
             dateDeparture = formatter.parse(departure);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("A problem occurred on our end");
        }

        Reservation reservation = reservationService.createReservation(email, firstName, lastName,
                CampApiUtility.convertToLocalDateTime(dateArrival),
                CampApiUtility.convertToLocalDateTime(dateDeparture),
                convertListToSet(guests));

        return ResponseEntity.ok("Reservation created successfully with bookingId: " + reservation.getBookingId());
    }

    private Set<Guest> convertListToSet(List<Map<String, String>> guests) {
        Set<Guest> guestSet = new HashSet<>();

        for (Map<String, String> map : guests) {
            Guest guest = new Guest();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                switch (entry.getKey()) {
                    case "firstName":
                        guest.setFirstName(entry.getValue());
                        break;
                    case "lastName":
                        guest.setLastName(entry.getValue());
                        break;
                    case "email":
                        guest.setEmail(entry.getValue());
                        break;
                }
                guest.setReservationHolder(false);
            }
            guestSet.add(guest);
        }
        return guestSet;
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

    @PutMapping(path = "/update/status")
    @ResponseBody
    public ResponseEntity<Object> updateReservationStatus(@RequestBody JSONObject request) {
        int bookingId = Integer.parseInt(request.get("bookingId").toString());
        int newStatusId = Integer.parseInt(request.get("statusId").toString());

        return ResponseEntity.ok("Reservation with bookingId " + bookingId + " status was updated to: " +
                reservationService.updateReservationStatus(bookingId, newStatusId).getStatus().getName());
    }
}
