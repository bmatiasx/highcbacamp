package com.andromedacodelab.HighCbaCamp.exception;

public class ReservationCancelledException extends RuntimeException {
    public ReservationCancelledException() {
    }

    public ReservationCancelledException(String message) {
        super(message);
    }
}
