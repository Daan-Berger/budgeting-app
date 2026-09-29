package com.daanberger.budgeting.controllers;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.daanberger.budgeting.exceptions.CategoryAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ProblemDetail HanleCategoryAlreadyExistsException(CategoryAlreadyExistsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }
}