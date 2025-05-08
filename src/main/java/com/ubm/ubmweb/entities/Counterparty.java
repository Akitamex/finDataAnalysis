package com.ubm.ubmweb.entities;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "counterparties", uniqueConstraints = {@UniqueConstraint(columnNames = {"company_id", "title"})})

public class Counterparty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title",nullable = false)
    private String title;
    
    @Column(name = "full name",nullable = true)
    private String fullName;
    
    @Column(name = "email",nullable = true)
    private String email;
    
    @Column(name = "phone number",nullable = true)
    private String phoneNum;
    
    @ManyToOne
    @JoinColumn(name = "counterparty_group_id")
    private CounterpartyGroup counterpartyGroup;
    
    @Column(name = "description",nullable = true)
    private String description;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "counterparty", fetch = FetchType.LAZY)
    private List<Operation> operations;

    @OneToMany(mappedBy = "counterparty", fetch = FetchType.LAZY)
    private List<Obligation> obligations;

    @OneToMany(mappedBy = "counterparty", fetch = FetchType.LAZY)
    private List<Loan> loans;

    @OneToMany(mappedBy = "counterparty", fetch = FetchType.EAGER)
    private List<FixedAsset> fixedAssets;

    @Column(name = "debt")
    private BigDecimal debt;

    public List<FixedAsset> getFixedAssets() {
        return fixedAssets;
    }
    public void setFixedAssets(List<FixedAsset> fixedAssets) {
        this.fixedAssets = fixedAssets;
    }
    public void addFixedAsset(FixedAsset fixedAsset) {
        fixedAssets.add(fixedAsset);
        fixedAsset.setCounterparty(this);
    }

    public void removeFixedAsset(FixedAsset fixedAsset) {
        fixedAssets.remove(fixedAsset);
        fixedAsset.setCounterparty(null);
    }

    public BigDecimal getDebt() {
        return debt;
    }
    public void setDebt(BigDecimal debt) {
        this.debt = debt;
    }
    public List<Loan> getLoans() {
        return loans;
    }
    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }
    public void addLoan(Loan loan) {
        loans.add(loan);
        loan.setCounterparty(this);
    }

    public void removeLoan(Loan loan) {
        loans.remove(loan);
        loan.setCounterparty(null);
    }
    public List<Obligation> getObligations() {
        return obligations;
    }
    public void setObligations(List<Obligation> obligations) {
        this.obligations = obligations;
    }
    public void addObligation(Obligation obligation) {
        obligations.add(obligation);
        obligation.setCounterparty(this);
    }

    public void removeObligation(Obligation obligation) {
        obligations.remove(obligation);
        obligation.setCounterparty(null);
    }
    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
        this.company = company;
    }
    public CounterpartyGroup getCounterpartyGroup() {
        return counterpartyGroup;
    }
    public void setCounterpartyGroup(CounterpartyGroup group) {
        this.counterpartyGroup = group;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<Operation> getOperations() {
        return operations;
    }
    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
    public void addOperation(Operation operation) {
        operations.add(operation);
        operation.setCounterparty(this);
    }

    public void removeOperation(Operation operation) {
        operations.remove(operation);
        operation.setCounterparty(null);
    }
    
}