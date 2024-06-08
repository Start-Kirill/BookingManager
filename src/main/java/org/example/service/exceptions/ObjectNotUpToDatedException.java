package org.example.service.exceptions;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.exceptions.CommonErrorException;

import java.util.List;

public class ObjectNotUpToDatedException extends CommonErrorException {
    public ObjectNotUpToDatedException(List<ErrorResponse> errors) {
        super(errors);
    }

}
