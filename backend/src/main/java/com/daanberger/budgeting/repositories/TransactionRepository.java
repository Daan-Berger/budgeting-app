package com.daanberger.budgeting.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daanberger.budgeting.domain.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    boolean existsByDateAndAmountAndDescription(LocalDate date, BigDecimal amount,
            String description);

}