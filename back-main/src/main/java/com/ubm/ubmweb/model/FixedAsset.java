package com.ubm.ubmweb.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
@Table(name = "fixed_assets")
public class FixedAsset {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
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
    @JsonIgnore
    private Counterparty counterparty;

    @ManyToOne
    @JoinColumn(name = "legal_entity_id", nullable = true)
    @JsonIgnore
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
    @JsonIgnore
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