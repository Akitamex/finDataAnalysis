package com.ubm.ubmweb.graphql.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class UpdateLoanInput {
    private UUID id;
    private UUID companyId;
    private String name;
    private String currency;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private LocalDate issueDate;
    private Integer loanTermMonths;
    private UUID counterpartyId;
    private UUID legalEntityId;

    public UUID getCompanyId() {
        return companyId;
    }
    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public BigDecimal getInterestRate() {
        return interestRate;
    }
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
    public LocalDate getIssueDate() {
        return issueDate;
    }
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
    public Integer getLoanTermMonths() {
        return loanTermMonths;
    }
    public void setLoanTermMonths(Integer loanTermMonths) {
        this.loanTermMonths = loanTermMonths;
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
}
