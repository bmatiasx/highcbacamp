package com.andromedacodelab.HighCbaCamp.util;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CampApiUtility {
    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static Map<String, String> availabilityResponseMessage(String value) {
        HashMap<String, String> map = new HashMap<>();
        map.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")));
        map.put("message", value);
        return map;
    }

    public static boolean validateArrivalIsBeforeDeparture(@NotNull LocalDateTime start, @NotNull LocalDateTime end) {
        return start.isBefore(end);
    }

    public static boolean isBetweenDates(LocalDateTime start, LocalDateTime end, LocalDateTime target) {
        return !(target.isBefore(start) || target.isAfter(end));
    }

    public static LocalDateTime addWholeDay(LocalDateTime endDay) {
        return endDay.plusHours(23).plusMinutes(59);
    }


    public static Set<Guest> convertListToSet(List<Map<String, String>> guests) {
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
}
