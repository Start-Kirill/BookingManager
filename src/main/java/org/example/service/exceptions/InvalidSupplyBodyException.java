package org.example.service.exceptions;

import org.example.core.exceptions.StructuredErrorException;

import java.util.Map;

public class InvalidSupplyBodyException extends StructuredErrorException {
    public InvalidSupplyBodyException(Map<String, String> errors) {
        super(errors);
    }

    public InvalidSupplyBodyException(String message, Map<String, String> errors) {
        super(message, errors);
    }

    public InvalidSupplyBodyException(String message, Throwable cause, Map<String, String> errors) {
        super(message, cause, errors);
    }

    public InvalidSupplyBodyException(Throwable cause, Map<String, String> errors) {
        super(cause, errors);
    }

    public InvalidSupplyBodyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Map<String, String> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errors);
    }
}
