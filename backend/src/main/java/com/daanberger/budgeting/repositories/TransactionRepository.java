package com.daanberger.budgeting.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daanberger.budgeting.domain.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

}