package com.ubm.ubmweb.graphql.dto;

import java.math.BigDecimal;
import java.util.UUID;

// import java.time.format.DateTimeFormatter;
// import java.time.format.DateTimeParseException;


public class UpdateTransferOperationInput {
    private UUID id;
    private UUID companyId;
    private BigDecimal balance;
    private String date;
    public void setDate(String date) {
        this.date = date;
    }
    private String description;
    private UUID articleId;
    private UUID toBankAccountId;
    private UUID fromBankAccountId;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getCompanyId() {
        return companyId;
    }
    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public String getDate() {
        return date;
    }
    // public void setDate(String dateString) {
    //     try {
    //         this.date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    //     } catch (DateTimeParseException e) {
    //         throw new IllegalArgumentException("Date must be in the format YYYY-MM-DD");
    //     }
    // }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public UUID getArticleId() {
        return articleId;
    }
    public void setArticleId(UUID articleId) {
        this.articleId = articleId;
    }
    public UUID getToBankAccountId() {
        return toBankAccountId;
    }
    public void setToBankAccountId(UUID toBankAccountId) {
        this.toBankAccountId = toBankAccountId;
    }
    public UUID getFromBankAccountId() {
        return fromBankAccountId;
    }
    public void setFromBankAccountId(UUID fromBankAccountId) {
        this.fromBankAccountId = fromBankAccountId;
    }
}
