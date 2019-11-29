package com.andromedacodelab.HighCbaCamp.exception;

public class RestApiError {
    private String timestamp;
    private Integer status;
    private String error;
    private String message;

    public RestApiError(String timestamp, Integer status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
