package com.daanberger.budgeting.controllers;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.daanberger.budgeting.controllers.dto.AssignCategoryRequest;
import com.daanberger.budgeting.controllers.dto.CreateTransactionRequest;
import com.daanberger.budgeting.controllers.dto.TransactionResponse;
import com.daanberger.budgeting.services.TransactionIngestionService;
import com.daanberger.budgeting.services.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final TransactionService service;

    private final TransactionIngestionService ingestionService;

    public TransactionController(TransactionService service, TransactionIngestionService ingestionService) {
        this.service = service;
        this.ingestionService = ingestionService;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        service.createTransaction(request);
    }

    @GetMapping("/{id}")
    public TransactionResponse getTransaction(@PathVariable UUID id) {
        return service.getTransaction(id);
    }

    @PatchMapping("/{id}/assign-category")
    public void assignCategory(@PathVariable UUID id, @Valid @RequestBody AssignCategoryRequest request) {
        service.assignCategory(id, request);
    }

    @PostMapping("/import")
    public void importCsv(@RequestParam("file") MultipartFile file) throws IOException {
        ingestionService.ingestFromCsv(file.getInputStream());
    }
}
