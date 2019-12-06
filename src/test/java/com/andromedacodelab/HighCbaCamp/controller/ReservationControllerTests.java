package com.andromedacodelab.HighCbaCamp.controller;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.ReservationStatus;
import com.andromedacodelab.HighCbaCamp.model.builder.GuestBuilder;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import com.andromedacodelab.HighCbaCamp.repository.ReservationStatusesRepository;
import com.andromedacodelab.HighCbaCamp.service.AvailabilityService;
import com.andromedacodelab.HighCbaCamp.service.GuestService;
import com.andromedacodelab.HighCbaCamp.service.ReservationService;
import com.andromedacodelab.HighCbaCamp.util.ReservationWrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.andromedacodelab.HighCbaCamp.TestUtil.createNewReservationBasedInWrapper;
import static com.andromedacodelab.HighCbaCamp.TestUtil.createNewReservationWrapper;
import static com.andromedacodelab.HighCbaCamp.TestUtil.parseFileToJson;
import static com.andromedacodelab.HighCbaCamp.util.CampApiUtil.customParseStringToLocalDateTime;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReservationControllerTests {
    private MockMvc mockMvc;

    @Autowired
    private ReservationController reservationController;

    @MockBean
    private ReservationRepository reservationRepository;

    @Mock
    private AvailabilityService availabilityService;

    @Mock
    private ReservationService reservationService;

    @MockBean
    private GuestService guestService;

    @MockBean
    private ReservationStatusesRepository reservationStatusesRepository;

    private List<Reservation> reservationList;

    private static final String JSON_CREATE_REQUEST_PATH = "src/test/resources/json/" +
            "create-reservation-request.json";
    private static final String JSON_CREATE_INVALID_DATES_REQUEST_PATH =
            "src/test/resources/json/invalid-creation/create-reservation-invalid-dates-request.json";
    private static final String JSON_CREATE_INVALID_OVERLAPPING_DATES_REQUEST_CASE2_PATH =
            "src/test/resources/json/invalid-creation/create-reservation-invalid-dates-case-2.json";
    private static final String JSON_CREATE_INVALID_OVERLAPPING_DATES_REQUEST_CASE3_PATH =
            "src/test/resources/json/invalid-creation/create-reservation-invalid-dates-case-3.json";
    private static final String JSON_CREATE_INVALID_OVERLAPPING_DATES_REQUEST_CASE4_PATH =
            "src/test/resources/json/invalid-creation/create-reservation-invalid-dates-case-4.json";
    private static final String JSON_CREATE_INVALID_OVERLAPPING_DATES_REQUEST_CASE7_PATH =
            "src/test/resources/json/invalid-creation/create-reservation-invalid-dates-case-7.json";
    private static final String JSON_CREATE_INVALID_OVERLAPPING_DATES_REQUEST_CASE8_PATH =
            "src/test/resources/json/invalid-creation/create-reservation-invalid-dates-case-8.json";
    private static final String JSON_CREATE_INVALID_OVERLAPPING_DATES_REQUEST_CASE9_PATH =
            "src/test/resources/json/invalid-creation/create-reservation-invalid-dates-case-9.json";
    private static final String JSON_UPDATE_REQUEST_PATH =
            "src/test/resources/json/update-reservation-status-request.json";
    private static final String JSON_UPDATE_GUESTS_REQUEST_PATH =
            "src/test/resources/json/update-reservation-guests-request.json";
    private static final String JSON_UPDATE_DATES_REQUEST_PATH =
            "src/test/resources/json/update-reservation-dates-request.json";
    private static final String JSON_UPDATE_STATUS_ONLY_REQUEST_PATH =
            "src/test/resources/json/update-reservation-status-only-request.json";

    @Before
    public void setUp() {
        this.mockMvc = standaloneSetup(this.reservationController).build();

        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Matt").withLastName("Damon")
                .withEmail("mattdamon@hotmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Joe").withLastName("Albarn")
                .withEmail("jalbarn@hotmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests1 = new HashSet<>(Arrays.asList(guest1, guest2));

        Guest guest3 = new GuestBuilder().withId(3).withFirstName("Robert").withLastName("Benjamin")
                .withEmail("mattdamon@hotmail.com").withIsReservationHolder(true).build();
        Guest guest4 = new GuestBuilder().withId(4).withFirstName("Casey").withLastName("Jones")
                .withEmail("casey@hotmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests2 = new HashSet<>(Arrays.asList(guest3, guest4));

        ReservationWrapper wrapper1 = createNewReservationWrapper("2020-05-25", "2020-05-27",
                guests1);
        ReservationWrapper wrapper2 = createNewReservationWrapper("2020-05-28", "2020-05-30",
                guests2);
        ReservationWrapper wrapper3 = createNewReservationWrapper("2020-08-02", "2020-08-02",
                guests2);


        Reservation reservation1 = createNewReservationBasedInWrapper(wrapper1);
        Reservation reservation2 = createNewReservationBasedInWrapper(wrapper2);
        Reservation reservation3 = createNewReservationBasedInWrapper(wrapper3);
        reservation1.setBookingId(4);
        reservation1.setStatus(new ReservationStatus(2, "CONFIRMED"));
        reservation2.setBookingId(4);
        reservation2.setStatus(new ReservationStatus(2, "CONFIRMED"));
        reservation3.setBookingId(4);
        reservation3.setStatus(new ReservationStatus(2, "CONFIRMED"));
        reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);
        reservationList.add(reservation3);
    }

    @Test
    public void givenReservationRequest_whenCreateReservation_thenReturnNewReservationId() throws Exception {
        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Derrick").withLastName("McKenzie")
                .withEmail("dmckenzie@gmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Robert").withLastName("Harris")
                .withEmail("rob.harris@gmail.com").withIsReservationHolder(false).build();
        Guest guest3 = new GuestBuilder().withId(3).withFirstName("Ellen").withLastName("Woods")
                .withEmail("e.woods@gmail.com").withIsReservationHolder(false).build();
        Guest guest4 = new GuestBuilder().withId(4).withFirstName("Paul").withLastName("Turner")
                .withEmail("paul.turner@gmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests = new HashSet<>(Arrays.asList(guest1, guest2, guest3, guest4));
        ReservationWrapper wrapper = createNewReservationWrapper("2020-05-10", "2020-05-12",
                guests);
        Reservation reservation = createNewReservationBasedInWrapper(wrapper);

        when(reservationRepository.save(reservation))
                .thenReturn(reservation);
        when(reservationRepository.findAll())
                .thenReturn(reservationList);
        when(availabilityService.isReservationDateRangeAvailable(
                reservation.getArrival(), reservation.getDeparture()))
                .thenReturn(true);
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));

        String jsonRequest = parseFileToJson(JSON_CREATE_REQUEST_PATH);

        mockMvc.perform(post("/api/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        is("Reservation created successfully with bookingId: " + reservation.getBookingId())));
    }

    @Test
    public void givenReservationRequest_whenUpdateReservationStatus_thenReturnOkStatus()  throws Exception {
        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Derrick").withLastName("McKenzie")
                .withEmail("dmckenzie@gmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Robert").withLastName("Harris")
                .withEmail("rob.harris@gmail.com").withIsReservationHolder(false).build();
        Guest guest3 = new GuestBuilder().withId(3).withFirstName("Ellen").withLastName("Woods")
                .withEmail("e.woods@gmail.com").withIsReservationHolder(false).build();
        Guest guest4 = new GuestBuilder().withId(4).withFirstName("Paul").withLastName("Turner")
                .withEmail("paul.turner@gmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests = new HashSet<>(Arrays.asList(guest1, guest2, guest3, guest4));
        ReservationWrapper wrapper = createNewReservationWrapper("2020-05-10", "2020-05-12",
                guests);
        wrapper.setStatus(new ReservationStatus(2, "CONFIRMED"));
        Reservation reservation = createNewReservationBasedInWrapper(wrapper);
        reservation.setBookingId(5);
        reservation.setStatus(new ReservationStatus(2, "CONFIRMED"));

        when(reservationRepository.findById(reservation.getBookingId()))
                .thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation))
                .thenReturn(reservation);
        when(reservationRepository.findAll())
                .thenReturn(reservationList);
        when(availabilityService.isReservationDateRangeAvailable(
                reservation.getArrival(), reservation.getDeparture()))
                .thenReturn(true);
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.getOne(4))
                .thenReturn(new ReservationStatus(4, "CANCELLED"));
        when(reservationStatusesRepository.findByName("CANCELLED"))
                .thenReturn(new ReservationStatus(4, "CANCELLED"));
        when(reservationRepository.findAll()).thenReturn(reservationList);

        String jsonRequest = parseFileToJson(JSON_UPDATE_REQUEST_PATH);

        mockMvc.perform(put("/api/reservation/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        is("Updated reservation with bookingId: " + reservation.getBookingId())));
        Assert.assertEquals("CANCELLED", reservation.getStatus().getName());
    }

    @Test
    public void givenReservationRequest_whenUpdateReservationDates_thenReturnOkStatus()  throws Exception {
        // case when dates are updated and available to change then update them
        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Derrick").withLastName("McKenzie")
                .withEmail("dmckenzie@gmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Robert").withLastName("Harris")
                .withEmail("rob.harris@gmail.com").withIsReservationHolder(false).build();
        Guest guest3 = new GuestBuilder().withId(3).withFirstName("Ellen").withLastName("Woods")
                .withEmail("e.woods@gmail.com").withIsReservationHolder(false).build();
        Guest guest4 = new GuestBuilder().withId(4).withFirstName("Paul").withLastName("Turner")
                .withEmail("paul.turner@gmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests = new HashSet<>(Arrays.asList(guest1, guest2, guest3, guest4));
        ReservationWrapper wrapper = createNewReservationWrapper("2020-05-10", "2020-05-12",
                guests);
        wrapper.setStatus(new ReservationStatus(2, "CONFIRMED"));
        Reservation reservation = createNewReservationBasedInWrapper(wrapper);
        reservation.setBookingId(5);
        reservation.setStatus(new ReservationStatus(2, "CONFIRMED"));

        when(reservationRepository.findById(reservation.getBookingId()))
                .thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation))
                .thenReturn(reservation);
        when(reservationRepository.findAll())
                .thenReturn(reservationList);
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.findByName("CONFIRMED"))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationRepository.findAll()).thenReturn(reservationList);

        String jsonRequest = parseFileToJson(JSON_UPDATE_DATES_REQUEST_PATH);

        mockMvc.perform(put("/api/reservation/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        is("Updated reservation with bookingId: " + reservation.getBookingId())));
        Assert.assertEquals(customParseStringToLocalDateTime("2020-05-15"), reservation.getArrival());
        Assert.assertEquals(customParseStringToLocalDateTime("2020-05-17"), reservation.getDeparture());
    }

    @Test(expected = NestedServletException.class)
    public void givenReservationRequestWithInvalidDates_whenCreateReservation_thenReturnErrorMessage() throws Exception {
        // case when the dates don't meet the constraints for booking
        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Derrick").withLastName("McKenzie")
                .withEmail("dmckenzie@gmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Robert").withLastName("Harris")
                .withEmail("rob.harris@gmail.com").withIsReservationHolder(false).build();
        Guest guest3 = new GuestBuilder().withId(3).withFirstName("Ellen").withLastName("Woods")
                .withEmail("e.woods@gmail.com").withIsReservationHolder(false).build();
        Guest guest4 = new GuestBuilder().withId(4).withFirstName("Paul").withLastName("Turner")
                .withEmail("paul.turner@gmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests = new HashSet<>(Arrays.asList(guest1, guest2, guest3, guest4));
        ReservationWrapper wrapper = createNewReservationWrapper("2020-08-14", "2020-08-20",
                guests);
        Reservation reservation = createNewReservationBasedInWrapper(wrapper);

        when(reservationRepository.findById(reservation.getBookingId()))
                .thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation))
                .thenReturn(reservation);
        when(reservationRepository.findAll())
                .thenReturn(reservationList);
        when(reservationService.validateDateRangeConstraints(reservation.getArrival(), reservation.getDeparture()))
                .thenReturn(true);
        when(availabilityService.isReservationDateRangeAvailable(
                reservation.getArrival(), reservation.getDeparture()))
                .thenReturn(true);
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.findByName("CONFIRMED"))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationRepository.findAll()).thenReturn(reservationList);

        String jsonRequest = parseFileToJson(JSON_CREATE_INVALID_DATES_REQUEST_PATH);

        mockMvc.perform(post("/api/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message",
                        is("The chosen date range exceeds the reservation constraint. Choose less days")));
    }

    @Test(expected = NestedServletException.class)
    public void givenReservationRequestWithInvalidOverlappingDatesCase2_whenCreateReservation_thenReturnErrorMessage() throws Exception {
        // case 2
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.findByName("CONFIRMED"))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationRepository.findAll()).thenReturn(reservationList);

        String jsonRequest = parseFileToJson(JSON_CREATE_INVALID_OVERLAPPING_DATES_REQUEST_CASE2_PATH);

        mockMvc.perform(post("/api/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        is("The chosen dates are not available. Please select others")));
    }

    @Test(expected = NestedServletException.class)
    public void givenReservationRequestWithInvalidOverlappingDatesCase3_whenCreateReservation_thenReturnErrorMessage() throws Exception {
        // case 3
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.findByName("CONFIRMED"))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationRepository.findAll()).thenReturn(reservationList);

        String jsonRequest = parseFileToJson(JSON_CREATE_INVALID_OVERLAPPING_DATES_REQUEST_CASE3_PATH);

        mockMvc.perform(post("/api/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        is("The chosen dates are not available. Please select others")));
    }

    @Test(expected = NestedServletException.class)
    public void givenReservationRequestWithInvalidOverlappingDatesCase4_whenCreateReservation_thenReturnErrorMessage() throws Exception {
        // case 4
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.findByName("CONFIRMED"))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationRepository.findAll()).thenReturn(reservationList);

        String jsonRequest = parseFileToJson(JSON_CREATE_INVALID_OVERLAPPING_DATES_REQUEST_CASE4_PATH);

        mockMvc.perform(post("/api/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        is("The chosen dates are not available. Please select others")));
    }

    @Test(expected = NestedServletException.class)
    public void givenReservationRequestWithInvalidOverlappingDatesCase7_whenCreateReservation_thenReturnErrorMessage() throws Exception {
        // case 7
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.findByName("CONFIRMED"))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationRepository.findAll()).thenReturn(reservationList);

        String jsonRequest = parseFileToJson(JSON_CREATE_INVALID_OVERLAPPING_DATES_REQUEST_CASE7_PATH);

        mockMvc.perform(post("/api/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        is("The chosen dates are not available. Please select others")));
    }

    @Test(expected = NestedServletException.class)
    public void givenReservationRequestWithInvalidOverlappingDatesCase8_whenCreateReservation_thenReturnErrorMessage() throws Exception {
        // case 8
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.findByName("CONFIRMED"))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationRepository.findAll()).thenReturn(reservationList);

        String jsonRequest = parseFileToJson(JSON_CREATE_INVALID_OVERLAPPING_DATES_REQUEST_CASE8_PATH);

        mockMvc.perform(post("/api/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        is("The chosen dates are not available. Please select others")));
    }

    @Test(expected = NestedServletException.class)
    public void givenReservationRequestWithInvalidOverlappingDatesCase9_whenCreateReservation_thenReturnErrorMessage() throws Exception {
        // case 9
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.findByName("CONFIRMED"))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationRepository.findAll()).thenReturn(reservationList);

        String jsonRequest = parseFileToJson(JSON_CREATE_INVALID_OVERLAPPING_DATES_REQUEST_CASE9_PATH);

        mockMvc.perform(post("/api/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        is("The chosen dates are not available. Please select others")));
    }

    @Test
    public void givenReservationRequest_whenUpdateReservationGuests_thenReturnOkStatus()  throws Exception {
        // case when updating the guests by removing one
        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Derrick").withLastName("McKenzie")
                .withEmail("dmckenzie@gmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Robert").withLastName("Harris")
                .withEmail("rob.harris@gmail.com").withIsReservationHolder(false).build();
        Guest guest3 = new GuestBuilder().withId(3).withFirstName("Ellen").withLastName("Woods")
                .withEmail("e.woods@gmail.com").withIsReservationHolder(false).build();
        Guest guest4 = new GuestBuilder().withId(4).withFirstName("Paul").withLastName("Turner")
                .withEmail("paul.turner@gmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests = new HashSet<>(Arrays.asList(guest1, guest2, guest3, guest4));
        ReservationWrapper wrapper = createNewReservationWrapper("2020-05-10", "2020-05-12",
                guests);
        wrapper.setStatus(new ReservationStatus(2, "CONFIRMED"));
        Reservation reservation = createNewReservationBasedInWrapper(wrapper);
        reservation.setBookingId(5);
        reservation.setStatus(new ReservationStatus(2, "CONFIRMED"));

        when(reservationRepository.findById(reservation.getBookingId()))
                .thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation))
                .thenReturn(reservation);
        when(reservationRepository.findAll())
                .thenReturn(reservationList);
        when(availabilityService.isReservationDateRangeAvailable(
                reservation.getArrival(), reservation.getDeparture()))
                .thenReturn(true);
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.findByName("CONFIRMED"))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationRepository.findAll()).thenReturn(reservationList);

        String jsonRequest = parseFileToJson(JSON_UPDATE_GUESTS_REQUEST_PATH);

        mockMvc.perform(put("/api/reservation/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        is("Updated reservation with bookingId: " + reservation.getBookingId())));
        Assert.assertEquals(3, reservation.getGuests().size());
    }

    @Test
    public void givenNewStatus_whenUpdateReservationStatusOnly_thenReturnOKMessage() throws Exception {
        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Derrick").withLastName("McKenzie")
                .withEmail("dmckenzie@gmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Robert").withLastName("Harris")
                .withEmail("rob.harris@gmail.com").withIsReservationHolder(false).build();
        Guest guest3 = new GuestBuilder().withId(3).withFirstName("Ellen").withLastName("Woods")
                .withEmail("e.woods@gmail.com").withIsReservationHolder(false).build();
        Guest guest4 = new GuestBuilder().withId(4).withFirstName("Paul").withLastName("Turner")
                .withEmail("paul.turner@gmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests = new HashSet<>(Arrays.asList(guest1, guest2, guest3, guest4));
        ReservationWrapper wrapper = createNewReservationWrapper("2020-05-10", "2020-05-12",
                guests);
        wrapper.setStatus(new ReservationStatus(2, "CONFIRMED"));
        Reservation reservation = createNewReservationBasedInWrapper(wrapper);
        reservation.setBookingId(2);
        reservation.setStatus(new ReservationStatus(2, "CONFIRMED"));

        when(reservationRepository.findById(reservation.getBookingId()))
                .thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation))
                .thenReturn(reservation);
        when(reservationRepository.findAll())
                .thenReturn(reservationList);
        when(availabilityService.isReservationDateRangeAvailable(
                reservation.getArrival(), reservation.getDeparture()))
                .thenReturn(true);
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.findByName("CONFIRMED"))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.getOne(4))
                .thenReturn(new ReservationStatus(4, "CANCELLED"));
        when(reservationRepository.findAll()).thenReturn(reservationList);

        String jsonRequest = parseFileToJson(JSON_UPDATE_STATUS_ONLY_REQUEST_PATH);

        mockMvc.perform(put("/api/reservation/update/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        is("Reservation with bookingId " + reservation.getBookingId()
                        + " status was updated to: " + reservation.getStatus().getName())));
    }

    @Test
    public void givenBookingId_whenDeleteReservation_thenReturnOKStatus() throws Exception {
        mockMvc.perform(delete("/api/reservation/delete?bookingId={bookingId}", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Deleted reservation with bookingId: 3")));
    }
}
