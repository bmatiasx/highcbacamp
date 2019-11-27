package com.andromedacodelab.HighCbaCamp.controller;

import com.andromedacodelab.HighCbaCamp.service.AvailabilityService;
import com.andromedacodelab.HighCbaCamp.util.CampApiUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {
    private AvailabilityService availabilityService;

    @Autowired
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> checkAvailabilityForDates(@RequestParam(value = "start", required = false)
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE,
                                                            pattern = "yyyy-MM-dd")
                                                            Date startDate,
                                                         @RequestParam(value = "end", required = false)
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE,
                                                            pattern = "yyyy-MM-dd")
                                                            Date endDate) {

        LocalDateTime start;
        LocalDateTime end;
        if (startDate == null || endDate == null) {
            /* Use the default date range which is 1 month*/
            start = LocalDateTime.now();
            end = start.plusMonths(1);
        } else {
            start = CampApiUtility.convertToLocalDateTime(startDate);
            end = CampApiUtility.convertToLocalDateTime(endDate);
        }

        if (availabilityService.isReservationAvailable(start, end)) {
            return new ResponseEntity<>(
                    CampApiUtility.availabilityResponseMessage("The date range [arrival: " +
                            start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                            ", departure: " + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "] is available!"),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(CampApiUtility.availabilityResponseMessage("The date range [arrival: " +
                    start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                    ", departure: " + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "] is not available")
                    , HttpStatus.CONFLICT);
        }
    }
}
