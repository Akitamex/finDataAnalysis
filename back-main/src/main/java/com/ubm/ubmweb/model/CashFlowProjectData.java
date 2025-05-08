package com.ubm.ubmweb.model;

import java.math.BigDecimal;
import java.util.List;

public class CashFlowProjectData {
    private String projectName;
    private List<String> projectIncomeRows;
    private List<List<BigDecimal>> projectIncome;
    private List<BigDecimal> projectIncomeSum;
    private List<String> projectExpenseRows;
    private List<List<BigDecimal>> projectExpense;
    private List<BigDecimal> projectExpenseSum;
    private List<BigDecimal> projectTotal;

    
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public List<String> getProjectIncomeRows() {
        return projectIncomeRows;
    }
    public void setProjectIncomeRows(List<String> projectIncomeRows) {
        this.projectIncomeRows = projectIncomeRows;
    }
    public List<List<BigDecimal>> getProjectIncome() {
        return projectIncome;
    }
    public void setProjectIncome(List<List<BigDecimal>> projectIncome) {
        this.projectIncome = projectIncome;
    }
    public List<BigDecimal> getProjectIncomeSum() {
        return projectIncomeSum;
    }
    public void setProjectIncomeSum(List<BigDecimal> projectIncomeSum) {
        this.projectIncomeSum = projectIncomeSum;
    }
    public List<String> getProjectExpenseRows() {
        return projectExpenseRows;
    }
    public void setProjectExpenseRows(List<String> projectExpenseRows) {
        this.projectExpenseRows = projectExpenseRows;
    }
    public List<List<BigDecimal>> getProjectExpense() {
        return projectExpense;
    }
    public void setProjectExpense(List<List<BigDecimal>> projectExpense) {
        this.projectExpense = projectExpense;
    }
    public List<BigDecimal> getProjectExpenseSum() {
        return projectExpenseSum;
    }
    public void setProjectExpenseSum(List<BigDecimal> projectExpenseSum) {
        this.projectExpenseSum = projectExpenseSum;
    }
    public List<BigDecimal> getProjectTotal() {
        return projectTotal;
    }
    public void setProjectTotal(List<BigDecimal> projectTotal) {
        this.projectTotal = projectTotal;
    }

}
