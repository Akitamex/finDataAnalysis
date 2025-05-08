package com.ubm.ubmweb.model;

import java.math.BigDecimal;
import java.util.List;

public class ProfitsLosses {
    private String timeframe;
    private String type;
    private List<String> columns;
    private List<String> rows;
    private List<BigDecimal> revenueSum; //Выручка                              a.DONE
    private List<BigDecimal> directCostsSum; // Прямые расходы                  a.DONE
    private List<BigDecimal> directCostsVariablesSum;
    private List<BigDecimal> directCostsConstantsSum;
    private List<BigDecimal> grossProfit; //Валовая прибыль                     a.DONE
    private List<BigDecimal> otherIncomeSum; //Прочие доходы                    a.DONE
    private List<String> otherIncomeNames;
    private List<List<BigDecimal>> otherIncome;
    private List<BigDecimal> indirectCostsSum; // Косвенные расходы             a.DONE
    private List<String> indirectCostsNames;
    private List<List<BigDecimal>> indirectCosts;
    private List<BigDecimal> operatingProfits; //Операционная прибыль           a.DONE
    private List<BigDecimal> taxesSum; //Налог на доход                         a.DONE
    //private List<BigDecimal> interestOnLoans; //Проценты по кредитам и займам ????
    private List<BigDecimal> depreciationSum; //Амортизация                     a.DONE
    private List<BigDecimal> netProfit; //Чистая прибыль                        a.DONE                  
    private List<BigDecimal> withdrawalOfProfitsSum; //Вывод прибыли из бизнеса a.DONE
    private List<String> withdrawalOfProfitsNames;
    private List<List<BigDecimal>> withdrawalOfProfits;
    private List<BigDecimal> retainedEarnings; // Нераспределенная прибыль      a.DONE
    private List<String> depreciationNames;
    private List<List<BigDecimal>> depreciation;
    private ProfitsLossesArticles profitsLossesArticles;
    private List<ProfitsLossesProjects> profitsLossesProjects;
    

    public List<ProfitsLossesProjects> getProfitsLossesProjects() {
        return profitsLossesProjects;
    }
    public void setProfitsLossesProjects(List<ProfitsLossesProjects> profitsLossesProjects) {
        this.profitsLossesProjects = profitsLossesProjects;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public ProfitsLossesArticles getProfitsLossesArticles() {
        return profitsLossesArticles;
    }
    public void setProfitsLossesArticles(ProfitsLossesArticles profitsLossesArticles) {
        this.profitsLossesArticles = profitsLossesArticles;
    }
    public List<String> getColumns() {
        return columns;
    }
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
    public String getTimeframe() {
        return timeframe;
    }
    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }
    public List<BigDecimal> getRevenue() {
        return revenueSum;
    }
    public void setRevenueSum(List<BigDecimal> revenueSum) {
        this.revenueSum = revenueSum;
    }
    public List<BigDecimal> getDirectCostsSum() {
        return directCostsSum;
    }
    public void setDirectCostsSum(List<BigDecimal> directCostsSum) {
        this.directCostsSum = directCostsSum;
    }
    public List<BigDecimal> getGrossProfit() {
        return grossProfit;
    }
    public void setGrossProfit(List<BigDecimal> grossProfit) {
        this.grossProfit = grossProfit;
    }
    public List<BigDecimal> getOtherIncomeSum() {
        return otherIncomeSum;
    }
    public void setOtherIncomeSum(List<BigDecimal> otherIncomeSum) {
        this.otherIncomeSum = otherIncomeSum;
    }
    public List<BigDecimal> getTaxesSum() {
        return taxesSum;
    }
    public void setTaxesSum(List<BigDecimal> taxesSum) {
        this.taxesSum = taxesSum;
    }
    public List<BigDecimal> getIndirectCostsSum() {
        return indirectCostsSum;
    }
    public void setIndirectCostsSum(List<BigDecimal> indirectCostsSum) {
        this.indirectCostsSum = indirectCostsSum;
    }
    public List<BigDecimal> getOperatingProfits() {
        return operatingProfits;
    }
    public void setOperatingProfits(List<BigDecimal> operatingProfits) {
        this.operatingProfits = operatingProfits;
    }
    
