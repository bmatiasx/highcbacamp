package com.andromedacodelab.HighCbaCamp.exception;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.BODY_IS_MISSING_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.CONSTRAINTS_NOT_MET_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.DATES_ARE_INVALID_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.DATES_ARE_NOT_VALID_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.DATE_RANGE_NOT_ACCEPTED_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.DATE_RANGE_NOT_AVAILABLE_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.INVALID_RESERVATION_STATUS_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.NO_RESERVATION_FOUND_TO_DELETE_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.RESERVATION_CANCELLED_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.RESERVATION_NOT_SAVED_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleApiRequestException(EntityNotFoundException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage());
        return new ResponseEntity<>(restApiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
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

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), BODY_IS_MISSING_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReservationOutOfTermException .class)
    protected ResponseEntity<Object> handleReservationOutOfTermException(ReservationOutOfTermException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), CONSTRAINTS_NOT_MET_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidReservationStatusException .class)
    protected ResponseEntity<Object> handleNotSavedReservationException(InvalidReservationStatusException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), INVALID_RESERVATION_STATUS_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReservationCancelledException .class)
    protected ResponseEntity<Object> handleReservationCancelledException(ReservationCancelledException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), RESERVATION_CANCELLED_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(DateRangeNotAcceptedException .class)
    protected ResponseEntity<Object> handleDateRangeNotAcceptedException(DateRangeNotAcceptedException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), DATE_RANGE_NOT_ACCEPTED_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    protected ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                NO_RESERVATION_FOUND_TO_DELETE_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DateFormatIsInvalidException.class)
    protected ResponseEntity<Object> handleDateFormatIsInvalidException(DateFormatIsInvalidException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                DATES_ARE_NOT_VALID_MESSAGE);
        return new ResponseEntity<>(restApiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParamsMissingException.class)
    protected ResponseEntity<Object> handleParamsMissingException(ParamsMissingException ex) {
        RestApiError restApiError = new RestApiError(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS)),
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage());
        return new ResponseEntity<>(restApiError, HttpStatus.BAD_REQUEST);
    }
}
