package org.example.service.exceptions;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.exceptions.CommonErrorException;

import java.util.List;

public class SuchElementNotExistsException extends CommonErrorException {
    public SuchElementNotExistsException(List<ErrorResponse> errors) {
        super(errors);
    }

}
