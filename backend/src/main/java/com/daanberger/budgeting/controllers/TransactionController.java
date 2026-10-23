package com.daanberger.budgeting.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.daanberger.budgeting.domain.dto.CreateTransactionRequest;
import com.daanberger.budgeting.services.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        service.createTransaction(request);
    }

}
