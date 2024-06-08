package org.example.dao.exceptions;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.exceptions.CommonInternalErrorException;

import java.util.List;

public class UpdatingDBDataException extends CommonInternalErrorException {

    public UpdatingDBDataException(List<ErrorResponse> errors) {
        super(errors);
    }

    public UpdatingDBDataException(Throwable cause, List<ErrorResponse> errors) {
        super(cause, errors);
    }

}
