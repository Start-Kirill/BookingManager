package org.example.dao.exceptions;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.exceptions.CommonInternalErrorException;

import java.util.List;

public class CreatingDBDataException extends CommonInternalErrorException {

    public CreatingDBDataException(List<ErrorResponse> errors) {
        super(errors);
    }

    public CreatingDBDataException(String message, List<ErrorResponse> errors) {
        super(message, errors);
    }

    public CreatingDBDataException(String message, Throwable cause, List<ErrorResponse> errors) {
        super(message, cause, errors);
    }

    public CreatingDBDataException(Throwable cause, List<ErrorResponse> errors) {
        super(cause, errors);
    }

    public CreatingDBDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<ErrorResponse> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errors);
    }
}
