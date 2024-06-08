package org.example.core.exceptions;

import java.util.Map;

public class StructuredErrorException extends RuntimeException {

    private Map<String, String> errors;

    public StructuredErrorException(Map<String, String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
