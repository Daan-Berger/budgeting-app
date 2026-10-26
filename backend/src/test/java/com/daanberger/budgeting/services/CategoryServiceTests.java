package com.daanberger.budgeting.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.daanberger.budgeting.controllers.dto.CategoryDTO;
import com.daanberger.budgeting.domain.BudgetBucket;
import com.daanberger.budgeting.domain.Category;
import com.daanberger.budgeting.exceptions.CategoryAlreadyExistsException;
import com.daanberger.budgeting.repositories.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {

    @Mock
    CategoryRepository repository;

    @InjectMocks
    CategoryService service;

    @Test
    void addCategory_whenNameIsNew_savesCategory() {
        CategoryDTO dto = new CategoryDTO("Groceries", BudgetBucket.NEEDS);

        when(repository.findByName("Groceries")).thenReturn(Optional.empty());

        service.addCategory(dto);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(repository).save(captor.capture());

        Category savedCategory = captor.getValue();
        assertEquals("Groceries", savedCategory.getName());
        assertEquals(BudgetBucket.NEEDS, savedCategory.getBucket());
    }

    @Test
    void addCategory_whenNameExists_throwsAndDoesNotSave() {
        CategoryDTO dto = new CategoryDTO("Groceries", BudgetBucket.NEEDS);

        when(repository.findByName("Groceries"))
                .thenReturn(Optional.of(new Category("Groceries", BudgetBucket.NEEDS)));

        assertThrows(CategoryAlreadyExistsException.class, () -> service.addCategory(dto));

        verify(repository, never()).save(any());
    }

}
