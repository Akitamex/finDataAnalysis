package com.ubm.ubmweb.models;

import java.math.BigDecimal;
import java.util.List;

public class DebtAnalysisProjectsPayable {
    private String projectName;
    private List<String> accountsPayableNames;
    private BigDecimal accountsPayableSum;
    private List<BigDecimal> accountsPayable;
    
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public List<String> getAccountsPayableNames() {
        return accountsPayableNames;
    }
    public void setAccountsPayableNames(List<String> accountsPayableNames) {
        this.accountsPayableNames = accountsPayableNames;
    }
    public BigDecimal getAccountsPayableSum() {
        return accountsPayableSum;
    }
    public void setAccountsPayableSum(BigDecimal accountsPayableSum) {
        this.accountsPayableSum = accountsPayableSum;
    }
    public List<BigDecimal> getAccountsPayable() {
        return accountsPayable;
    }
    public void setAccountsPayable(List<BigDecimal> accountsPayable) {
        this.accountsPayable = accountsPayable;
    }
}
