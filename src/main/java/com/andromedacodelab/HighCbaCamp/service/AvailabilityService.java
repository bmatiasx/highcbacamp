package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.exception.InvalidDateRangeException;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.andromedacodelab.HighCbaCamp.util.CampApiUtil.isBetweenDates;
import static com.andromedacodelab.HighCbaCamp.util.CampApiUtil.validateArrivalIsBeforeDeparture;

@Service
public class AvailabilityService {
    private ReservationRepository reservationRepository;

    @Autowired
    public AvailabilityService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     *  Checks if a given date range is available
     * @param start the possible arrival date
     * @param end the possible departure date
     * @return true if there is not overlapping with other reservations, otherwise returns false
     */
    public boolean isReservationDateRangeAvailable(LocalDate start, LocalDate end) {
        /* Checks if the initial date is before the end date */
        if (!validateArrivalIsBeforeDeparture(start, end)) {
            throw new InvalidDateRangeException();
        }

        List<Reservation> existingReservations = reservationRepository.findAll();
        boolean isDateRangeAvailable = false;
        if (existingReservations.size() == 0) isDateRangeAvailable = true;

        existingReservations.removeIf(r -> r.getStatus().getName().equals("CANCELLED"));

        for (Reservation existingReservation : existingReservations) {
            LocalDate existingArrival = existingReservation.getArrival();
            LocalDate existingDeparture = existingReservation.getDeparture();
            // case 1
            if (start.isEqual(existingArrival) && end.isEqual(
                    existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 2
            } else if (start.isEqual(existingArrival) && isBetweenDates(start, end, existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 3
            } else if (isBetweenDates(start, end, existingArrival) && end.isEqual(
                    existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 4
            } else if (start.isBefore(existingArrival) && end.isAfter(existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 5
            } else if (existingArrival.isAfter(start) && isBetweenDates(existingArrival, existingDeparture, end)) {
                isDateRangeAvailable = false;
                break;
                // case 6
            } else if (isBetweenDates(start, end, existingDeparture) && end.isAfter(existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 7
            } else if (start.isAfter(existingArrival) && end.isBefore(existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 8
            } else if (isBetweenDates(existingArrival, existingDeparture, start) && end.isEqual(existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 9
            } else if(start.isEqual(existingArrival) && isBetweenDates(existingArrival, existingDeparture, end)) {
                isDateRangeAvailable = false;
                break;
                // case 10 (success)
            } else if (start.isAfter(existingDeparture)) {
                isDateRangeAvailable = true;
                // case 11 (success)
            } else if (end.isBefore(existingDeparture)){
                isDateRangeAvailable = true;
            }
        }
        return isDateRangeAvailable;
    }
}
