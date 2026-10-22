package com.daanberger.budgeting.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.daanberger.budgeting.domain.BudgetBucket;
import com.daanberger.budgeting.domain.dto.CategoryDTO;
import com.daanberger.budgeting.exceptions.CategoryAlreadyExistsException;
import com.daanberger.budgeting.services.CategoryService;

import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CategoryService service;

    @Test
    void addCategory_withValidPayload_returnsCreated() throws Exception {
        CategoryDTO dto = new CategoryDTO("Groceries", BudgetBucket.NEEDS);

        mockMvc.perform(post("/api/category/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(service).addCategory(dto);
    }

    @Test
    void addCategory_withInvalidPayload_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/category/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"","budgetBucket":"NEEDS"}
                        """))
                .andExpect(status().isBadRequest());

        verify(service, never()).addCategory(any());
    }

    @Test
    void addCategory_alreadyExists_returnsConflict() throws Exception {
        CategoryDTO dto = new CategoryDTO("Groceries", BudgetBucket.NEEDS);

        doThrow(new CategoryAlreadyExistsException("Groceries"))
                .when(service).addCategory(any());

        mockMvc.perform(post("/api/category/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }
}
