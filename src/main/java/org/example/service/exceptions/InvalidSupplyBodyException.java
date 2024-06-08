package org.example.service.exceptions;

import org.example.core.exceptions.StructuredErrorException;

import java.util.Map;

public class InvalidSupplyBodyException extends StructuredErrorException {
    public InvalidSupplyBodyException(Map<String, String> errors) {
        super(errors);
    }

}
