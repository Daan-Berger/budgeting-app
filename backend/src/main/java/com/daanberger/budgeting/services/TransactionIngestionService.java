package com.daanberger.budgeting.services;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.daanberger.budgeting.controllers.dto.RawTransactionDto;
import com.daanberger.budgeting.domain.Transaction;
import com.daanberger.budgeting.parsing.BankCsvParser;
import com.daanberger.budgeting.repositories.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionIngestionService {
    private final BankCsvParser csvParser;
    private final TransactionRepository repository;

    public TransactionIngestionService(BankCsvParser csvParser, TransactionRepository repository) {
        this.csvParser = csvParser;
        this.repository = repository;
    }

    @Transactional
    public void ingestFromCsv(InputStream stream) {
        List<RawTransactionDto> transactionDtos = csvParser.parseDto(stream);
        for (RawTransactionDto dto : transactionDtos) {
            if (!repository.existsByDateAndAmountAndDescription(
                    dto.date(), dto.amount(), dto.description())) {
                repository.save(new Transaction(
                        dto.date(),
                        dto.amount(),
                        dto.counterparty(),
                        dto.counterpartyIban(),
                        dto.description(),
                        null));
            }
        }

    }
}
