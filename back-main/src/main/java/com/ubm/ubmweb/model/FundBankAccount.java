package com.ubm.ubmweb.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FUND")
public class FundBankAccount extends BankAccount{
    
}
