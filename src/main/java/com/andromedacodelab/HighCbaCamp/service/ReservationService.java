package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation findReservationByBookingId(int bookingId)  {
        return reservationRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "The reservation with bookingId " + bookingId + " is not found"
                ));
    }
}
