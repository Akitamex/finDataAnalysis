package com.ubm.ubmweb.graphql.dto;

import java.util.UUID;

public class CreateLegalEntityInput {
    private UUID companyId;
    private String name;
    private String fullName;
    private String iin;
    private String cor;
    private String msrn;
    private String legalAddress;
    private String phoneNum;
    private Boolean vat;

    public UUID getCompanyId() {
        return companyId;
    }
    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getLegalAddress() {
        return legalAddress;
    }
    public void setLegalAddress(String legalAddress) {
        this.legalAddress = legalAddress;
    }
    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    public String getIin() {
        return iin;
    }
    public void setIin(String iin) {
        this.iin = iin;
    }
    public String getCor() {
        return cor;
    }
    public void setCor(String cor) {
        this.cor = cor;
    }
    public String getMsrn() {
        return msrn;
    }
    public void setMsrn(String msrn) {
        this.msrn = msrn;
    }
    public Boolean getVat() {
        return vat;
    }
    public void setVat(Boolean vat) {
        this.vat = vat;
    }
    
}