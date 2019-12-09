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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.andromedacodelab.HighCbaCamp.util.CampApiUtil.convertToLocalDateTime;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.YEAR_MONTH_DAY;

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
                                                            pattern = YEAR_MONTH_DAY)
                                                            Date startDate,
                                                            @RequestParam(value = "end", required = false)
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE,
                                                            pattern = YEAR_MONTH_DAY)
                                                            Date endDate) {

        LocalDateTime start;
        LocalDateTime end;
        if (startDate == null && endDate == null) {
            /* Use the default date range which is 1 month*/
            start = LocalDateTime.now();
            end = start.plusMonths(1);
        } else if (endDate == null){
            start = convertToLocalDateTime(startDate);
            end = start.plusMonths(1);
        } else if (startDate == null) {
            end = convertToLocalDateTime(endDate);
            start = end.minusMonths(1);
        } else {
            start = convertToLocalDateTime(startDate);
            end = convertToLocalDateTime(endDate);
        }

        if (availabilityService.isReservationDateRangeAvailable(start, end)) {
            return new ResponseEntity<>(
                    CampApiUtil.availabilityResponseMessage("The date range [arrival: " +
                            start.format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY)) +
                            ", departure: " + end.format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY)) + "] is available!"),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(CampApiUtil.availabilityResponseMessage("The date range [arrival: " +
                    start.format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY)) +
                    ", departure: " + end.format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY)) + "] is not available")
                    , HttpStatus.CONFLICT);
        }
    }
}
