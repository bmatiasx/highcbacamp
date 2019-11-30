package com.andromedacodelab.HighCbaCamp.exception;

public class DateRangeNotAcceptedException extends RuntimeException{
    public DateRangeNotAcceptedException() {
    }

    public DateRangeNotAcceptedException(String message) {
        super(message);
    }
}
