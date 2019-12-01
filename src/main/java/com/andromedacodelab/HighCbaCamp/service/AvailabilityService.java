package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.exception.InvalidDateRangeException;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import com.andromedacodelab.HighCbaCamp.util.CampApiUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.andromedacodelab.HighCbaCamp.util.CampApiUtility.addWholeDayInHours;
import static com.andromedacodelab.HighCbaCamp.util.CampApiUtility.isBetweenDates;

@Service
public class AvailabilityService {
    private ReservationRepository reservationRepository;

    @Autowired
    public AvailabilityService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public boolean isReservationDateRangeAvailable(LocalDateTime start, LocalDateTime end) {
        /* Checks if the initial date is before the end date */
        if (!CampApiUtility.validateArrivalIsBeforeDeparture(start, end)) {
            throw new InvalidDateRangeException();
        }

        List<Reservation> existingReservations = reservationRepository.findAll();
        boolean isDateRangeAvailable = false;

        existingReservations.removeIf(r -> r.getStatus().getName().equals("CANCELLED"));

        for (Reservation existingReservation : existingReservations) {
            // case 1
            if (start.isEqual(existingReservation.getArrival()) && addWholeDayInHours(end).isEqual(
                    existingReservation.getDeparture())) {
                isDateRangeAvailable = false;
                break;
                // case 2
            } else if (start.isEqual(existingReservation.getArrival()) && isBetweenDates(start, addWholeDayInHours(end),
                    existingReservation.getDeparture())) {
                isDateRangeAvailable = false;
                break;
                // case 3
            } else if (isBetweenDates(start, end, existingReservation.getArrival()) && addWholeDayInHours(end).isEqual(
                    existingReservation.getDeparture())) {
                isDateRangeAvailable = false;
                break;
                // case 4
            } else if (start.isBefore(existingReservation.getArrival()) && addWholeDayInHours(end).isAfter(
                    existingReservation.getDeparture())) {
                isDateRangeAvailable = false;
                break;
                // case 5
            } else if (isBetweenDates(start, end, existingReservation.getArrival()) &&
                    isBetweenDates(existingReservation.getArrival(), existingReservation.getDeparture(), addWholeDayInHours(end))) {
                isDateRangeAvailable = false;
                break;
                // case 6
            } else if (isBetweenDates(start, end, existingReservation.getDeparture()) &&
                    end.isAfter(existingReservation.getDeparture())) {
                isDateRangeAvailable = false;
                break;
                // case 7
            } else if (start.isAfter(existingReservation.getArrival()) && addWholeDayInHours(end).isBefore(existingReservation.getDeparture())) {
                isDateRangeAvailable = false;
                break;
                // case 8
            } else if (isBetweenDates(existingReservation.getArrival(), existingReservation.getDeparture(), start) &&
                    addWholeDayInHours(end).isEqual(existingReservation.getDeparture())) {
                isDateRangeAvailable = false;
                break;
                // case 9
            } else if(start.isEqual(existingReservation.getArrival()) && isBetweenDates(existingReservation.getArrival(),
                    existingReservation.getDeparture(), addWholeDayInHours(end))) {
                isDateRangeAvailable = false;
                break;
                // case 10
            } else if(existingReservations.iterator().hasNext() &&
                    isBetweenDates(existingReservation.getArrival(), existingReservation.getDeparture(), start) &&
                    isBetweenDates(existingReservations.iterator().next().getArrival(),
                            existingReservations.iterator().next().getDeparture(), addWholeDayInHours(end))) {
                isDateRangeAvailable = false;
                break;
                // case 11 (success)
            } else if (start.isAfter(existingReservation.getDeparture())) {
                isDateRangeAvailable = true;
                // case 12 (success)
            } else if (addWholeDayInHours(end).isBefore(existingReservation.getDeparture())){
                isDateRangeAvailable = true;
            }
        }
        return isDateRangeAvailable;
    }
}
