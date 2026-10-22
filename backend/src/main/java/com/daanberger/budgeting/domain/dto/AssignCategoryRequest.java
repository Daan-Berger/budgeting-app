package com.daanberger.budgeting.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AssignCategoryRequest(@NotNull UUID categoryId) {
}
