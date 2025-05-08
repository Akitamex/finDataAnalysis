package com.ubm.ubmweb.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
//import java.util.List;

import jakarta.persistence.*;

/*      !!TO-DO!!
 *      1.Attachments implementation!!!  
 */



@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "operation_type")
@Table(name = "operations")
public abstract class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_type", insertable = false, updatable = false)
    private String type;

    @Column(name = "balance",nullable = false)
    private BigDecimal balance;
    
    
    @Column(name = "date",nullable = true)
    private LocalDate date;
    
    @Column(name = "description",nullable = true)
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "article_id",nullable = true)
    private Article article;
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = true)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "bank_account_id", nullable = true)
    private BankAccount bankAccount;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name="counterparty_id",nullable = true)
    private Counterparty counterparty;

    @Column(name = "is_obligation", nullable = false)
    private Boolean isObligation;

    @ManyToOne
    @JoinColumn(name = "loan_payment_id", nullable = true)
    private LoanPayment loanPayment;

    
    public Boolean getIsObligation() {
        return isObligation;
    }
    public void setIsObligation(Boolean obligation) {
        this.isObligation = obligation;
    }
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

    public Article getArticle() {
        return article;
    }
    public void setArticle(Article article) {
        this.article = article;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
   
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    public BankAccount getBankAccount() {
        return bankAccount;
    }
    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
    public Counterparty getCounterparty() {
        return counterparty;
    }
    public void setCounterparty(Counterparty counterparty) {
        this.counterparty = counterparty;
    }
    public LoanPayment getLoanPayment() {
        return loanPayment;
    }
    public void setLoanPayment(LoanPayment loanPayment) {
        this.loanPayment = loanPayment;
    }
}
