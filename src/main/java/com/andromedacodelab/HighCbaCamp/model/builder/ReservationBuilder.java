package com.andromedacodelab.HighCbaCamp.model.builder;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.model.Reservation;

import java.time.LocalDateTime;
import java.util.Set;

public class ReservationBuilder {
    private Reservation reservation;

    public ReservationBuilder() {
        this.reservation = new Reservation();
    }

    public Reservation build() {
        return reservation;
    }

    public ReservationBuilder withBookinngId(int bookingId) {
        reservation.setBookingId(bookingId);
        return this;
    }

    public ReservationBuilder withArrivalDate(LocalDateTime arrivalDate) {
        reservation.setArrival(arrivalDate);
        return this;
    }

    public ReservationBuilder withDepartureDate(LocalDateTime departureDate) {
        reservation.setDeparture(departureDate);
        return this;
    }

    public ReservationBuilder withBookinngId(Set<Guest> guests) {
        reservation.setGuests(guests);
        return this;
    }
}
