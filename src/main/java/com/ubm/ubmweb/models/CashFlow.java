package com.ubm.ubmweb.models;

import java.math.BigDecimal;
import java.util.List;

public class CashFlow {
    private String type;
    private String first;
    private String timeframe;
    private String grouping;
    private List<Long> bankAccountIds;
    private List<Long> projectIds;
    private List<String> columns;
    private List<String> rows;
    private List<String> initialBalanceRows;
    private List<List<BigDecimal>> initialBalance;
    private List<BigDecimal> initialBalanceSum;
    private List<String> incomeRows;
    private List<List<BigDecimal>> income;
    private List<BigDecimal> incomeSum;
    private List<String> expenseRows;
    private List<List<BigDecimal>> expense;
    private List<BigDecimal> expenseSum;
    private List<String> transfer;
    private List<BigDecimal> transferIn;
    private List<BigDecimal> transferOut;
    private List<BigDecimal> transferSum;
    private List<BigDecimal> saldo;
    private List<String> finalBalanceRows;
    private List<List<BigDecimal>> finalBalance;
    private List<BigDecimal> finalBalanceSum;
    private List<CashFlowProjectData> projectData;
    private CashFlowByActivity activityData;
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getFirst() {
        return first;
    }
    public void setFirst(String first) {
        this.first = first;
    }
    public String getTimeframe() {
        return timeframe;
    }
    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }
    public String getGrouping() {
        return grouping;
    }
    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }
    public List<Long> getBankAccountIds() {
        return bankAccountIds;
    }
    public void setBankAccountIds(List<Long> bankAccountIds) {
        this.bankAccountIds = bankAccountIds;
    }
    public List<Long> getProjectIds() {
        return projectIds;
    }
    public void setProjectIds(List<Long> projectIds) {
        this.projectIds = projectIds;
    }
    public List<String> getColumns() {
        return columns;
    }
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
    public List<String> getRows() {
        return rows;
    }
    public void setRows(List<String> rows) {
        this.rows = rows;
    }
    public List<String> getInitialBalanceRows() {
        return initialBalanceRows;
    }
    public void setInitialBalanceRows(List<String> initialBalanceRows) {
        this.initialBalanceRows = initialBalanceRows;
    }
    public List<List<BigDecimal>> getInitialBalance() {
        return initialBalance;
    }
    public void setInitialBalance(List<List<BigDecimal>> initialBalance) {
        this.initialBalance = initialBalance;
    }
    public List<BigDecimal> getInitialBalanceSum() {
        return initialBalanceSum;
    }
    public void setInitialBalanceSum(List<BigDecimal> initialBalanceSum) {
        this.initialBalanceSum = initialBalanceSum;
    }
    public List<String> getIncomeRows() {
        return incomeRows;
    }
    public void setIncomeRows(List<String> incomeRows) {
        this.incomeRows = incomeRows;
    }
    public List<List<BigDecimal>> getIncome() {
        return income;
    }
    public void setIncome(List<List<BigDecimal>> income) {
        this.income = income;
    }
    public List<BigDecimal> getIncomeSum() {
        return incomeSum;
    }
    public void setIncomeSum(List<BigDecimal> incomeSum) {
        this.incomeSum = incomeSum;
    }
    public List<String> getExpenseRows() {
        return expenseRows;
    }
    public void setExpenseRows(List<String> expenseRows) {
        this.expenseRows = expenseRows;
    }
    public List<List<BigDecimal>> getExpense() {
        return expense;
    }
    public void setExpense(List<List<BigDecimal>> expense) {
        this.expense = expense;
    }
    public List<BigDecimal> getExpenseSum() {
        return expenseSum;
    }
    public void setExpenseSum(List<BigDecimal> expenseSum) {
        this.expenseSum = expenseSum;
    }
    public List<String> getTransfer() {
        return transfer;
    }
    public void setTransfer(List<String> transfer) {
        this.transfer = transfer;
    }
    public List<BigDecimal> getTransferIn() {
        return transferIn;
    }
    public void setTransferIn(List<BigDecimal> transferIn) {
        this.transferIn = transferIn;
    }
    public List<BigDecimal> getTransferOut() {
        return transferOut;
    }
    public void setTransferOut(List<BigDecimal> transferOut) {
        this.transferOut = transferOut;
    }
    public List<BigDecimal> getTransferSum() {
        return transferSum;
    }
    public void setTransferSum(List<BigDecimal> transferSum) {
        this.transferSum = transferSum;
    }
    public List<BigDecimal> getSaldo() {
        return saldo;
    }
    public void setSaldo(List<BigDecimal> saldo) {
        this.saldo = saldo;
    }
    public List<String> getFinalBalanceRows() {
        return finalBalanceRows;
    }
    public void setFinalBalanceRows(List<String> finalBalanceRows) {
        this.finalBalanceRows = finalBalanceRows;
    }
    public List<List<BigDecimal>> getFinalBalance() {
        return finalBalance;
    }
    public void setFinalBalance(List<List<BigDecimal>> finalBalance) {
        this.finalBalance = finalBalance;
    }
    public List<BigDecimal> getFinalBalanceSum() {
        return finalBalanceSum;
    }
    public void setFinalBalanceSum(List<BigDecimal> finalBalanceSum) {
        this.finalBalanceSum = finalBalanceSum;
    }
    public List<CashFlowProjectData> getProjectData() {
        return projectData;
    }
    public void setProjectData(List<CashFlowProjectData> projectData) {
        this.projectData = projectData;
    }
    public CashFlowByActivity getActivityData() {
        return activityData;
    }
    public void setActivityData(CashFlowByActivity activityData) {
        this.activityData = activityData;
    }

   
    
    
}
