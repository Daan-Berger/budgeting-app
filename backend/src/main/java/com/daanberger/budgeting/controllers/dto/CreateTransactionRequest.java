package com.daanberger.budgeting.controllers.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTransactionRequest(
        @NotNull LocalDate date,
        @NotNull BigDecimal amount,
        @NotBlank String counterparty,
        String counterpartyIban,
        String description,
        UUID categoryId) {
}
