package org.example.service.exceptions;

public class ObjectNotUpToDatedException extends RuntimeException {
    public ObjectNotUpToDatedException() {
    }

    public ObjectNotUpToDatedException(String message) {
        super(message);
    }

    public ObjectNotUpToDatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectNotUpToDatedException(Throwable cause) {
        super(cause);
    }

    public ObjectNotUpToDatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
