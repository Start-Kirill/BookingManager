package org.example.dao.exceptions;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.exceptions.CommonInternalErrorException;

import java.util.List;

public class DeletingDBDataException extends CommonInternalErrorException {

    public DeletingDBDataException(List<ErrorResponse> errors) {
        super(errors);
    }


    public DeletingDBDataException(Throwable cause, List<ErrorResponse> errors) {
        super(cause, errors);
    }

}
