package com.daanberger.budgeting.domain.dto;

import com.daanberger.budgeting.domain.BudgetBucket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryDTO(@NotBlank String name, @NotNull BudgetBucket budgetBucket) {
}
