package com.andromedacodelab.HighCbaCamp.util;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;

import java.time.LocalDate;
import java.util.Set;

public class ReservationWrapper extends Reservation {
    private String statusName;

    public ReservationWrapper(Integer bookingId, LocalDate arrival, LocalDate departure,
                              Set<Guest> guests, String statusName) {
        super.setBookingId(bookingId);
        super.setArrival(arrival);
        super.setDeparture(departure);
        super.setGuests(guests);
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
