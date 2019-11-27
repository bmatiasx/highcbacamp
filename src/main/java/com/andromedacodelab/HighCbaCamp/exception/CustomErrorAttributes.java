package com.andromedacodelab.HighCbaCamp.exception;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {
    /*private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");*/
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        // Let Spring handle the error first, we will modify later
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);

        // format & update timestamp
        Object timestamp = errorAttributes.get("timestamp");
        if (timestamp == null) {
            errorAttributes.put("timestamp", dateFormat.format(new Date()));
        } else {
            errorAttributes.put("timestamp", dateFormat.format((Date) timestamp));
        }

        if (errorAttributes.get("status").equals(NOT_FOUND.value())) {
            errorAttributes.put("message", "Resource does not exists");
        } else if (errorAttributes.get("status").equals(INTERNAL_SERVER_ERROR.value())){
            errorAttributes.put("message", "Oops, there was a problem on our end");
            errorAttributes.remove("trace");
        }
        errorAttributes.remove("path");

        return errorAttributes;
    }
}
