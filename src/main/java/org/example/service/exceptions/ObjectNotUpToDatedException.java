package org.example.service.exceptions;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.exceptions.CommonErrorException;

import java.util.List;

public class ObjectNotUpToDatedException extends CommonErrorException {
    public ObjectNotUpToDatedException(List<ErrorResponse> errors) {
        super(errors);
    }

    public ObjectNotUpToDatedException(String message, List<ErrorResponse> errors) {
        super(message, errors);
    }

    public ObjectNotUpToDatedException(String message, Throwable cause, List<ErrorResponse> errors) {
        super(message, cause, errors);
    }

    public ObjectNotUpToDatedException(Throwable cause, List<ErrorResponse> errors) {
        super(cause, errors);
    }

    public ObjectNotUpToDatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<ErrorResponse> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errors);
    }
}
