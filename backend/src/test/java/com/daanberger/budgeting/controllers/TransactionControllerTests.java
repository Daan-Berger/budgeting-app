package com.daanberger.budgeting.controllers;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.daanberger.budgeting.controllers.dto.AssignCategoryRequest;
import com.daanberger.budgeting.controllers.dto.CreateTransactionRequest;
import com.daanberger.budgeting.controllers.dto.TransactionResponse;
import com.daanberger.budgeting.exceptions.CategoryNotFoundException;
import com.daanberger.budgeting.exceptions.TransactionNotFoundException;
import com.daanberger.budgeting.services.TransactionService;

import tools.jackson.databind.ObjectMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TransactionService service;

    @Test
    void addTransaction_WithValidPayload_ReturnsCreated() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                LocalDate.of(2026, 10, 25),
                new BigDecimal("-60"),
                "Delhaize",
                "BE68539007547034",
                "Groceries this week",
                null);

        mockMvc.perform(post("/api/transaction/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(service).createTransaction(request);
    }

    @Test
    void addTransaction_WithInvalidPayload_ReturnsBadRequest() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                null,
                new BigDecimal("-60"),
                "Delhaize",
                "BE68539007547034",
                "Groceries this week",
                null);

        mockMvc.perform(post("/api/transaction/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(service, never()).createTransaction(request);
    }

    @Test
    void getTransaction_WithExistingTransaction_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();

        TransactionResponse response = new TransactionResponse(
                id,
                LocalDate.of(2026, 10, 25),
                new BigDecimal("-60"),
                "Delhaize",
                "BE68539007547034",
                "Groceries this week",
                null,
                null);

        when(service.getTransaction(id)).thenReturn(response);

        mockMvc.perform(get("/api/transaction/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.counterparty").value("Delhaize"))
                .andExpect(jsonPath("$.amount").value(-60));

        verify(service).getTransaction(id);
    }

    @Test
    void getTransaction_WithNonExistingTransaction_ReturnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new TransactionNotFoundException(id))
                .when(service).getTransaction(id);

        mockMvc.perform(get("/api/transaction/{id}", id))
                .andExpect(status().isNotFound());

        verify(service).getTransaction(id);
    }

    @Test
    void assignCategory_WithExistingTransactionAndCategory_ReturnsOk() throws Exception {
        UUID transactionId = UUID.randomUUID();

        UUID categoryId = UUID.randomUUID();

        AssignCategoryRequest request = new AssignCategoryRequest(categoryId);

        mockMvc.perform(patch("/api/transaction/{id}/assign-category", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service).assignCategory(transactionId, request);
    }

    @Test
    void assignCategory_WithNonExistingTransaction_ReturnsNotFound() throws Exception {
        UUID transactionId = UUID.randomUUID();

        UUID categoryId = UUID.randomUUID();

        AssignCategoryRequest request = new AssignCategoryRequest(categoryId);

        doThrow(new TransactionNotFoundException(transactionId))
                .when(service).assignCategory(transactionId, request);

        mockMvc.perform(patch("/api/transaction/{id}/assign-category", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(service).assignCategory(transactionId, request);
    }

    @Test
    void assignCategory_WithNonExistingCategory_ReturnsNotFound() throws Exception {
        UUID transactionId = UUID.randomUUID();

        UUID categoryId = UUID.randomUUID();

        AssignCategoryRequest request = new AssignCategoryRequest(categoryId);

        doThrow(new CategoryNotFoundException(categoryId))
                .when(service).assignCategory(transactionId, request);

        mockMvc.perform(patch("/api/transaction/{id}/assign-category", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(service).assignCategory(transactionId, request);
    }
}
