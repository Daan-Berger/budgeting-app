package com.daanberger.budgeting.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daanberger.budgeting.controllers.dto.AssignCategoryRequest;
import com.daanberger.budgeting.controllers.dto.CreateTransactionRequest;
import com.daanberger.budgeting.controllers.dto.TransactionResponse;
import com.daanberger.budgeting.domain.Category;
import com.daanberger.budgeting.domain.Transaction;
import com.daanberger.budgeting.exceptions.CategoryNotFoundException;
import com.daanberger.budgeting.exceptions.TransactionNotFoundException;
import com.daanberger.budgeting.repositories.CategoryRepository;
import com.daanberger.budgeting.repositories.TransactionRepository;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public void createTransaction(CreateTransactionRequest request) {
        Category category = null;

        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));
        }

        Transaction transaction = new Transaction(
                request.date(),
                request.amount(),
                request.counterparty(),
                request.counterpartyIban(),
                request.description(),
                category);

        transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        return TransactionResponse.from(transaction);
    }

    @Transactional
    public void assignCategory(UUID transactionId, AssignCategoryRequest request) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        transaction.setCategory(category);
    }
}
