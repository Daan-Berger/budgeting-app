package com.daanberger.budgeting.exceptions;

public class CategoryAlreadyExistsException extends RuntimeException {

    public CategoryAlreadyExistsException(String name) {
        super("Category '" + name + "' already exists");
    }
}
