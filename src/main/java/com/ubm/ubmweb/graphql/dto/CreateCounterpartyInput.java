package com.ubm.ubmweb.graphql.dto;

import java.util.UUID;

public class CreateCounterpartyInput {
    private UUID companyId;
    private UUID counterpartyGroupId;
    private String title;
    private String fullName;
    private String email;
    private String phoneNum;
    private String description;
    
    public UUID getCompanyId() {
        return companyId;
    }
    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
    public UUID getCounterpartyGroupId() {
        return counterpartyGroupId;
    }
    public void setCounterpartyGroupId(UUID counterpartyGroupId) {
        this.counterpartyGroupId = counterpartyGroupId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
