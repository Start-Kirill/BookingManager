package org.example.dao.exceptions;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.exceptions.CommonInternalErrorException;

import java.util.List;

public class CreatingDBDataException extends CommonInternalErrorException {


    public CreatingDBDataException(Throwable cause, List<ErrorResponse> errors) {
        super(cause, errors);
    }

}
