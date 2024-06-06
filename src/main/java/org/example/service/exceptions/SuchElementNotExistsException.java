package org.example.service.exceptions;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.exceptions.CommonErrorException;

import java.util.List;

public class SuchElementNotExistsException extends CommonErrorException {
    public SuchElementNotExistsException(List<ErrorResponse> errors) {
        super(errors);
    }

    public SuchElementNotExistsException(String message, List<ErrorResponse> errors) {
        super(message, errors);
    }

    public SuchElementNotExistsException(String message, Throwable cause, List<ErrorResponse> errors) {
        super(message, cause, errors);
    }

    public SuchElementNotExistsException(Throwable cause, List<ErrorResponse> errors) {
        super(cause, errors);
    }

    public SuchElementNotExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<ErrorResponse> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errors);
    }
}
