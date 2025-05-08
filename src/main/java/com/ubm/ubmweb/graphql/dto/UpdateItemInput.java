package com.ubm.ubmweb.graphql.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class UpdateItemInput {
    private UUID id;
    private UUID companyId;
    private String name;
    private String unit;
    private String vendorCode;
    private String description;
    private BigDecimal defaultSellingPrice;
    private Boolean setDefault;
    private String dspCurrency;
    private Boolean setInitial;
    private BigDecimal initialQuantity;
    private String date;
    private BigDecimal initialPrice;
    private UUID legalEntityId;
    private String initialCurrency;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getCompanyId() {
        return companyId;
    }
    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getVendorCode() {
        return vendorCode;
    }
    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getDefaultSellingPrice() {
        return defaultSellingPrice;
    }
    public void setDefaultSellingPrice(BigDecimal defaultSellingPrice) {
        this.defaultSellingPrice = defaultSellingPrice;
    }
    public String getDspCurrency() {
        return dspCurrency;
    }
    public void setDspCurrency(String dspCurrency) {
        this.dspCurrency = dspCurrency;
    }
    public BigDecimal getInitialQuantity() {
        return initialQuantity;
    }
    public void setInitialQuantity(BigDecimal initialQuantity) {
        this.initialQuantity = initialQuantity;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public BigDecimal getInitialPrice() {
        return initialPrice;
    }
    public void setInitialPrice(BigDecimal initialPrice) {
        this.initialPrice = initialPrice;
    }
    public UUID getLegalEntityId() {
        return legalEntityId;
    }
    public void setLegalEntityId(UUID legalEntityId) {
        this.legalEntityId = legalEntityId;
    }
    public String getInitialCurrency() {
        return initialCurrency;
    }
    public void setInitialCurrency(String initialCurrency) {
        this.initialCurrency = initialCurrency;
    }
    public Boolean getSetDefault() {
        return setDefault;
    }
    public void setSetDefault(Boolean setDefault) {
        this.setDefault = setDefault;
    }
    public Boolean getSetInitial() {
        return setInitial;
    }
    public void setSetInitial(Boolean setInitial) {
        this.setInitial = setInitial;
    }
}
