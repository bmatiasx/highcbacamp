package com.andromedacodelab.HighCbaCamp.exception;

public class InvalidReservationStatusException extends RuntimeException {
    public InvalidReservationStatusException() {
    }

    public InvalidReservationStatusException(String s) {
        super(s);
    }
}
