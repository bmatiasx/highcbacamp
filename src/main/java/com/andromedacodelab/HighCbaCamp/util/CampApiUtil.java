package com.andromedacodelab.HighCbaCamp.util;

import com.andromedacodelab.HighCbaCamp.exception.DateFormatIsInvalidException;
import com.andromedacodelab.HighCbaCamp.exception.DateRangeNotAcceptedException;
import com.andromedacodelab.HighCbaCamp.exception.DateRangeNotAvailableException;
import com.andromedacodelab.HighCbaCamp.exception.InvalidDateRangeException;
import com.andromedacodelab.HighCbaCamp.exception.ParamsMissingException;
import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.builder.GuestBuilder;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.ARRIVAL_PARAM_MISSING_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.BOOKING_ID_PARAM_MISSING_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.DEPARTURE_PARAM_MISSING_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.GUEST_PARAM_MISSING_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.STATUS_PARAM_MISSING_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.YEAR_MONTH_DAY;

public class CampApiUtil {
    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static LocalDateTime customParseStringToLocalDateTime(String dateToConvert) {
        DateFormat formatter = new SimpleDateFormat(YEAR_MONTH_DAY);
        Date date = new Date();
        try {
            date = formatter.parse(dateToConvert);
        } catch (ParseException e) {
            throw new DateFormatIsInvalidException();
        }

        return convertToLocalDateTime(date);
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

    public static LocalDateTime addWholeDayInHours(LocalDateTime endDay) {
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

    public static ReservationWrapper extractReservationFromPutRequest(JSONObject request, boolean isCreate) {
        ReservationWrapper reservationWrapper;
        List<String> paramListForValidate = new ArrayList<>();

        String arrival = request.get("arrival").toString();
        String departure = request.get("departure").toString();
        List guests = (List) request.get("guests");

        if(isCreate) {
            String firstName = request.get("firstName").toString();
            String lastName = request.get("lastName").toString();
            String email = request.get("email").toString();

            validateRequestParameters(arrival, departure, guests, paramListForValidate);

            Guest reservationHolder = new GuestBuilder().withFirstName(firstName).withLastName(lastName)
                    .withEmail(email).withIsReservationHolder(true).build();
            Set<Guest> guestSet = convertListToSet(guests);
            guestSet.add(reservationHolder);

            reservationWrapper = new ReservationWrapper(null,
                    customParseStringToLocalDateTime(arrival),
                    customParseStringToLocalDateTime(departure),
                    guestSet, "");
        } else {
            String bookingId = request.get("bookingId").toString();
            String statusName = request.get("status").toString();

            validateRequestParameters(arrival, departure, guests, bookingId, statusName, paramListForValidate);

            reservationWrapper =  new ReservationWrapper(
                    Integer.parseInt(bookingId),
                    customParseStringToLocalDateTime(arrival),
                    customParseStringToLocalDateTime(departure),
                    convertListToSet(guests),
                    statusName);
        }

        return reservationWrapper;
    }

    public static void validateRequestParameters(String arrival, String departure, List guests, List<String> parameters) throws ParamsMissingException {
        if (arrival.isEmpty()) parameters.add(ARRIVAL_PARAM_MISSING_MESSAGE);
        if (departure.isEmpty()) parameters.add(DEPARTURE_PARAM_MISSING_MESSAGE);

        if (parameters.size() > 0) throw new ParamsMissingException(parameters);
    }

    public static void validateRequestParameters(String arrival, String departure, List guests, String bookingId,
                                                  String statusName, List<String> parameters) throws ParamsMissingException{
        if (bookingId.isEmpty()) parameters.add(BOOKING_ID_PARAM_MISSING_MESSAGE);
        if (statusName.isEmpty()) parameters.add(STATUS_PARAM_MISSING_MESSAGE);
        validateRequestParameters(arrival, departure, guests, parameters);
    }
}
