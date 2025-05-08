package com.ubm.ubmweb.graphql.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateFundBankAccountInput {
    private UUID companyId;
    private String name;
    private UUID legalEntityId;
    private String currency;
    private BigDecimal balance;

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public UUID getCompanyId() {
        return companyId;
    }
    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public UUID getLegalEntityId() {
        return legalEntityId;
    }
    public void setLegalEntityId(UUID legalEntityId) {
        this.legalEntityId = legalEntityId;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
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
}
