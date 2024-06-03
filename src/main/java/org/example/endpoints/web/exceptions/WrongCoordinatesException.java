package org.example.endpoints.web.exceptions;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.exceptions.CommonErrorException;

import java.util.List;

public class WrongCoordinatesException extends CommonErrorException {
    public WrongCoordinatesException(List<ErrorResponse> errors) {
        super(errors);
    }

    public WrongCoordinatesException(String message, List<ErrorResponse> errors) {
        super(message, errors);
    }

    public WrongCoordinatesException(String message, Throwable cause, List<ErrorResponse> errors) {
        super(message, cause, errors);
    }

    public WrongCoordinatesException(Throwable cause, List<ErrorResponse> errors) {
        super(cause, errors);
    }

    public WrongCoordinatesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<ErrorResponse> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errors);
    }
}
