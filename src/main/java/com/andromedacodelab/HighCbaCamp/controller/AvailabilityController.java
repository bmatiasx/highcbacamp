package com.andromedacodelab.HighCbaCamp.controller;

import com.andromedacodelab.HighCbaCamp.service.AvailabilityService;
import com.andromedacodelab.HighCbaCamp.util.CampApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

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
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                            LocalDate startDate,
                                                            @RequestParam(value = "end", required = false)
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                            LocalDate  endDate) {

        if (startDate == null && endDate == null) {
            /* Use the default date range which is 1 month*/
            startDate = LocalDate.now();
            endDate = startDate.plusMonths(1);
        } else if (endDate == null){
            endDate = startDate.plusMonths(1);
        } else if (startDate == null) {
            startDate = endDate.minusMonths(1);
        }

        if (availabilityService.isReservationDateRangeAvailable(startDate, endDate)) {
            return new ResponseEntity<>(
                    CampApiUtil.availabilityResponseMessage("The date range [arrival: " + startDate +
                            ", departure: " + endDate + "] is available!"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(CampApiUtil.availabilityResponseMessage("The date range [arrival: " +
                    startDate + ", departure: " + endDate + "] is not available"), HttpStatus.CONFLICT);
        }
    }
}
