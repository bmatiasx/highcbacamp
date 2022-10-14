package com.andromedacodelab.HighCbaCamp.unit;

import com.andromedacodelab.HighCbaCamp.TestUtil;
import com.andromedacodelab.HighCbaCamp.exception.InvalidDateRangeException;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.andromedacodelab.HighCbaCamp.TestUtil.createNewReservationBasedInWrapper;
import static com.andromedacodelab.HighCbaCamp.TestUtil.createNewReservationWrapper;
import static org.mockito.Mockito.when;

public class ReservationServiceTest {
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

        when(reservationRepository.findById(reservation.getBookingId()))
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

        ReservationWrapper wrapper = createNewReservationWrapper("2022-12-20", "2022-12-23",
                guests);

        Reservation reservation = createNewReservationBasedInWrapper(wrapper);
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation);

        when(reservationRepository.save(reservation))
                .thenReturn(reservation);
        when(reservationRepository.findAll())
                .thenReturn(reservationList);
        when(availabilityService.isReservationDateRangeAvailable(
                reservation.getArrival(), reservation.getDeparture()))
                .thenReturn(true);
        when(guestService.guestExists(guest1))
                .thenReturn(true);
        when(guestService.guestExists(guest2))
                .thenReturn(true);
        when(reservationStatusesRepository.getOne(2))
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
        ReservationWrapper wrapper1 = createNewReservationWrapper("2022-12-24", "2022-12-27",
                guests1);
        ReservationWrapper wrapper2 = createNewReservationWrapper("2022-12-23", "2022-12-27",
                guests2);

        Reservation reservation1 = createNewReservationBasedInWrapper(wrapper1);
        Reservation reservation2 = createNewReservationBasedInWrapper(wrapper2);
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);

        when(reservationRepository.save(reservation1)).thenReturn(reservation1);
        when(reservationRepository.findAll()).thenReturn(reservationList);

        Reservation result = reservationService.createReservation(wrapper1);

        Assert.assertEquals(reservation1.getBookingId(), result.getBookingId());
    }

    @Test
    public void givenDateRangeWithCancelledReservation_whenSaveReservation_thenReturnValidReservation() {
        // case with one reservation cancelled
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

        ReservationWrapper wrapper1 = createNewReservationWrapper("2022-12-20", "2022-12-22",
                guests1);
        ReservationWrapper wrapper2 = createNewReservationWrapper("2022-12-20", "2022-12-22",
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

        when(availabilityService.isReservationDateRangeAvailable(
                reservation1.getArrival(), reservation1.getDeparture()))
                .thenReturn(true);
        when(guestService.guestExists(guest1))
                .thenReturn(true);
        when(guestService.guestExists(guest1))
                .thenReturn(true);
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.getOne(4))
                .thenReturn(new ReservationStatus(4, "CANCELLED"));
        when(reservationRepository.save(reservation1)).thenReturn(reservation1);
        when(reservationRepository.findAll()).thenReturn(reservationList);

        Reservation result = reservationService.createReservation(wrapper1);

        Assert.assertEquals(reservation1.getDeparture(), result.getDeparture());
        Assert.assertEquals(reservation1.getArrival(), result.getArrival());
    }

    @Test
    public void givenReservationParameters_whenUpdateReservationStatus_thenReturnValidReservation() {
        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Matt").withLastName("Damon")
                .withEmail("mattdamon@hotmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Joe").withLastName("Albarn")
                .withEmail("jalbarn@hotmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests1 = new HashSet<>(Arrays.asList(guest1, guest2));

        ReservationWrapper newReservationWrapper = createNewReservationWrapper("2022-12-20", "2022-12-22",
                guests1);
        ReservationWrapper oldReservationWrapper = createNewReservationWrapper("2022-12-20", "2022-12-22",
                guests1);
        newReservationWrapper.setBookingId(3);
        oldReservationWrapper.setBookingId(3);

        newReservationWrapper.setStatusName("CANCELLED");
        newReservationWrapper.setStatus(new ReservationStatus(4, newReservationWrapper.getStatusName()));

        Reservation reservationNewState = createNewReservationBasedInWrapper(newReservationWrapper);
        Reservation oldReservation = createNewReservationBasedInWrapper(oldReservationWrapper);
        oldReservation.setStatus(new ReservationStatus(2, "CONFIRMED"));

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(oldReservation);

        when(availabilityService.isReservationDateRangeAvailable(
                reservationNewState.getArrival(), reservationNewState.getDeparture()))
                .thenReturn(true);
        when(reservationRepository.findById(3))
                .thenReturn(Optional.of(oldReservation));
        when(guestService.guestExists(guest1))
                .thenReturn(true);
        when(guestService.guestExists(guest1))
                .thenReturn(true);
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.getOne(4))
                .thenReturn(new ReservationStatus(4, "CANCELLED"));
        when(reservationStatusesRepository.findByName(newReservationWrapper.getStatusName()))
                .thenReturn(new ReservationStatus(4, "CANCELLED"));
        when(reservationRepository.save(reservationNewState)).thenReturn(oldReservation);
        when(reservationRepository.findAll()).thenReturn(reservationList);

        Reservation result = reservationService.updateReservation(newReservationWrapper);

        Assert.assertEquals(oldReservation.getStatus().getId(), result.getStatus().getId());
    }

    @Test
    public void givenReservationParameters_whenUpdateReservationDates_thenReturnValidReservation() {
        Guest guest1 = new GuestBuilder().withId(1).withFirstName("Matt").withLastName("Damon")
                .withEmail("mattdamon@hotmail.com").withIsReservationHolder(true).build();
        Guest guest2 = new GuestBuilder().withId(2).withFirstName("Joe").withLastName("Albarn")
                .withEmail("jalbarn@hotmail.com").withIsReservationHolder(false).build();
        Set<Guest> guests1 = new HashSet<>(Arrays.asList(guest1, guest2));

        ReservationWrapper newReservationWrapper = createNewReservationWrapper("2022-12-15", "2022-12-17",
                guests1);
        ReservationWrapper oldReservationWrapper = createNewReservationWrapper("2022-12-20", "2022-12-22",
                guests1);
        newReservationWrapper.setBookingId(3);
        oldReservationWrapper.setBookingId(3);

        newReservationWrapper.setStatusName("CONFIRMED");
        newReservationWrapper.setStatus(new ReservationStatus(2, newReservationWrapper.getStatusName()));

        Reservation updatedReservation = createNewReservationBasedInWrapper(newReservationWrapper);
        Reservation oldReservation = createNewReservationBasedInWrapper(oldReservationWrapper);
        oldReservation.setStatus(new ReservationStatus(2, "CONFIRMED"));
        updatedReservation.setStatus(new ReservationStatus(2, "CONFIRMED"));

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(oldReservation);

        when(availabilityService.isReservationDateRangeAvailable(
                updatedReservation.getArrival(), updatedReservation.getDeparture()))
                .thenReturn(true);
        when(reservationRepository.findById(3))
                .thenReturn(Optional.of(oldReservation));
        when(availabilityService.isReservationDateRangeAvailable(
                updatedReservation.getArrival(), updatedReservation.getDeparture()))
                .thenReturn(true);
        when(guestService.guestExists(guest1))
                .thenReturn(true);
        when(guestService.guestExists(guest1))
                .thenReturn(true);
        when(reservationStatusesRepository.getOne(2))
                .thenReturn(new ReservationStatus(2, "CONFIRMED"));
        when(reservationStatusesRepository.findByName("CONFIRMED"))
                .thenReturn(new ReservationStatus(4, "CONFIRMED"));
        when(reservationRepository.save(updatedReservation)).thenReturn(oldReservation);
        when(reservationRepository.findAll()).thenReturn(reservationList);

        Reservation result = reservationService.updateReservation(newReservationWrapper);

        Assert.assertEquals(oldReservation.getArrival(), result.getArrival());
    }
 }
