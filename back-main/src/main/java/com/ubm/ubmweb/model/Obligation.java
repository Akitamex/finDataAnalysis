package com.ubm.ubmweb.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "obligations")
public class Obligation {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;

    @Column(name = "type") //Sent or Recieved from Counterparty
    private String type;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "currency")
    private String currency;

    @ManyToOne
    @JoinColumn(name = "counterparty_id",nullable = true)
    @JsonIgnore
    private Counterparty counterparty;

    @ManyToOne
    @JoinColumn(name = "legal_entity_id",nullable = true)
    @JsonIgnore
    private LegalEntity legalEntity;

    @ManyToOne
    @JoinColumn(name = "project_id",nullable = true)
    @JsonIgnore
    private Project project;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "description")
    private String description;


    @OneToMany(mappedBy = "obligation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemHistory> itemHistories;


    public List<ItemHistory> getItemHistories() {
        return itemHistories;
    }

    public void setItemHistories(List<ItemHistory> itemHistories) {
        this.itemHistories = itemHistories;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Counterparty getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(Counterparty counterparty) {
        counterparty.addObligation(this);
        this.counterparty = counterparty;
    }

    public LegalEntity getLegalEntity() {
        return legalEntity;
    }

    public void setLegalEntity(LegalEntity legalEntity) {
        this.legalEntity = legalEntity;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

}
