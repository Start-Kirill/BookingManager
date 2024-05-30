package org.example.dao.exceptions;

public class DeletingDBDataException extends RuntimeException {

    public DeletingDBDataException() {
    }

    public DeletingDBDataException(String message) {
        super(message);
    }

    public DeletingDBDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeletingDBDataException(Throwable cause) {
        super(cause);
    }

    public DeletingDBDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
