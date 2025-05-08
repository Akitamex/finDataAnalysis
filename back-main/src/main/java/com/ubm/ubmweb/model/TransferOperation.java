package com.ubm.ubmweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("TRANSFER")
public class TransferOperation extends Operation{
    
    @ManyToOne
    @JoinColumn(name="to_bankAcc_id",nullable = true)
    @JsonIgnore
    private BankAccount toBankAccount;
    @ManyToOne
    @JoinColumn(name="from_bankAcc_id",nullable = true)
    @JsonIgnore
    private BankAccount fromBankAccount;
    
    public BankAccount getToBankAccount() {
        return toBankAccount;
    }
    public void setToBankAccount(BankAccount toBankAccount) {
        this.toBankAccount = toBankAccount;
    }
    public BankAccount getFromBankAccount() {
        return fromBankAccount;
    }
    public void setFromBankAccount(BankAccount fromBankAccount) {
        this.fromBankAccount = fromBankAccount;
    }
}
