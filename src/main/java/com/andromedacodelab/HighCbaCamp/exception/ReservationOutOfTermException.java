package com.andromedacodelab.HighCbaCamp.exception;

public class ReservationOutOfTermException extends RuntimeException {
    public ReservationOutOfTermException() {
    }

    public ReservationOutOfTermException(String s) {
        super(s);
    }
}
