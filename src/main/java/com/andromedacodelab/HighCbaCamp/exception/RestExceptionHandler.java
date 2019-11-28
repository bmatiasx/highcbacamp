package com.andromedacodelab.HighCbaCamp.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.BAD_REQUEST_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.CONSTRAINTS_NOT_MET_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.DATES_ARE_INVALID_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.DATE_RANGE_NOT_AVAILABLE_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.INVALID_RESERVATION_STATUS_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.RESERVATION_NOT_SAVED_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException .class)
    protected ResponseEntity<Object> handleApiRequestException(EntityNotFoundException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage());
        return new ResponseEntity<>(restApiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReservationNotFoundException .class)
    protected ResponseEntity<Object> handleApiRequestException(ReservationNotFoundException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage());
        return new ResponseEntity<>(restApiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> constraintValidationException(MethodArgumentTypeMismatchException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), BAD_REQUEST_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<Object> handleInvalidDateRangeException(InvalidDateRangeException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), DATES_ARE_INVALID_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status,
                                                                          WebRequest request) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
        return new ResponseEntity<>(restApiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateRangeNotAvailableException .class)
    protected ResponseEntity<Object> handleDateRangeNotAvailableException(DateRangeNotAvailableException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), DATE_RANGE_NOT_AVAILABLE_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(ReservationOutOfTermException .class)
    protected ResponseEntity<Object> handleReservationOutOfTermException(ReservationOutOfTermException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), CONSTRAINTS_NOT_MET_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotSavedReservationException .class)
    protected ResponseEntity<Object> handleNotSavedReservationException(NotSavedReservationException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), RESERVATION_NOT_SAVED_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidReservationStatusException .class)
    protected ResponseEntity<Object> handleNotSavedReservationException(InvalidReservationStatusException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), INVALID_RESERVATION_STATUS_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.CONFLICT);
    }
}
