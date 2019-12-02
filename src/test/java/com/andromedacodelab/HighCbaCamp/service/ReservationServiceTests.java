package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.TestUtil;
import com.andromedacodelab.HighCbaCamp.exception.InvalidDateRangeException;
import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.ReservationStatus;
import com.andromedacodelab.HighCbaCamp.model.builder.GuestBuilder;
import com.andromedacodelab.HighCbaCamp.model.builder.ReservationBuilder;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import com.andromedacodelab.HighCbaCamp.repository.ReservationStatusesRepository;
import com.andromedacodelab.HighCbaCamp.util.CampApiUtil;
import com.andromedacodelab.HighCbaCamp.util.ReservationWrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.andromedacodelab.HighCbaCamp.util.CampApiUtil.substractWholeDayInHours;

public class ReservationServiceTests {
    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private AvailabilityService availabilityService;

    @Mock
    private GuestService guestService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationStatusesRepository reservationStatusesRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenBookingId_whenFindById_thenReturnReservation() {
        Reservation reservation = TestUtil.createReservationMock();

        Mockito.when(reservationRepository.findById(reservation.getBookingId()))
                .thenReturn(Optional.of(reservation));

        Reservation result = reservationService.findReservationByBookingId(1);

        Assert.assertEquals(reservation.getDeparture(), result.getDeparture());
    }

    @Test
    public void givenReservationParameters_whenCreateReservation_thenReturnValidReservation() {
        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Matt").withLastName("Damon")
                .withEmail("mattdamon@hotmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Joe").withLastName("Albarn")
                .withEmail("ja@hotmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests = new HashSet<>(Arrays.asList(guest1, guest2));

        ReservationWrapper wrapper = createNewReservationWrapper("2020-05-20", "2020-05-23",
                guests);

        Reservation reservation = createNewReservationBasedInWrapper(wrapper);
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation);

        Mockito.when(reservationRepository.save(reservation))
                .thenReturn(reservation);
        Mockito.when(reservationRepository.findAll())
                .thenReturn(reservationList);
        Mockito.when(availabilityService.isReservationDateRangeAvailable(
                reservation.getArrival(), substractWholeDayInHours(reservation.getDeparture())))
                .thenReturn(true);
        Mockito.when(guestService.guestExists(guest1))
                .thenReturn(true);
        Mockito.when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));

        Reservation result = reservationService.createReservation(wrapper);

        Assert.assertEquals(reservation.getBookingId(), result.getBookingId());

    }

    @Test(expected = InvalidDateRangeException.class)
    public void givenReservationParameters_whenCreateReservation_thenReturnError() {
        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Matt").withLastName("Damon")
                .withEmail("mattdamon@hotmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Joe").withLastName("Albarn")
                .withEmail("ja@hotmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests1 = new HashSet<>(Arrays.asList(guest1, guest2));

        Guest guest3 = new GuestBuilder().withId(3).withFirstName("Robert").withLastName("Benjamin")
                .withEmail("mattdamon@hotmail.com").withIsReservationHolder(true).build();
        Guest guest4 = new GuestBuilder().withId(4).withFirstName("Casey").withLastName("Jones")
                .withEmail("ja@hotmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests2 = new HashSet<>(Arrays.asList(guest3, guest4));

        // case 2 overlapping reservations
        ReservationWrapper wrapper1 = createNewReservationWrapper("2020-05-24", "2020-05-27",
                guests1);
        ReservationWrapper wrapper2 = createNewReservationWrapper("2020-05-23", "2020-05-27",
                guests2);

        Reservation reservation1 = createNewReservationBasedInWrapper(wrapper1);
        Reservation reservation2 = createNewReservationBasedInWrapper(wrapper2);
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);

        Mockito.when(reservationRepository.save(reservation1))
                .thenReturn(reservation1);
        Mockito.when(reservationRepository.findAll())
                .thenReturn(reservationList);
        /*Mockito.when(availabilityService.isReservationDateRangeAvailable(
                reservation1.getArrival(), substractWholeDayInHours(reservation1.getDeparture())))
                .thenThrow(new InvalidDateRangeException());*/

        Reservation result = reservationService.createReservation(wrapper1);

        Assert.assertEquals(reservation1.getBookingId(), result.getBookingId());

    }

    public ReservationWrapper createNewReservationWrapper(String arrivalDate, String departureDate, Set<Guest> guests) {
        LocalDateTime arrival = CampApiUtil.customParseStringToLocalDateTime(arrivalDate);
        LocalDateTime departure = CampApiUtil.customParseStringToLocalDateTime(departureDate);

        ReservationStatus status = new ReservationStatus(2, "CONFIRMED");

        return new ReservationWrapper(null, arrival, departure, guests, status.getName());
    }

    public Reservation createNewReservationBasedInWrapper(ReservationWrapper wrapper) {
        return new ReservationBuilder()
                /*.withBookingId()*/
                .withArrivalDate(wrapper.getArrival())
                .withDepartureDate(wrapper.getDeparture())
                .withGuests(wrapper.getGuests())
                .build();
    }
 }
