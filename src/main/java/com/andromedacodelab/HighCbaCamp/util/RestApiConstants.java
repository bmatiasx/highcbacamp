package com.andromedacodelab.HighCbaCamp.util;

public class RestApiConstants {
    public static final String BAD_REQUEST_MESSAGE = "Request parameters are not be valid";
    public static final String DATES_ARE_INVALID_MESSAGE = "The chosen dates are not available. Please select others";
    public static final String DATE_RANGE_NOT_AVAILABLE_MESSAGE = "The chosen date range is already taken, please " +
            "choose other";
    public static final String CONSTRAINTS_NOT_MET_MESSAGE = "The chosen date range does not meets the constraints" +
            " of being one day ahead or up to one month in advance from now";
    public static final String RESERVATION_NOT_SAVED_MESSAGE = "The reservation could not be saved";
    public static final String INVALID_RESERVATION_STATUS_MESSAGE = "Invalid reservation status found. Try again" +
            " with a valid status";
    public static final String RESOURCE_NOT_EXISTS_MESSAGE = "Resource does not exists";
    public static final String SERVER_ERROR_MESSAGE = "Oops, there was a problem on our end";
    public static final String BODY_IS_MISSING_MESSAGE = "Required request body is missing";
    public static final String NO_RESERVATION_FOUND_TO_DELETE_MESSAGE = "The reservation you're trying to delete doesn't exists";
    public static final String RESERVATION_CANCELLED_MESSAGE = "The reservation you're trying to modify is cancelled";
    public static final String DATE_RANGE_NOT_ACCEPTED_MESSAGE = "The chosen date range exceeds the reservation" +
            " constraint. Choose less days";
    public static final String DATES_ARE_NOT_VALID_MESSAGE = "Arrival or departure date has not the yyyy-MM-dd format";
    public static final String MISSING_PARAMS_INITIAL_MESSAGE = "The following parameter(s) were missing: ";
    public static final String NO_PARAMETERS_FOUND_MESSAGE = "Empty request";
    public static final String ARRIVAL_PARAM_MISSING_MESSAGE = "arrival date is missing";
    public static final String DEPARTURE_PARAM_MISSING_MESSAGE = "departure date is missing";
    public static final String BOOKING_ID_PARAM_MISSING_MESSAGE = "booking id is missing";
    public static final String STATUS_PARAM_MISSING_MESSAGE = "status is missing";
    public static final String YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS = "yyyy-MM-dd hh:mm:ss a";
    public static final String YEAR_MONTH_DAY = "yyyy-MM-dd";
}