    // public List<BigDecimal> getInterestOnLoans() {
    //     return interestOnLoans;
    // }
    // public void setInterestOnLoans(List<BigDecimal> interestOnLoans) {
    //     this.interestOnLoans = interestOnLoans;
    // }
    public List<BigDecimal> getDepreciationSum() {
        return depreciationSum;
    }
    public void setDepreciationSum(List<BigDecimal> depreciationSum) {
        this.depreciationSum = depreciationSum;
    }
    public List<BigDecimal> getNetProfit() {
        return netProfit;
    }
    public void setNetProfit(List<BigDecimal> netProfit) {
        this.netProfit = netProfit;
    }
    public List<BigDecimal> getWithdrawalOfProfitsSum() {
        return withdrawalOfProfitsSum;
    }
    public void setWithdrawalOfProfitsSum(List<BigDecimal> withdrawalOfProfitsSum) {
        this.withdrawalOfProfitsSum = withdrawalOfProfitsSum;
    }
    public List<BigDecimal> getRetainedEarnings() {
        return retainedEarnings;
    }
    public void setRetainedEarnings(List<BigDecimal> retainedEarnings) {
        this.retainedEarnings = retainedEarnings;
    }
    public List<String> getRows() {
        return rows;
    }
    public void setRows(List<String> rows) {
        this.rows = rows;
    }
    public List<BigDecimal> getRevenueSum() {
        return revenueSum;
    }
    public List<String> getWithdrawalOfProfitsNames() {
        return withdrawalOfProfitsNames;
    }
    public void setWithdrawalOfProfitsNames(List<String> withdrawalOfProfitsNames) {
        this.withdrawalOfProfitsNames = withdrawalOfProfitsNames;
    }
    public List<List<BigDecimal>> getWithdrawalOfProfits() {
        return withdrawalOfProfits;
    }
    public void setWithdrawalOfProfits(List<List<BigDecimal>> withdrawalOfProfits) {
        this.withdrawalOfProfits = withdrawalOfProfits;
    }
    public List<String> getDepreciationNames() {
        return depreciationNames;
    }
    public void setDepreciationNames(List<String> depreciationNames) {
        this.depreciationNames = depreciationNames;
    }
    public List<List<BigDecimal>> getDepreciation() {
        return depreciation;
    }
    public void setDepreciation(List<List<BigDecimal>> depreciation) {
        this.depreciation = depreciation;
    }
    public List<String> getIndirectCostsNames() {
        return indirectCostsNames;
    }
    public void setIndirectCostsNames(List<String> indirectCostsNames) {
        this.indirectCostsNames = indirectCostsNames;
    }
    public List<List<BigDecimal>> getIndirectCosts() {
        return indirectCosts;
    }
    public void setIndirectCosts(List<List<BigDecimal>> indirectCosts) {
        this.indirectCosts = indirectCosts;
    }
    public List<String> getOtherIncomeNames() {
        return otherIncomeNames;
    }
    public void setOtherIncomeNames(List<String> otherIncomeNames) {
        this.otherIncomeNames = otherIncomeNames;
    }
    public List<List<BigDecimal>> getOtherIncome() {
        return otherIncome;
    }
    public void setOtherIncome(List<List<BigDecimal>> otherIncome) {
        this.otherIncome = otherIncome;
    }
    public List<BigDecimal> getDirectCostsVariablesSum() {
        return directCostsVariablesSum;
    }
    public void setDirectCostsVariablesSum(List<BigDecimal> directCostsVariablesSum) {
        this.directCostsVariablesSum = directCostsVariablesSum;
    }
    public List<BigDecimal> getDirectCostsConstantsSum() {
        return directCostsConstantsSum;
    }
    public void setDirectCostsConstantsSum(List<BigDecimal> directCostsConstantsSum) {
        this.directCostsConstantsSum = directCostsConstantsSum;
    }
    
        

}
