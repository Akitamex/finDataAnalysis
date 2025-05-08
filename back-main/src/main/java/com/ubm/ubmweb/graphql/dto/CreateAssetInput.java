package com.ubm.ubmweb.graphql.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateAssetInput {
    private UUID companyId;
    private String name;
    private Long quantity;
    private BigDecimal remainingCost;
    private BigDecimal wholeCost;
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
    public BigDecimal getWholeCost() {
        return wholeCost;
    }
    public void setWholeCost(BigDecimal wholeCost) {
        this.wholeCost = wholeCost;
    }
}
