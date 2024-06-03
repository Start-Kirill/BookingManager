package org.example.dao.exceptions;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.exceptions.CommonInternalErrorException;

import java.util.List;

public class ReceivingDBDataException extends CommonInternalErrorException {

    public ReceivingDBDataException(List<ErrorResponse> errors) {
        super(errors);
    }

    public ReceivingDBDataException(String message, List<ErrorResponse> errors) {
        super(message, errors);
    }

    public ReceivingDBDataException(String message, Throwable cause, List<ErrorResponse> errors) {
        super(message, cause, errors);
    }

    public ReceivingDBDataException(Throwable cause, List<ErrorResponse> errors) {
        super(cause, errors);
    }

    public ReceivingDBDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<ErrorResponse> errors) {
        super(message, cause, enableSuppression, writableStackTrace, errors);
    }
}
