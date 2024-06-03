package org.example.dao.exceptions;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.exceptions.CommonInternalErrorException;

import java.util.List;

public class UpdatingDBDataException extends CommonInternalErrorException {

    public UpdatingDBDataException(List<ErrorResponse> errors) {
        super(errors);
    }

    public UpdatingDBDataException(String message, List<ErrorResponse> errors) {
        super(message, errors);
    }

    public UpdatingDBDataException(String message, Throwable cause, List<ErrorResponse> errors) {
        super(message, cause, errors);
    }

    public UpdatingDBDataException(Throwable cause, List<ErrorResponse> errors) {
        super(cause, errors);
    }

    public UpdatingDBDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<ErrorResponse> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errors);
    }
}
