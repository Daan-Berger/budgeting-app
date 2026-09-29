package com.daanberger.budgeting.domain;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "category")
public class Category {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetBucket bucket;

    protected Category() {

    }

    public Category(String name, BudgetBucket budgetBucket) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.bucket = budgetBucket;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BudgetBucket getBucket() {
        return bucket;
    }
}
