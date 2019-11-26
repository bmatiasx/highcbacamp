package com.andromedacodelab.HighCbaCamp.controller;

import com.andromedacodelab.HighCbaCamp.service.AvailabilityService;
import com.andromedacodelab.HighCbaCamp.util.CampApiUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> checkAvailabilityForDates(@RequestParam("start")
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

        // https://stackoverflow.com/questions/40765010/how-to-return-json-with-multiple-properties-by-using-responseentity-in-spring-re

        if (isDateRangeAvailable) {
            /*return ResponseEntity.ok().body("The date range is available!");*/
            /*return CampApiUtility.availabilityResponseMessage("The date range is available!");*/
            return new ResponseEntity<Map<String, String>>(
                    CampApiUtility.availabilityResponseMessage("The date range is available!"), HttpStatus.OK);
        } else {
            /*return ResponseEntity.ok("The date range [startDate: " +
                    start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                    ", endDate: " + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "] is not available");*/

            /*return CampApiUtility.availabilityResponseMessage("The date range [startDate: " +
                    start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                    ", endDate: " + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "] is not available");*/
            return new ResponseEntity<Map<String, String>>(
                    CampApiUtility.availabilityResponseMessage("The date range [startDate: " +
                            start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                            ", endDate: " + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "] is not available"), HttpStatus.CONFLICT);
        }
    }
}
