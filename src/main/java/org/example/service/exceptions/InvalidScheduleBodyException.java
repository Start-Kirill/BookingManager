package org.example.service.exceptions;

import org.example.core.exceptions.StructuredErrorException;

import java.util.Map;

public class InvalidScheduleBodyException extends StructuredErrorException {
    public InvalidScheduleBodyException(Map<String, String> errors) {
        super(errors);
    }

}
