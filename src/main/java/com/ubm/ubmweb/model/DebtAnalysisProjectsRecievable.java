package com.ubm.ubmweb.model;

import java.math.BigDecimal;
import java.util.List;

public class DebtAnalysisProjectsRecievable {
    private String projectName;
    private List<String> accountsRecievableNames;
    private BigDecimal accountsRecievableSum;
    private List<BigDecimal> accountsRecievable;
    
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public List<String> getAccountsRecievableNames() {
        return accountsRecievableNames;
    }
    public void setAccountsRecievableNames(List<String> accountsRecievableNames) {
        this.accountsRecievableNames = accountsRecievableNames;
    }
    public BigDecimal getAccountsRecievableSum() {
        return accountsRecievableSum;
    }
    public void setAccountsRecievableSum(BigDecimal accountsRecievableSum) {
        this.accountsRecievableSum = accountsRecievableSum;
    }
    public List<BigDecimal> getAccountsRecievable() {
        return accountsRecievable;
    }
    public void setAccountsRecievable(List<BigDecimal> accountsRecievable) {
        this.accountsRecievable = accountsRecievable;
    }
}
