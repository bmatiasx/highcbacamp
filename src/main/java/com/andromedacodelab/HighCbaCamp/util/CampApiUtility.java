package com.andromedacodelab.HighCbaCamp.util;

import com.andromedacodelab.HighCbaCamp.model.Reservation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class CampApiUtility {
    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static boolean doesReservationDatesOverlap(LocalDateTime start, LocalDateTime end, Reservation reservation) {
        return (start.isEqual(reservation.getArrival())
                && end.plusHours(23).plusMinutes(59).isEqual(reservation.getDeparture()));
    }
}
