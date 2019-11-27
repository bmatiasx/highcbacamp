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

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String BAD_REQUEST_MESSAGE = "Request parameters are not be valid";
    private static final String DATES_ARE_INVALID_MESSAGE = "The dates selected are not valid";

    @ExceptionHandler(EntityNotFoundException .class)
    protected ResponseEntity<Object> handleApiRequestException(EntityNotFoundException ex) {
        ApiException apiException = new ApiException(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")),
                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage()
                );
        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> constraintValidationException(MethodArgumentTypeMismatchException ex) {
        ApiException apiException = new ApiException(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")),
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), BAD_REQUEST_MESSAGE);
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<Object> invalidDateRangeException(InvalidDateRangeException ex) {
        ApiException apiException = new ApiException(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")),
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), DATES_ARE_INVALID_MESSAGE);
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status,
                                                                          WebRequest request) {
        ApiException apiException = new ApiException(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")),
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }
}
