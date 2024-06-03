package org.example.core.exceptions;

import org.example.core.dto.errors.ErrorResponse;

import java.util.List;

public class NullArgumentException extends CommonErrorException {
    public NullArgumentException(List<ErrorResponse> errors) {
        super(errors);
    }

    public NullArgumentException(String message, List<ErrorResponse> errors) {
        super(message, errors);
    }

    public NullArgumentException(String message, Throwable cause, List<ErrorResponse> errors) {
        super(message, cause, errors);
    }

    public NullArgumentException(Throwable cause, List<ErrorResponse> errors) {
        super(cause, errors);
    }

    public NullArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<ErrorResponse> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errors);
    }
}
