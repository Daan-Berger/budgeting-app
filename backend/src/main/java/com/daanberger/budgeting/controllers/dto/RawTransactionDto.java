package com.daanberger.budgeting.controllers.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RawTransactionDto(
        @NotNull LocalDate date,
        @NotNull BigDecimal amount,
        @NotBlank String counterparty,
        @NotBlank String counterpartyIban,
        String description) {
}
