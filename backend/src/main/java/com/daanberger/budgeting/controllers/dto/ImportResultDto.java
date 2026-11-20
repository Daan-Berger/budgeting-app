package com.daanberger.budgeting.controllers.dto;

public record ImportResultDto(int totalRows, int imported, int skippedDuplicates) {
}
