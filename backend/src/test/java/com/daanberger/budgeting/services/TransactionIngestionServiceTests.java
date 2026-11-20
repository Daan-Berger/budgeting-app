package com.daanberger.budgeting.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.daanberger.budgeting.controllers.dto.ImportResultDto;
import com.daanberger.budgeting.controllers.dto.RawTransactionDto;
import com.daanberger.budgeting.domain.Transaction;
import com.daanberger.budgeting.parsing.BankCsvParser;
import com.daanberger.budgeting.repositories.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionIngestionServiceTests {
    @Mock
    BankCsvParser parser;

    @Mock
    TransactionRepository repository;

    @InjectMocks
    TransactionIngestionService service;

    @Test
    void ingestFromCsv_WithoutExistingTransactions_ShouldSaveTransaction() {
        RawTransactionDto dto = new RawTransactionDto(
                LocalDate.of(2026, 10, 26),
                new BigDecimal("-15.50"),
                "SAMPLE STORE 999999",
                null,
                "Purchase");

        when(parser.parseDto(any(InputStream.class)))
                .thenReturn(List.of(dto));

        ImportResultDto result = service.ingestFromCsv(new ByteArrayInputStream(new byte[0]));

        assertEquals(new ImportResultDto(1, 1, 0), result);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(repository).save(captor.capture());

        Transaction savedTransaction = captor.getValue();

        assertEquals(dto.date(), savedTransaction.getDate());
        assertEquals(dto.amount(), savedTransaction.getAmount());
        assertEquals(dto.counterparty(), savedTransaction.getCounterparty());
        assertNull(savedTransaction.getCategory());
    }

    @Test
    void ingestFromCsv_WithExistingTransaction_ShouldNotSaveTransaction() {
        RawTransactionDto dto = new RawTransactionDto(
                LocalDate.of(2026, 10, 26),
                new BigDecimal("-15.50"),
                "SAMPLE STORE 999999",
                null,
                "Purchase");

        when(parser.parseDto(any(InputStream.class)))
                .thenReturn(List.of(dto));

        when(repository.existsByDateAndAmountAndDescription(
                dto.date(), dto.amount(), dto.description()))
                .thenReturn(true);

        ImportResultDto result = service.ingestFromCsv(new ByteArrayInputStream(new byte[0]));

        assertEquals(new ImportResultDto(1, 0, 1), result);
        verify(repository, never()).save(any(Transaction.class));
    }

    @Test
    void ingestFromCsv_WithMixOfNewAndExistingTransactions_ShouldCountBoth() {
        RawTransactionDto existing = rawTransaction("-15.50", "Purchase REF. : 1");
        RawTransactionDto newOne = rawTransaction("-4.20", "Coffee REF. : 2");
        RawTransactionDto newTwo = rawTransaction("-60.00", "Groceries REF. : 3");

        when(parser.parseDto(any(InputStream.class)))
                .thenReturn(List.of(existing, newOne, newTwo));
        when(repository.existsByDateAndAmountAndDescription(
                existing.date(), existing.amount(), existing.description()))
                .thenReturn(true);

        ImportResultDto result = service.ingestFromCsv(new ByteArrayInputStream(new byte[0]));

        assertEquals(new ImportResultDto(3, 2, 1), result);
        verify(repository, times(2)).save(any(Transaction.class));
    }

    @Test
    void ingestFromCsv_WithNoRows_ShouldReturnZeroCounts() {
        when(parser.parseDto(any(InputStream.class))).thenReturn(List.of());

        ImportResultDto result = service.ingestFromCsv(new ByteArrayInputStream(new byte[0]));

        assertEquals(new ImportResultDto(0, 0, 0), result);
        verify(repository, never()).save(any(Transaction.class));
    }

    private static RawTransactionDto rawTransaction(String amount, String description) {
        return new RawTransactionDto(
                LocalDate.of(2026, 10, 26),
                new BigDecimal(amount),
                "SAMPLE STORE 999999",
                null,
                description);
    }
}
