package com.ubm.ubmweb.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BANK")
public class BankBankAccount extends BankAccount{

    @Column(name = "bank",nullable = true)
    private String bank;
    @Column(name = "BIC",nullable = true)
    private String bic;
    @Column(name = "correspondent account",nullable = true)
    private String correspondentAccount;
    @Column(name = "account_number",nullable = true)
    private String accountNumber;
    
    public String getBank() {
        return bank;
    }
    public void setBank(String bank) {
        this.bank = bank;
    }
    public String getBIC() {
        return bic;
    }
    public void setBIC(String bIC) {
        bic = bIC;
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
