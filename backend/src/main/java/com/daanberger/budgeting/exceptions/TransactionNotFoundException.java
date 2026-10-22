package com.daanberger.budgeting.exceptions;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(UUID id) {
        super("Transaction '" + id + "' not found");
    }
}
