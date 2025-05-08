package com.ubm.ubmweb.model;

public class PLProjectAttributes {
    private Boolean revenue;
    private Boolean directCostsVariables;
    private Boolean directCostsConstants;
    private Boolean taxes;
    
    public Boolean getRevenue() {
        return revenue;
    }
    public void setRevenue(Boolean revenue) {
        this.revenue = revenue;
    }
    public Boolean getDirectCostsVariables() {
        return directCostsVariables;
    }
    public void setDirectCostsVariables(Boolean directCostsVariables) {
        this.directCostsVariables = directCostsVariables;
    }
    public Boolean getDirectCostsConstants() {
        return directCostsConstants;
    }
    public void setDirectCostsConstants(Boolean directCostsConstants) {
        this.directCostsConstants = directCostsConstants;
    }
    public Boolean getTaxes() {
        return taxes;
    }
    public void setTaxes(Boolean taxes) {
        this.taxes = taxes;
    }
}
