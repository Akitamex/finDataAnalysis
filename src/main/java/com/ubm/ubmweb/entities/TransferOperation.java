package com.ubm.ubmweb.entities;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("TRANSFER")
public class TransferOperation extends Operation{
    
    @ManyToOne
    @JoinColumn(name="to_bankAcc_id",nullable = true)
    private BankAccount toBankAccount;
    @ManyToOne
    @JoinColumn(name="from_bankAcc_id",nullable = true)
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
