package com.ubm.ubmweb.graphql.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class UpdateBankBankAccountInput {
    private UUID id;
    private UUID companyId;
    private String name;
    private UUID legalEntityId;
    private String currency;
    private BigDecimal balance;
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    private String bank;
    private String BIC;
    private String correspondentAccount;
    private String accountNumber;
    
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
    public String getBank() {
        return bank;
    }
    public void setBank(String bank) {
        this.bank = bank;
    }
    public String getBIC() {
        return BIC;
    }
    public void setBIC(String bIC) {
        BIC = bIC;
    }
    public String getCorrespondentAccount() {
        return correspondentAccount;
    }
    public void setCorrespondentAccount(String correspondentAccount) {
        this.correspondentAccount = correspondentAccount;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
