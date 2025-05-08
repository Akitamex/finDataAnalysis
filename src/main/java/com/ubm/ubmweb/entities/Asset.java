package com.ubm.ubmweb.entities;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "assets", uniqueConstraints = {@UniqueConstraint(columnNames = {"company_id", "name"})})
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "name")
    private String name;

    @Column(name = "amount")
    private Long quantity;

    @Column(name = "remaining_cost")
    private BigDecimal remainingCost;

    @Column(name = "whole_cost")
    private BigDecimal wholeCost;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<FixedAsset> fixedAssets;

    public List<FixedAsset> getFixedAssets() {
        return fixedAssets;
    }
    public void setFixedAssets(List<FixedAsset> fixedAssets) {
        this.fixedAssets = fixedAssets;
    }
    public void addFixedAsset(FixedAsset fixedAsset) {
        fixedAssets.add(fixedAsset);
        fixedAsset.setAsset(this);
    }

    public void removeFixedAsset(FixedAsset fixedAsset) {
        fixedAssets.remove(fixedAsset);
        fixedAsset.setAsset(null);
    }


    public BigDecimal getWholeCost() {
        return wholeCost;
    }

    public void setWholeCost(BigDecimal wholeCost) {
        this.wholeCost = wholeCost;
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

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getRemainingCost() {
        return remainingCost;
    }

    public void setRemainingCost(BigDecimal remainingCost) {
        this.remainingCost = remainingCost;
    }

    
}
