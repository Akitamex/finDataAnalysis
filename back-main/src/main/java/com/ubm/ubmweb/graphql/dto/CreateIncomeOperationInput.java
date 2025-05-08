package com.ubm.ubmweb.graphql.dto;

import java.math.BigDecimal;
import java.util.UUID;

// import java.time.format.DateTimeFormatter;
// import java.time.format.DateTimeParseException;

public class CreateIncomeOperationInput {
    private UUID companyId;
    private BigDecimal balance;
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    private String date;
    public void setDate(String date) {
        this.date = date;
    }
    private String description;
    private UUID articleId;
    private UUID bankAccountId;
    private UUID projectId;
    private UUID counterpartyId;
    private Boolean isObligation;
    
    
    public UUID getCompanyId() {
        return companyId;
    }
    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    // public void setBalance(String balanceString) {
    //     try {
    //         this.balance = Long.parseLong(balanceString);
    //     } catch (NumberFormatException e) {
    //         throw new IllegalArgumentException("Balance must be a valid numeric value");
    //     }
    // }
    public String getDate() {
        return date;
    }
    // public void setDate(String dateString) {
    //     try {
    //         this.date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    //     } catch (DateTimeParseException e) {
    //         throw new IllegalArgumentException("Date must be in the format YYYY-MM-DD");
    //     }
    // }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public UUID getArticleId() {
        return articleId;
    }
    public void setArticleId(UUID articleId) {
        this.articleId = articleId;
    }
    public UUID getBankAccountId() {
        return bankAccountId;
    }
    public void setBankAccountId(UUID bankAccountId) {
        this.bankAccountId = bankAccountId;
    }
    public UUID getProjectId() {
        return projectId;
    }
    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
    public UUID getCounterpartyId() {
        return counterpartyId;
    }
    public void setCounterpartyId(UUID counterpartyId) {
        this.counterpartyId = counterpartyId;
    }
    public Boolean getIsObligation() {
        return isObligation;
    }
    public void setIsObligation(Boolean isObligation) {
        this.isObligation = isObligation;
    }
}
