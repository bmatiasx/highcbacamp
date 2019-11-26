package com.andromedacodelab.HighCbaCamp.controller;

import com.andromedacodelab.HighCbaCamp.service.AvailabilityService;
import com.andromedacodelab.HighCbaCamp.util.CampApiUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
    public ResponseEntity<String> checkAvailabilityForDates(@RequestParam("start")
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE,
                                                            pattern = "yyyy-MM-dd")
                                                            Date startDate,
                                                            @RequestParam("end")
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE,
                                                            pattern = "yyyy-MM-dd")
                                                            Date endDate) {

        LocalDateTime start = CampApiUtility.convertToLocalDateTime(startDate);
        LocalDateTime end = CampApiUtility.convertToLocalDateTime(endDate);
        boolean isDateRangeAvailable = availabilityService.isReservationAvailable(start, end);
        if (isDateRangeAvailable) {
            return ResponseEntity.ok().body("The date range is available!");
        } else {
            return ResponseEntity.ok("The date range [startDate: " +
                    start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                    ", endDate: " + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "] is not available");
        }
    }
}
