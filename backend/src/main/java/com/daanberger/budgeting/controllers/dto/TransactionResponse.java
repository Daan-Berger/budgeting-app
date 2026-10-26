package com.daanberger.budgeting.controllers.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.daanberger.budgeting.domain.Category;
import com.daanberger.budgeting.domain.Transaction;

public record TransactionResponse(
        UUID id,
        LocalDate date,
        BigDecimal amount,
        String counterparty,
        String counterpartyIban,
        String description,
        UUID categoryId,
        String categoryName) {

    public static TransactionResponse from(Transaction transaction) {
        Category category = transaction.getCategory();

        return new TransactionResponse(
                transaction.getId(),
                transaction.getDate(),
                transaction.getAmount(),
                transaction.getCounterparty(),
                transaction.getCounterpartyIban(),
                transaction.getDescription(),
                category != null ? category.getId() : null,
                category != null ? category.getName() : null);
    }
}
