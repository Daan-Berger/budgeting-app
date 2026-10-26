package com.daanberger.budgeting.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.daanberger.budgeting.controllers.dto.AssignCategoryRequest;
import com.daanberger.budgeting.controllers.dto.CreateTransactionRequest;
import com.daanberger.budgeting.controllers.dto.TransactionResponse;
import com.daanberger.budgeting.domain.BudgetBucket;
import com.daanberger.budgeting.domain.Category;
import com.daanberger.budgeting.domain.Transaction;
import com.daanberger.budgeting.exceptions.CategoryNotFoundException;
import com.daanberger.budgeting.exceptions.TransactionNotFoundException;
import com.daanberger.budgeting.repositories.CategoryRepository;
import com.daanberger.budgeting.repositories.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    TransactionService service;

    @Test
    void addTransaction_WhenInputIsValid_SavesTransaction() {
        CreateTransactionRequest request = createRequest(null);

        service.createTransaction(request);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());

        Transaction savedTransaction = captor.getValue();
        assertEquals(LocalDate.of(2026, 10, 22), savedTransaction.getDate());
        assertEquals(new BigDecimal("-42.50"), savedTransaction.getAmount());
        assertEquals("COLRUYT MECHELEN", savedTransaction.getCounterparty());
        assertEquals("BE68539007547034", savedTransaction.getCounterpartyIban());
        assertEquals("Weekly groceries", savedTransaction.getDescription());
        assertNull(savedTransaction.getCategory());
    }

    @Test
    void addTransaction_WhenCategoryExists_SavesWithCategory() {
        Category category = new Category("Groceries", BudgetBucket.NEEDS);
        CreateTransactionRequest request = createRequest(category.getId());

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        service.createTransaction(request);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());

        assertSame(category, captor.getValue().getCategory());
    }

    @Test
    void addTransaction_WhenCategoryDoesNotExist_ThrowsAndDoesNotSave() {
        UUID categoryUuid = UUID.randomUUID();
        CreateTransactionRequest request = createRequest(categoryUuid);

        when(categoryRepository.findById(categoryUuid)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> service.createTransaction(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getTransaction_WhenTransactionExists_ReturnsTransaction() {
        Transaction transaction = sampleTransaction(null);

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        TransactionResponse response = service.getTransaction(transaction.getId());

        assertEquals(LocalDate.of(2026, 10, 22), response.date());
        assertEquals(new BigDecimal("-42.50"), response.amount());
        assertEquals("COLRUYT MECHELEN", response.counterparty());
        assertEquals("BE68539007547034", response.counterpartyIban());
        assertEquals("Weekly groceries", response.description());
        assertNull(response.categoryId());
        assertNull(response.categoryName());
    }

    @Test
    void getTransaction_WhenTransactionHasCategory_ReturnsCategoryFields() {
        Category category = new Category("Groceries", BudgetBucket.NEEDS);
        Transaction transaction = sampleTransaction(category);

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        TransactionResponse response = service.getTransaction(transaction.getId());

        assertEquals(category.getId(), response.categoryId());
        assertEquals("Groceries", response.categoryName());
    }

    @Test
    void getTransaction_WhenTransactionDoesNotExist_ThrowsNotFound() {
        UUID transactionUuid = UUID.randomUUID();

        when(transactionRepository.findById(transactionUuid)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> service.getTransaction(transactionUuid));
    }

    @Test
    void assignCategory_WhenTransactionAndCategoryAreValid_SetsCategory() {
        Transaction transaction = sampleTransaction(null);
        Category category = new Category("Groceries", BudgetBucket.NEEDS);

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        service.assignCategory(transaction.getId(), new AssignCategoryRequest(category.getId()));

        assertSame(category, transaction.getCategory());
    }

    @Test
    void assignCategory_WhenTransactionDoesNotExist_ThrowsNotFound() {
        UUID transactionUuid = UUID.randomUUID();

        when(transactionRepository.findById(transactionUuid)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class,
                () -> service.assignCategory(transactionUuid, new AssignCategoryRequest(UUID.randomUUID())));
    }

    @Test
    void assignCategory_WhenCategoryDoesNotExist_ThrowsNotFound() {
        Transaction transaction = sampleTransaction(null);
        UUID categoryUuid = UUID.randomUUID();

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        when(categoryRepository.findById(categoryUuid)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> service.assignCategory(transaction.getId(), new AssignCategoryRequest(categoryUuid)));
    }

    private static CreateTransactionRequest createRequest(UUID categoryId) {
        return new CreateTransactionRequest(
                LocalDate.of(2026, 10, 22),
                new BigDecimal("-42.50"),
                "COLRUYT MECHELEN",
                "BE68539007547034",
                "Weekly groceries",
                categoryId);
    }

    private static Transaction sampleTransaction(Category category) {
        return new Transaction(
                LocalDate.of(2026, 10, 22),
                new BigDecimal("-42.50"),
                "COLRUYT MECHELEN",
                "BE68539007547034",
                "Weekly groceries",
                category);
    }
}
