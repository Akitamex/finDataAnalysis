package com.ubm.ubmweb.graphql.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class UpdateFixedAssetInput {
    private UUID id;
    private UUID companyId;
    private Long quantity;
    private BigDecimal unitPrice;
    private String currency;
    private String purchaseDate;
    private Integer serviceLifeMonths;
    private UUID counterpartyId;
    private UUID legalEntityId;
    private Boolean includeVat;
    private Integer vat;

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
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getPurchaseDate() {
        return purchaseDate;
    }
    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    public Integer getServiceLifeMonths() {
        return serviceLifeMonths;
    }
    public void setServiceLifeMonths(Integer serviceLifeMonths) {
        this.serviceLifeMonths = serviceLifeMonths;
    }
    public UUID getCounterpartyId() {
        return counterpartyId;
    }
    public void setCounterpartyId(UUID counterpartyId) {
        this.counterpartyId = counterpartyId;
    }
    public UUID getLegalEntityId() {
        return legalEntityId;
    }
    public void setLegalEntityId(UUID legalEntityId) {
        this.legalEntityId = legalEntityId;
    }
    public Boolean getIncludeVat() {
        return includeVat;
    }
    public void setIncludeVat(Boolean includeVat) {
        this.includeVat = includeVat;
    }
    public Integer getVat() {
        return vat;
    }
    public void setVat(Integer vat) {
        this.vat = vat;
    }
}
