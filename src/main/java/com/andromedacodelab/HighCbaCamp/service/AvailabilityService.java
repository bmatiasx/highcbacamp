package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.exception.ApiException;
import com.andromedacodelab.HighCbaCamp.exception.InvalidDateRangeException;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import com.andromedacodelab.HighCbaCamp.util.CampApiUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.andromedacodelab.HighCbaCamp.util.CampApiUtility.doesReservationDatesOverlap;

@Service
public class AvailabilityService {
    private ReservationRepository reservationRepository;

    @Autowired
    public AvailabilityService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public boolean isReservationAvailable(LocalDateTime start, LocalDateTime end) {
        /* Checks if the initial date is before the end date */
        if (!CampApiUtility.validateDates(start, end)) {
            throw new InvalidDateRangeException();
        }

        List<Reservation> reservations = reservationRepository.findAll();
        boolean isDateRangeAvailable = false;

        for (Reservation reservation : reservations) {
            isDateRangeAvailable = !doesReservationDatesOverlap(start, end, reservation);
        }
        return isDateRangeAvailable;
    }
}
