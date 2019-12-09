package com.andromedacodelab.HighCbaCamp.unit;

import com.andromedacodelab.HighCbaCamp.exception.InvalidDateRangeException;
import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.ReservationStatus;
import com.andromedacodelab.HighCbaCamp.model.builder.GuestBuilder;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import com.andromedacodelab.HighCbaCamp.service.AvailabilityService;
import com.andromedacodelab.HighCbaCamp.util.CampApiUtil;
import com.andromedacodelab.HighCbaCamp.util.ReservationWrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.andromedacodelab.HighCbaCamp.TestUtil.createNewReservationBasedInWrapper;
import static com.andromedacodelab.HighCbaCamp.TestUtil.createNewReservationWrapper;

public class AvailabilityServiceTest {
    @InjectMocks
    private AvailabilityService availabilityService;

    @Mock
    private ReservationRepository reservationRepository;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenDateRange_whenFindAvailability_thenReturnTrue() {
        LocalDate start = CampApiUtil.customParseStringToLocalDate("2020-03-10");
        LocalDate end = CampApiUtil.customParseStringToLocalDate("2020-03-13");

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

        // case 2 future reservations
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
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);

        Mockito.when(reservationRepository.findAll()).thenReturn(reservationList);

        Boolean result = availabilityService.isReservationDateRangeAvailable(start, end);

        Assert.assertEquals(result, true);
    }


    @Test
    public void givenDateRange_whenFindAvailability_thenReturnFalse() {
        // case overlapping dates
        LocalDate start = CampApiUtil.customParseStringToLocalDate("2020-05-21");
        LocalDate end = CampApiUtil.customParseStringToLocalDate("2020-05-24");

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
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);

        Mockito.when(reservationRepository.findAll()).thenReturn(reservationList);

        Boolean result = availabilityService.isReservationDateRangeAvailable(start, end);

        Assert.assertEquals(result, false);
    }

    @Test
    public void givenDateRange_whenFindAvailability_thenReturnFalse2ndScenario() {
        // case overlapping dates
        LocalDate start = CampApiUtil.customParseStringToLocalDate("2020-05-24");
        LocalDate end = CampApiUtil.customParseStringToLocalDate("2020-05-27");

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
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);

        Mockito.when(reservationRepository.findAll()).thenReturn(reservationList);

        Boolean result = availabilityService.isReservationDateRangeAvailable(start, end);

        Assert.assertEquals(result, false);
    }

    @Test
    public void givenDateRange_whenFindAvailability_thenReturnFalse3rdScenario() {
        // case overlapping dates in the end of existing reservation
        LocalDate start = CampApiUtil.customParseStringToLocalDate("2020-05-30");
        LocalDate end = CampApiUtil.customParseStringToLocalDate("2020-06-01");

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
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);

        Mockito.when(reservationRepository.findAll()).thenReturn(reservationList);

        Boolean result = availabilityService.isReservationDateRangeAvailable(start, end);

        Assert.assertEquals(result, false);
    }

    @Test(expected = InvalidDateRangeException.class)
    public void givenInvalidDateRange_whenFindAvailability_thenReturnException() {
        // case invalid date ranges
        LocalDate start = CampApiUtil.customParseStringToLocalDate("2020-06-15");
        LocalDate end = CampApiUtil.customParseStringToLocalDate("2020-04-20");

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
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);

        Mockito.when(reservationRepository.findAll()).thenReturn(reservationList);

        Boolean result = availabilityService.isReservationDateRangeAvailable(start, end);

        Assert.assertEquals(result, false);
    }

    @Test
    public void givenDateRangeWithCancelledReservation_whenFindAvailability_thenReturnTrue() {
        // case one cancelled reservation
        LocalDate start = CampApiUtil.customParseStringToLocalDate("2020-05-28");
        LocalDate end = CampApiUtil.customParseStringToLocalDate("2020-05-30");

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
        reservation2.setStatus(new ReservationStatus(2, "CANCELLED"));
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);

        Mockito.when(reservationRepository.findAll()).thenReturn(reservationList);

        Boolean result = availabilityService.isReservationDateRangeAvailable(start, end);

        Assert.assertEquals(result, true);
    }

}
