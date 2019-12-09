package com.andromedacodelab.HighCbaCamp.model.builder;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;
import com.andromedacodelab.HighCbaCamp.model.ReservationStatus;

import java.time.LocalDate;
import java.util.Set;

public class ReservationBuilder {
    private Reservation reservation;

    public ReservationBuilder() {
        this.reservation = new Reservation();
    }

    public Reservation build() {
        return reservation;
    }

    public ReservationBuilder withBookingId(Integer bookingId) {
        reservation.setBookingId(bookingId);
        return this;
    }

    public ReservationBuilder withArrivalDate(LocalDate arrivalDate) {
        reservation.setArrival(arrivalDate);
        return this;
    }

    public ReservationBuilder withDepartureDate(LocalDate departureDate) {
        reservation.setDeparture(departureDate);
        return this;
    }

    public ReservationBuilder withGuests(Set<Guest> guests) {
        reservation.setGuests(guests);
        return this;
    }

    public ReservationBuilder withStatus(ReservationStatus status) {
        reservation.setStatus(status);
        return this;
    }
}
