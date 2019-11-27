package com.andromedacodelab.HighCbaCamp.util;

import com.andromedacodelab.HighCbaCamp.model.Reservation;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CampApiUtility {
    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static boolean doesReservationDatesOverlap(@NotNull LocalDateTime start, @NotNull LocalDateTime end,
                                                      Reservation reservation) {
        // case 1
        return (start.isEqual(reservation.getArrival()) && addWholeDay(end).isEqual(reservation.getDeparture())) ||
                // case 2
                (start.isEqual(reservation.getArrival()) && isBetweenDates(start, end, reservation.getDeparture())) ||
                // case 3
                (isBetweenDates(start, end, reservation.getArrival())) &&
                        addWholeDay(end).isEqual(reservation.getDeparture()) ||
                // case 4
                (start.isBefore(reservation.getArrival()) && addWholeDay(end).isAfter(reservation.getDeparture())) ||
                // case 5
                (isBetweenDates(start, end, reservation.getArrival()) &&
                        isBetweenDates(reservation.getArrival(), reservation.getDeparture(), addWholeDay(end))) ||
                // case 6
                (isBetweenDates(start, reservation.getArrival(), reservation.getDeparture()) &&
                        isBetweenDates(reservation.getArrival(), start, end)) ||
                // case 7
                (start.isAfter(reservation.getArrival()) && addWholeDay(end).isBefore(reservation.getDeparture()));
    }

    public static Map<String, String> availabilityResponseMessage(String value) {
        HashMap<String, String> map = new HashMap<>();
        map.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")));
        map.put("message", value);
        return map;
    }

    public static boolean validateDates(@NotNull LocalDateTime start, @NotNull LocalDateTime end) {
        return start.isBefore(end);
    }

    public static boolean isBetweenDates(LocalDateTime start, LocalDateTime end, LocalDateTime target) {
        return !(target.isBefore(start) || target.isAfter(end));
    }

    public static LocalDateTime addWholeDay(LocalDateTime endDay) {
        return endDay.plusHours(23).plusMinutes(59);
    }
}
