package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvailabilityService {
    private ReservationRepository reservationRepository;

    @Autowired
    public AvailabilityService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public boolean isReservationAvailable(LocalDateTime start, LocalDateTime end) {
        List<Reservation> reservations = reservationRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        boolean isDateRangeAvailable = false;

        if (start == null) {
            // Use default time range which is 1 month
            start = now;
            end = now.plusMonths(1);
        }

        for (Reservation r : reservations) {
            isDateRangeAvailable = (start.isEqual(r.getArrival()) || start.isAfter(r.getArrival())
            && end.isEqual(r.getDeparture()) || end.isBefore(r.getDeparture()));
        }

        return isDateRangeAvailable;
    }
}
