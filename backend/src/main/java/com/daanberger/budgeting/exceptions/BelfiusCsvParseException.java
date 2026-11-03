package com.daanberger.budgeting.exceptions;

public class BelfiusCsvParseException extends RuntimeException {

    public BelfiusCsvParseException(String message) {
        super(message);
    }

    public BelfiusCsvParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
