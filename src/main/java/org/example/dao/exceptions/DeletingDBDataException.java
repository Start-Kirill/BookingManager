package org.example.dao.exceptions;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.exceptions.CommonInternalErrorException;

import java.util.List;

public class DeletingDBDataException extends CommonInternalErrorException {

    public DeletingDBDataException(List<ErrorResponse> errors) {
        super(errors);
    }

    public DeletingDBDataException(String message, List<ErrorResponse> errors) {
        super(message, errors);
    }

    public DeletingDBDataException(String message, Throwable cause, List<ErrorResponse> errors) {
        super(message, cause, errors);
    }

    public DeletingDBDataException(Throwable cause, List<ErrorResponse> errors) {
        super(cause, errors);
    }

    public DeletingDBDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<ErrorResponse> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errors);
    }
}
