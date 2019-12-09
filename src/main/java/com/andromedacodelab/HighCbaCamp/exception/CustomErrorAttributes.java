package com.andromedacodelab.HighCbaCamp.exception;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.RESOURCE_NOT_EXISTS_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.SERVER_ERROR_MESSAGE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        // Let Spring handle the error first, we will modify later
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);

        // format & update timestamp
        Object timestamp = errorAttributes.get("timestamp");
        if (timestamp == null) {
            errorAttributes.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")));
        } else {
            errorAttributes.put("timestamp", timestamp);
        }

        if (errorAttributes.get("status").equals(NOT_FOUND.value())) {
            errorAttributes.put("message", RESOURCE_NOT_EXISTS_MESSAGE);
        } else if (errorAttributes.get("status").equals(INTERNAL_SERVER_ERROR.value())){
            errorAttributes.put("message", SERVER_ERROR_MESSAGE);
            errorAttributes.remove("trace");
        }
        errorAttributes.remove("path");

        return errorAttributes;
    }
}
