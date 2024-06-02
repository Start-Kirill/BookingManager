package org.example.service.exceptions;

public class PhoneNumberNotCorrectException extends RuntimeException {
    public PhoneNumberNotCorrectException() {
    }

    public PhoneNumberNotCorrectException(String message) {
        super(message);
    }

    public PhoneNumberNotCorrectException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhoneNumberNotCorrectException(Throwable cause) {
        super(cause);
    }

    public PhoneNumberNotCorrectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
