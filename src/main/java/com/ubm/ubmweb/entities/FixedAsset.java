package com.ubm.ubmweb.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;



@Entity
@Table(name = "fixed_assets")
public class FixedAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "amortise")
    private Boolean amortise;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "service_life_months", nullable = false)
    private Integer serviceLifeMonths;

    @ManyToOne
    @JoinColumn(name="counterparty_id",nullable = true)
    private Counterparty counterparty;

    @ManyToOne
    @JoinColumn(name = "legal_entity_id", nullable = true)
    private LegalEntity legalEntity;

    @Column(name = "remaining_cost")
    private BigDecimal remainingCost;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "include_vat")
    private Boolean includeVat;

    @Column(name = "vat")
    private Integer vat;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;
    

    public BigDecimal getRemainingCost() {
        return remainingCost;
    }

    public void setRemainingCost(BigDecimal remainingCost) {
        this.remainingCost = remainingCost;
    }

    public Boolean getAmortise() {
        return amortise;
    }

    public void setAmortise(Boolean amortise) {
        this.amortise = amortise;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
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

    public Counterparty getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(Counterparty counterparty) {
        this.counterparty = counterparty;
    }
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getVat() {
        return vat;
    }

    public void setVat(Integer vat) {
        this.vat = vat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Integer getServiceLifeMonths() {
        return serviceLifeMonths;
    }

    public void setServiceLifeMonths(Integer serviceLifeMonths) {
        this.serviceLifeMonths = serviceLifeMonths;
    }


    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Boolean getIncludeVat() {
        return includeVat;
    }

    public void setIncludeVat(Boolean includeVat) {
        this.includeVat = includeVat;
    }


}