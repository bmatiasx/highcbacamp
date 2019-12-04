package com.andromedacodelab.HighCbaCamp;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.ReservationStatus;
import com.andromedacodelab.HighCbaCamp.model.builder.GuestBuilder;
import com.andromedacodelab.HighCbaCamp.model.builder.ReservationBuilder;
import com.andromedacodelab.HighCbaCamp.util.CampApiUtil;
import com.andromedacodelab.HighCbaCamp.util.ReservationWrapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.andromedacodelab.HighCbaCamp.util.CampApiUtil.customParseStringToLocalDateTime;

public class TestUtil {
    public static Reservation createReservationMock() {
        String arrival = "2020-05-10";
        String departure = "2020-05-13";
        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Christian").withLastName("Cornell")
                .withEmail("chcornell@hotmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Zach").withLastName("De la Rocha")
                .withEmail("zach@hotmail.com").withIsReservationHolder(false).build();

        ReservationStatus status = new ReservationStatus("CONFIRMED");

        Set<Guest> guests = new HashSet<>(Arrays.asList(guest1, guest2));

        return new ReservationBuilder()
                .withBookingId(1)
                .withArrivalDate(customParseStringToLocalDateTime(arrival))
                .withDepartureDate(customParseStringToLocalDateTime(departure))
                .withStatus(status)
                .withGuests(guests)
                .build();
    }

    public static ReservationWrapper createNewReservationWrapper(String arrivalDate, String departureDate, Set<Guest> guests) {
        LocalDateTime arrival = CampApiUtil.customParseStringToLocalDateTime(arrivalDate);
        LocalDateTime departure = CampApiUtil.customParseStringToLocalDateTime(departureDate);

        ReservationStatus status = new ReservationStatus(2, "CONFIRMED");

        return new ReservationWrapper(null, arrival, departure, guests, status.getName());
    }

    public static Reservation createNewReservationBasedInWrapper(ReservationWrapper wrapper) {
        return new ReservationBuilder()
                .withArrivalDate(wrapper.getArrival())
                .withDepartureDate(wrapper.getDeparture())
                .withGuests(wrapper.getGuests())
                .build();
    }

    public static String parseFileToJson(String path) {
        String object = "";
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(path)) {
            object = jsonParser.parse(reader).toString();
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }

        return object;
    }
}
