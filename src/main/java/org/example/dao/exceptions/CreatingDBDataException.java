package org.example.dao.exceptions;

public class CreatingDBDataException extends RuntimeException {

    public CreatingDBDataException() {
    }

    public CreatingDBDataException(String message) {
        super(message);
    }

    public CreatingDBDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreatingDBDataException(Throwable cause) {
        super(cause);
    }

    public CreatingDBDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
