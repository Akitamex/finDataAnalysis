package com.ubm.ubmweb.model;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "counterparty_groups", uniqueConstraints = {@UniqueConstraint(columnNames = {"company_id", "name"})})
public class CounterpartyGroup {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @OneToMany(mappedBy = "counterpartyGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Counterparty> counterparties;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;

    
    public List<Counterparty> getCounterparties() {
        return counterparties;
    }

    public void setCounterparties(List<Counterparty> counterparties) {
        this.counterparties = counterparties;
    }
    
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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


}
