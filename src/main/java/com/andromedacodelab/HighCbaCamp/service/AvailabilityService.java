package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.exception.InvalidDateRangeException;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.andromedacodelab.HighCbaCamp.util.CampApiUtil.addWholeDayInHours;
import static com.andromedacodelab.HighCbaCamp.util.CampApiUtil.isBetweenDates;
import static com.andromedacodelab.HighCbaCamp.util.CampApiUtil.validateArrivalIsBeforeDeparture;

@Service
public class AvailabilityService {
    private ReservationRepository reservationRepository;

    @Autowired
    public AvailabilityService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public boolean isReservationDateRangeAvailable(LocalDateTime start, LocalDateTime end) {
        /* Checks if the initial date is before the end date */
        if (!validateArrivalIsBeforeDeparture(start, end)) {
            throw new InvalidDateRangeException();
        }

        List<Reservation> existingReservations = reservationRepository.findAll();
        boolean isDateRangeAvailable = false;
        if (existingReservations.size() == 0) isDateRangeAvailable = true;

        existingReservations.removeIf(r -> r.getStatus().getName().equals("CANCELLED"));

        for (Reservation existingReservation : existingReservations) {
            LocalDateTime existingArrival = existingReservation.getArrival();
            LocalDateTime existingDeparture = addWholeDayInHours(existingReservation.getDeparture());
            // case 1
            if (start.isEqual(existingArrival) && addWholeDayInHours(end).isEqual(
                    existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 2
            } else if (start.isEqual(existingArrival) && isBetweenDates(start, addWholeDayInHours(end),
                    existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 3
            } else if (isBetweenDates(start, end, existingArrival) && addWholeDayInHours(end).isEqual(
                    existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 4
            } else if (start.isBefore(existingArrival) && addWholeDayInHours(end).isAfter(
                    existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 5
            } else if (existingArrival.isAfter(start) &&
                    isBetweenDates(existingArrival, existingDeparture, addWholeDayInHours(end))) {
                isDateRangeAvailable = false;
                break;
                // case 6
            } else if (isBetweenDates(start, end, existingDeparture) &&
                    end.isAfter(existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 7
            } else if (start.isAfter(existingArrival) && addWholeDayInHours(end).isBefore(existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 8
            } else if (isBetweenDates(existingArrival, existingDeparture, start) &&
                    addWholeDayInHours(end).isEqual(existingDeparture)) {
                isDateRangeAvailable = false;
                break;
                // case 9
            } else if(start.isEqual(existingArrival) && isBetweenDates(existingArrival,
                    existingDeparture, addWholeDayInHours(end))) {
                isDateRangeAvailable = false;
                break;
                // case 10 (success)
            } else if (start.isAfter(existingDeparture)) {
                isDateRangeAvailable = true;
                // case 11 (success)
            } else if (addWholeDayInHours(end).isBefore(existingDeparture)){
                isDateRangeAvailable = true;
            }
        }
        return isDateRangeAvailable;
    }
}
