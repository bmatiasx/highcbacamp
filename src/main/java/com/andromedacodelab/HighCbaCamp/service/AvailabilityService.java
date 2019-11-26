package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        List<Reservation> reservations = reservationRepository.findAll();
        boolean isDateRangeAvailable = false;

        for (Reservation reservation : reservations) {
            isDateRangeAvailable = !doesReservationDatesOverlap(start, end, reservation);
        }
        return isDateRangeAvailable;
    }
}
