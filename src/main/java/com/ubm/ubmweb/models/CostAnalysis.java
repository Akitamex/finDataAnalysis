package com.ubm.ubmweb.models;

import java.math.BigDecimal;
import java.util.List;

public class CostAnalysis {
    private List<String> rows;
    private List<String> columns;
    private List<List<BigDecimal>> entries;
    public List<String> getRows() {
        return rows;
    }
    public void setRows(List<String> rows) {
        this.rows = rows;
    }
    public List<String> getColumns() {
        return columns;
    }
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
    public List<List<BigDecimal>> getEntries() {
        return entries;
    }
    public void setEntries(List<List<BigDecimal>> entries) {
        this.entries = entries;
    } 
}
