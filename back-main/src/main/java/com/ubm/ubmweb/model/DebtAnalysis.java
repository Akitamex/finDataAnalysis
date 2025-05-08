package com.ubm.ubmweb.model;

import java.math.BigDecimal;
import java.util.List;

public class DebtAnalysis {
    private List<String> rows;
    private List<String> accountsRecievableNames; //Дебиторская задолженность
    private BigDecimal accountsRecievableSum;
    private List<BigDecimal> accountsRecievable;
    private List<String> accountsPayableNames; //Кредиторская задолженность
    private BigDecimal accountsPayableSum;
    private List<BigDecimal> accountsPayable;
    private List<String> debtNames; //Kredity
    private List<BigDecimal> debts;
    private BigDecimal total;
    private List<DebtAnalysisProjectsRecievable> debtAnalysisProjectsRecievable;
    private List<DebtAnalysisProjectsPayable> debtAnalysisProjectsPayable;

    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public List<String> getRows() {
        return rows;
    }
    public void setRows(List<String> rows) {
        this.rows = rows;
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
    public List<String> getDebtNames() {
        return debtNames;
    }
    public void setDebtNames(List<String> debtNames) {
        this.debtNames = debtNames;
    }
    public List<BigDecimal> getDebts() {
        return debts;
    }
    public void setDebts(List<BigDecimal> debts) {
        this.debts = debts;
    }
    public List<DebtAnalysisProjectsRecievable> getDebtAnalysisProjectsRecievable() {
        return debtAnalysisProjectsRecievable;
    }
    public void setDebtAnalysisProjectsRecievable(List<DebtAnalysisProjectsRecievable> debtAnalysisProjectsRecievable) {
        this.debtAnalysisProjectsRecievable = debtAnalysisProjectsRecievable;
    }
    public List<DebtAnalysisProjectsPayable> getDebtAnalysisProjectsPayable() {
        return debtAnalysisProjectsPayable;
    }
    public void setDebtAnalysisProjectsPayable(List<DebtAnalysisProjectsPayable> debtAnalysisProjectsPayable) {
        this.debtAnalysisProjectsPayable = debtAnalysisProjectsPayable;
    }
}
