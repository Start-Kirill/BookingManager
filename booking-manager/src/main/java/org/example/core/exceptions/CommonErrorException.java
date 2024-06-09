package org.example.core.exceptions;

import org.example.core.dto.errors.ErrorResponse;

import java.util.List;

public class CommonErrorException extends RuntimeException {

    private List<ErrorResponse> errors;

    public CommonErrorException(List<ErrorResponse> errors) {
        this.errors = errors;
    }


    public List<ErrorResponse> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorResponse> errors) {
        this.errors = errors;
    }
}
