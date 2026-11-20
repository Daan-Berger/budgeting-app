package com.daanberger.budgeting.exceptions;

public class EmptyCsvFileException extends RuntimeException {

    public EmptyCsvFileException() {
        super("Uploaded CSV file cannot be empty.");
    }
}
