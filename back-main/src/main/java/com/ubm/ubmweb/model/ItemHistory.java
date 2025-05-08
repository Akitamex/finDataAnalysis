package com.ubm.ubmweb.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "item_history")
public class ItemHistory {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonIgnore
    private Item item;

    @Column(name = "is_incoming", nullable = false)
    private Boolean isIncoming;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "total")
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name ="counterparty_id",nullable = true)
    @JsonIgnore
    private Counterparty counterparty;

    @ManyToOne
    @JoinColumn(name = "legal_entity_id",nullable = false)
    @JsonIgnore
    private LegalEntity legalEntity;

    @Column(name = "is_initial_balance")
    private Boolean isInitialBalance;

    @ManyToOne
    @JoinColumn(name = "obligation_id", nullable = true)
    @JsonIgnore
    private Obligation obligation;

    public Obligation getObligation() {
        return obligation;
    }

    public void setObligation(Obligation obligation) {
        this.obligation = obligation;
    }

    public Boolean isIncoming() {
        return isIncoming;
    }

    public void setIncoming(Boolean isIncoming) {
        this.isIncoming = isIncoming;
    }
    public Boolean isInitialBalance() {
        return isInitialBalance;
    }

    public void setInitialBalance(Boolean isInitialBalance) {
        this.isInitialBalance = isInitialBalance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Counterparty getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(Counterparty counterparty) {
        this.counterparty = counterparty;
    }

    public LegalEntity getLegalEntity() {
        return legalEntity;
    }

    public void setLegalEntity(LegalEntity legalEntity) {
        this.legalEntity = legalEntity;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}
