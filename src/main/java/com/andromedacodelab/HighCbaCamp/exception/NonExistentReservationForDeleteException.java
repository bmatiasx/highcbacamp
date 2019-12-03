package com.andromedacodelab.HighCbaCamp.exception;

public class NonExistentReservationForDeleteException extends RuntimeException {
    public NonExistentReservationForDeleteException() {
    }

    public NonExistentReservationForDeleteException(String message) {
        super(message);
    }
}
