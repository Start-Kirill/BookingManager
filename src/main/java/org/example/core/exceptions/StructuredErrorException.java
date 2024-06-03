package org.example.core.exceptions;

import java.util.Map;

public class StructuredErrorException extends RuntimeException {

    private Map<String, String> errors;

    public StructuredErrorException(Map<String, String> errors) {
        this.errors = errors;
    }

    public StructuredErrorException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    public StructuredErrorException(String message, Throwable cause, Map<String, String> errors) {
        super(message, cause);
        this.errors = errors;
    }

    public StructuredErrorException(Throwable cause, Map<String, String> errors) {
        super(cause);
        this.errors = errors;
    }

    public StructuredErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Map<String, String> errors) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
