package org.example.dao.exceptions;

public class ReceivingDBDataException extends RuntimeException {

    public ReceivingDBDataException() {
    }

    public ReceivingDBDataException(String message) {
        super(message);
    }

    public ReceivingDBDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReceivingDBDataException(Throwable cause) {
        super(cause);
    }

    public ReceivingDBDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
