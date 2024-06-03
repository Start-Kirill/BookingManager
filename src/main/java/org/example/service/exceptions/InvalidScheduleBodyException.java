package org.example.service.exceptions;

import org.example.core.exceptions.StructuredErrorException;

import java.util.Map;

public class InvalidScheduleBodyException extends StructuredErrorException {
    public InvalidScheduleBodyException(Map<String, String> errors) {
        super(errors);
    }

    public InvalidScheduleBodyException(String message, Map<String, String> errors) {
        super(message, errors);
    }

    public InvalidScheduleBodyException(String message, Throwable cause, Map<String, String> errors) {
        super(message, cause, errors);
    }

    public InvalidScheduleBodyException(Throwable cause, Map<String, String> errors) {
        super(cause, errors);
    }

    public InvalidScheduleBodyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Map<String, String> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errors);
    }
}
