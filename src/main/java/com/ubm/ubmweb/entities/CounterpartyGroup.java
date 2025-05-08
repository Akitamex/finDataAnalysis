package com.ubm.ubmweb.entities;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "counterparty_groups", uniqueConstraints = {@UniqueConstraint(columnNames = {"company_id", "name"})})
public class CounterpartyGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @OneToMany(mappedBy = "counterpartyGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Counterparty> counterparties;

    @ManyToOne
    @JoinColumn(name = "company_id")
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


    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}
