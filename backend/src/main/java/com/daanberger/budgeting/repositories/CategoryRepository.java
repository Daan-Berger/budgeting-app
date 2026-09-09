package com.daanberger.budgeting.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daanberger.budgeting.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}