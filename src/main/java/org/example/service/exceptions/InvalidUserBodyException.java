package org.example.service.exceptions;

import org.example.core.exceptions.StructuredErrorException;

import java.util.Map;

public class InvalidUserBodyException extends StructuredErrorException {
    public InvalidUserBodyException(Map<String, String> errors) {
        super(errors);
    }

    public InvalidUserBodyException(String message, Map<String, String> errors) {
        super(message, errors);
    }

    public InvalidUserBodyException(String message, Throwable cause, Map<String, String> errors) {
        super(message, cause, errors);
    }

    public InvalidUserBodyException(Throwable cause, Map<String, String> errors) {
        super(cause, errors);
    }

    public InvalidUserBodyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Map<String, String> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errors);
    }
}
