package com.andromedacodelab.HighCbaCamp.exception;

public class ReservationNotFoundException extends RuntimeException {
    private Integer bookingId;

    public ReservationNotFoundException(Integer bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public String getMessage() {
        return "The reservation with bookingId " + bookingId + " is not found";
    }
}
