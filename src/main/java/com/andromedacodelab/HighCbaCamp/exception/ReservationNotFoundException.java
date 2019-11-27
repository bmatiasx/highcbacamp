package com.andromedacodelab.HighCbaCamp.exception;

public class ReservationNotFoundException extends RuntimeException {
    private int bookingId;

    public ReservationNotFoundException() {
    }

    public ReservationNotFoundException(String s) {
        super(s);
    }

    public ReservationNotFoundException(int bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public String getMessage() {
        return "The reservation with bookingId " + bookingId + " is not found";
    }
}
