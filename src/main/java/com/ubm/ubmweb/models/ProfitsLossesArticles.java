package com.ubm.ubmweb.models;

import java.math.BigDecimal;
import java.util.List;

public class ProfitsLossesArticles {
    private List<String> revenueNames;
    private List<List<BigDecimal>> revenue;
    private List<String> directCostsVariablesNames;
    private List<List<BigDecimal>> directCostsVariables;
    private List<String> directCostsConstantsNames;
    private List<List<BigDecimal>> directCostsConstants;
    private List<String> taxesNames;
    private List<List<BigDecimal>> taxes;
    

    public List<String> getRevenueNames() {
        return revenueNames;
    }
    public void setRevenueNames(List<String> revenueNames) {
        this.revenueNames = revenueNames;
    }
    public List<List<BigDecimal>> getRevenueRows() {
        return revenue;
    }
    public void setRevenue(List<List<BigDecimal>> revenue) {
        this.revenue = revenue;
    }
    public List<String> getDirectCostsVariablesNames() {
        return directCostsVariablesNames;
    }
    public void setDirectCostsVariablesNames(List<String> directCostsVariablesNames) {
        this.directCostsVariablesNames = directCostsVariablesNames;
    }
    public List<List<BigDecimal>> getDirectCostsVariables() {
        return directCostsVariables;
    }
    public void setDirectCostsVariables(List<List<BigDecimal>> directCostsVariables) {
        this.directCostsVariables = directCostsVariables;
    }
    public List<String> getDirectCostsConstantsNames() {
        return directCostsConstantsNames;
    }
    public void setDirectCostsConstantsNames(List<String> directCostsConstantsNames) {
        this.directCostsConstantsNames = directCostsConstantsNames;
    }
    public List<List<BigDecimal>> getDirectCostsConstants() {
        return directCostsConstants;
    }
    public void setDirectCostsConstants(List<List<BigDecimal>> directCostsConstants) {
        this.directCostsConstants = directCostsConstants;
    }
    public List<String> getTaxesNames() {
        return taxesNames;
    }
    public void setTaxesNames(List<String> taxesNames) {
        this.taxesNames = taxesNames;
    }
    public List<List<BigDecimal>> getTaxes() {
        return taxes;
    }
    public void setTaxes(List<List<BigDecimal>> taxes) {
        this.taxes = taxes;
    }
    
}
