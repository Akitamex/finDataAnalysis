package com.ubm.ubmweb.graphql.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;

public class CreateObligationInput {
    private UUID companyId;
    private String type;
    private BigDecimal balance;
    private String currency;
    private UUID counterpartyId;
    private UUID legalEntityId;
    private UUID projectId;
    private String date;
    private String description;
    private List<CreateItemHistoryInput> itemHistories;
    
    public List<CreateItemHistoryInput> getItemHistories() {
        return itemHistories;
    }
    public void setItemHistories(List<CreateItemHistoryInput> itemHistories) {
        this.itemHistories = itemHistories;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        if(type.equals("IN") || type.equals("OUT")){
            this.type = type;
        }
        else{
            throw new UnauthorizedAccessException("Type can either be IN or OUT");
        }
    }
    public UUID getCompanyId() {
        return companyId;
    }
    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
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
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
