package com.daanberger.budgeting.services;

import org.springframework.stereotype.Service;

import com.daanberger.budgeting.domain.Category;
import com.daanberger.budgeting.domain.dto.CategoryDTO;
import com.daanberger.budgeting.repositories.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public void addCategory(CategoryDTO category) {
        Category newCategory = new Category(category.name());
        if (repository.findByName(category.name()).isEmpty()) {
            repository.save(newCategory);
        }
    }
}
