package com.daanberger.budgeting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "transaction", uniqueConstraints = {
        @UniqueConstraint(name = "transaction_date_amount_desc", columnNames = { "date", "amount", "description" })
})
public class Transaction {
    @Id
    private UUID id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String counterparty;

    private String counterpartyIban;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    protected Transaction() {
    }

    public Transaction(LocalDate date, BigDecimal amount, String counterparty, String counterpartyIban,
            String description, Category category) {
        this.id = UUID.randomUUID();
        this.date = date;
        this.amount = amount;
        this.counterparty = counterparty;
        this.counterpartyIban = counterpartyIban;
        this.description = description;
        this.category = category;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public String getCounterpartyIban() {
        return counterpartyIban;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
