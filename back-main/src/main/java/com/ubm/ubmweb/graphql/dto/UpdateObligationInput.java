package com.ubm.ubmweb.graphql.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class UpdateObligationInput {
    private UUID id;
    private BigDecimal balance;
    private String currency;
    private UUID counterpartyId;
    private UUID legalEntityId;
    private UUID projectId;
    private String description;
    private List<CreateItemHistoryInput> itemHistories;
    
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
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
    public UUID getProjectId() {
        return projectId;
    }
    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<CreateItemHistoryInput> getItemHistories() {
        return itemHistories;
    }
    public void setItemHistories(List<CreateItemHistoryInput> itemHistories) {
        this.itemHistories = itemHistories;
    }

}
