package com.andromedacodelab.HighCbaCamp.controller;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.ReservationStatus;
import com.andromedacodelab.HighCbaCamp.model.builder.GuestBuilder;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import com.andromedacodelab.HighCbaCamp.util.ReservationWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.andromedacodelab.HighCbaCamp.TestUtil.createNewReservationBasedInWrapper;
import static com.andromedacodelab.HighCbaCamp.TestUtil.createNewReservationWrapper;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.YEAR_MONTH_DAY;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@SpringBootTest
@RunWith(SpringRunner.class)
public class AvailabilityControllerTests {
    private MockMvc mockMvc;

    @Autowired
    private AvailabilityController availabilityController;

    @MockBean
    private ReservationRepository reservationRepository;

    private List<Reservation> reservationList;

    @Before
    public void setUp() {
        this.mockMvc = standaloneSetup(this.availabilityController).build();

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

        ReservationWrapper wrapper1 = createNewReservationWrapper("2020-05-24", "2020-05-27",
                guests1);
        ReservationWrapper wrapper2 = createNewReservationWrapper("2020-05-28", "2020-05-30",
                guests2);

        Reservation reservation1 = createNewReservationBasedInWrapper(wrapper1);
        Reservation reservation2 = createNewReservationBasedInWrapper(wrapper2);
        reservation1.setBookingId(4);
        reservation1.setStatus(new ReservationStatus(2, "CONFIRMED"));
        reservation2.setBookingId(4);
        reservation2.setStatus(new ReservationStatus(2, "CONFIRMED"));
        reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);
    }

    @Test
    public void givenDateRange_whenGetAvailability_thenReturnAvailableMessage() throws Exception {
        when(reservationRepository.findAll()).thenReturn(reservationList);

        mockMvc.perform(get("/api/availability?start=2021-05-10&end=2021-05-12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("The date range [arrival: 2021-05-10, departure: 2021-05-12] is available!")));
    }

    @Test
    public void givenDateRange_whenGetAvailability_thenReturnNotAvailableMessage() throws Exception {
        when(reservationRepository.findAll()).thenReturn(reservationList);

        mockMvc.perform(get("/api/availability?start=2020-05-28&end=2020-05-30")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message",
                        is("The date range [arrival: 2020-05-28, departure: 2020-05-30] is not available")));
    }

    @Test
    public void givenDateRangeWithoutEnd_whenGetAvailability_thenReturnAvailableMessage() throws Exception {
        // case when no departure date is provided then use the 1 month default
        when(reservationRepository.findAll()).thenReturn(reservationList);

        mockMvc.perform(get("/api/availability?start=2021-05-10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("The date range [arrival: 2021-05-10, departure: 2021-06-10] is available!")));
    }

    @Test
    public void givenDateRangeWithoutStart_whenGetAvailability_thenReturnAvailableMessage() throws Exception {
        // case when no departure date is provided then use the 1 month default
        when(reservationRepository.findAll()).thenReturn(reservationList);

        mockMvc.perform(get("/api/availability?end=2021-05-10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("The date range [arrival: 2021-04-10, departure: 2021-05-10] is available!")));
    }

    @Test
    public void givenEmptyDateRange_whenGetAvailability_thenReturnAvailableMessage() throws Exception {
        // case when no dates are provided then use now() as the starting date
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY));
        String endDate = LocalDateTime.now().plusMonths(1).format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY));
        when(reservationRepository.findAll()).thenReturn(reservationList);

        mockMvc.perform(get("/api/availability")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",
                        is("The date range [arrival: " + now + ", departure: " + endDate + "] is available!")));
    }
}
