package org.example.service.exceptions;

public class InvalidDurationException extends RuntimeException {

    public InvalidDurationException() {
    }

    public InvalidDurationException(String message) {
        super(message);
    }

    public InvalidDurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDurationException(Throwable cause) {
        super(cause);
    }

    public InvalidDurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
