package org.example.core.exceptions;

import org.example.core.dto.errors.ErrorResponse;

import java.util.List;

public class NullArgumentException extends CommonErrorException {
    public NullArgumentException(List<ErrorResponse> errors) {
        super(errors);
    }


}
