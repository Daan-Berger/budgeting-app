package com.daanberger.budgeting.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
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

        service.ingestFromCsv(new ByteArrayInputStream(new byte[0]));

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

        service.ingestFromCsv(new ByteArrayInputStream(new byte[0]));

        verify(repository, org.mockito.Mockito.never()).save(any(Transaction.class));
    }
}
