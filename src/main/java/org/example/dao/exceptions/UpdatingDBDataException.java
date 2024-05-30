package org.example.dao.exceptions;

public class UpdatingDBDataException extends RuntimeException {

    public UpdatingDBDataException() {
    }

    public UpdatingDBDataException(String message) {
        super(message);
    }

    public UpdatingDBDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdatingDBDataException(Throwable cause) {
        super(cause);
    }

    public UpdatingDBDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
