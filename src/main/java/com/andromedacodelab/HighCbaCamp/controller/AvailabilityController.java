package com.andromedacodelab.HighCbaCamp.controller;

import com.andromedacodelab.HighCbaCamp.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private AvailabilityService availabilityService;

    @Autowired
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    // Check https://www.baeldung.com/spring-date-parameters
    @PostMapping(path = "/")
    public ResponseEntity<String> checkAvailabilityForDates(@RequestParam(name = "start")
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME,
                                            pattern = "yyyy-MM-dd hh:mm:ss a") LocalDateTime startDate,
                                                           @RequestParam(name = "end")
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME,
                                            pattern = "yyyy-MM-dd hh:mm:ss a") LocalDateTime endDate) {
        boolean isDateRangeAvailable = availabilityService.isReservationAvailable(startDate, endDate);
        if (isDateRangeAvailable) {
            // TODO reformat to return a json properly formed message
            // see: https://www.baeldung.com/spring-boot-json
            return ResponseEntity.ok("The date range is available");
        } else {
            return ResponseEntity.ok("The date range [{startDate " + startDate + "}, {endDate " + endDate + "}]");
        }
    }
}
