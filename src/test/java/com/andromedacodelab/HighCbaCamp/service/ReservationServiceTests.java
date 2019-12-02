package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.TestUtil;
import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.ReservationStatus;
import com.andromedacodelab.HighCbaCamp.model.builder.GuestBuilder;
import com.andromedacodelab.HighCbaCamp.model.builder.ReservationBuilder;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
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

public class ReservationServiceTests {
    @InjectMocks
    private ReservationService reservationService;

    @InjectMocks
    private AvailabilityService availabilityService;

    @Mock
    private ReservationRepository reservationRepository;


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
    public void givenReservationParameters_whenCreateReservation_thenReturnOkMessage() {
        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Matt").withLastName("Damon")
                .withEmail("mattdamon@hotmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Joe").withLastName("Albarn")
                .withEmail("ja@hotmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests = new HashSet<>(Arrays.asList(guest1, guest2));

        ReservationWrapper wrapper = createNewReservationWrapper("2020-05-20", "2020-05-20",
                guests);


        Reservation reservation = createNewReservationBasedInWrapper(wrapper);
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation);

        Mockito.when(reservationRepository.save(reservation))
                .thenReturn(reservation);
        Mockito.when(reservationRepository.findAll())
                .thenReturn(reservationList);
        Mockito.when(availabilityService.isReservationDateRangeAvailable(
                reservation.getArrival(), reservation.getDeparture()))
                .thenReturn(true);

        Reservation result = reservationService.createReservation(wrapper);

        Assert.assertEquals(reservation.getBookingId(), result.getBookingId());

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
