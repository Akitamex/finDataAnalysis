package com.ubm.ubmweb.graphql.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateItemHistoryInput {
    private UUID id;
    private UUID companyId;
    private UUID itemId;
    private String date;
    private BigDecimal quantity;
    private String currency;
    private BigDecimal price;
    private BigDecimal total;
    private UUID counterpartyId;
    private UUID legalEntityId;
    private Boolean isInitialBalance;
    private Boolean isIncoming;

    public Boolean isIncoming() {
        return isIncoming;
    }
    public void isIncoming(Boolean isIncoming) {
        this.isIncoming = isIncoming;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getCompanyId() {
        return companyId;
    }
    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
    public UUID getItemId() {
        return itemId;
    }
    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public BigDecimal getQuantity() {
        return quantity;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
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
    public UUID getCounterpartyId() {
        return counterpartyId;
    }
    public void setCounterpartyId(UUID counterpartyId) {
        this.counterpartyId = counterpartyId;
    }
    public UUID getLegalEntityId() {
        return legalEntityId;
    }
    public void setLegalEntityId(UUID legalEntityId) {
        this.legalEntityId = legalEntityId;
    }
    public Boolean isInitialBalance() {
        return isInitialBalance;
    }

    public void setInitialBalance(Boolean initialBalance) {
        isInitialBalance = initialBalance;
    }
}
