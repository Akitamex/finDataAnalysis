package com.ubm.ubmweb.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type")
@Table(name = "bank_accounts", uniqueConstraints = {@UniqueConstraint(columnNames = {"company_id", "name"})})


public abstract class BankAccount {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "account_type", insertable = false, updatable = false)
    private String type;
    
    @ManyToOne
    @JoinColumn(name = "legal_entity_id",nullable = false)
    @JsonIgnore
    private LegalEntity legalEntity;
    
    @Column(name = "currency",nullable = false)
    private String currency;
    
    @Column(name = "balance", nullable = false) //0 by default
    private BigDecimal balance;

    
    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Operation> operations;

    
    public List<Operation> getOperations() {
        return operations;
    }
    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
    public void addOperation(Operation operation) {
        operations.add(operation);
        operation.setBankAccount(this);
    }

    public void removeOperation(Operation operation) {
        operations.remove(operation);
        operation.setBankAccount(null);
    }


    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
        this.company = company;
    }
    public LegalEntity getLegalEntity() {
        return legalEntity;
    }
    public void setLegalEntity(LegalEntity legalEntity) {
        this.legalEntity = legalEntity;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public void addBalance(BigDecimal balance) {
        
        this.balance = this.balance.add(balance);
    }
    public void substractBalance(BigDecimal balance){
        this.balance = this.balance.subtract(balance);
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
}
