package com.ubm.ubmweb.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FUND")
public class FundBankAccount extends BankAccount{
    
}
