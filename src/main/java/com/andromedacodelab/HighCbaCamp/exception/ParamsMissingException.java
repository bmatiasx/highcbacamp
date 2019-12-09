package com.andromedacodelab.HighCbaCamp.exception;

import java.util.Iterator;
import java.util.List;

import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.MISSING_PARAMS_INITIAL_MESSAGE;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.NO_PARAMETERS_FOUND_MESSAGE;

public class ParamsMissingException extends RuntimeException {
    private List<String> parameters;

    public ParamsMissingException(List<String> parameters) {
        this.parameters = parameters;
    }

    public ParamsMissingException() {

    }

    @Override
    public String getMessage() {
        StringBuffer message = new StringBuffer(MISSING_PARAMS_INITIAL_MESSAGE);
        if (parameters == null) {
            message.append(NO_PARAMETERS_FOUND_MESSAGE);
        } else {
            Iterator<String> iterator = parameters.iterator();

            while (iterator.hasNext()) {
                message.append(iterator.next());
                if (iterator.hasNext()) {
                    message.append(", ");
                } else {
                    message.append(".");
                }
            }
        }
        return message.toString();
    }
}
