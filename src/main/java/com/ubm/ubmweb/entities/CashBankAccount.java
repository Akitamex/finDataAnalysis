package com.ubm.ubmweb.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CASH")
public class CashBankAccount extends BankAccount{
    
}
