package org.example.core.exceptions;


import org.example.core.dto.errors.ErrorResponse;

import java.util.List;

public class CommonInternalErrorException extends RuntimeException {

    private List<ErrorResponse> errors;

    public CommonInternalErrorException(List<ErrorResponse> errors) {
        this.errors = errors;
    }

    public CommonInternalErrorException(String message, List<ErrorResponse> errors) {
        super(message);
        this.errors = errors;
    }

    public CommonInternalErrorException(String message, Throwable cause, List<ErrorResponse> errors) {
        super(message, cause);
        this.errors = errors;
    }

    public CommonInternalErrorException(Throwable cause, List<ErrorResponse> errors) {
        super(cause);
        this.errors = errors;
    }

    public CommonInternalErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<ErrorResponse> errors) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errors = errors;
    }

    public List<ErrorResponse> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorResponse> errors) {
        this.errors = errors;
    }
}
