// Юредические лица
package com.ubm.ubmweb.entities;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "legal_entities")
public class LegalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name",nullable = false)
    private String name;
    
    @Column(name = "full name",nullable = true)
    private String fullName;
    
    @Column(name = "IIN",nullable = true)
    private String iin; 
    
    @Column(name = "COR",nullable = true)
    private String cor; // Code of reason, КПП
    
    @Column(name = "MSRN",nullable = true)
    private String msrn;   //ОГРН
    
    @Column(name = "Legal Address",nullable = true)
    private String LegalAddress;
    
    @Column(name = "Phone Number",nullable = true)
    private String phoneNum;
    
    @Column(name = "VAT",nullable = false)
    private Boolean vat;
    
    @OneToMany(mappedBy = "legalEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY) // Name of the attribute in BankAccount class
    private List<BankAccount> bankAccounts;
    
    @OneToMany(mappedBy = "legalEntity", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<FixedAsset> fixedAssets;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    public List<FixedAsset> getFixedAssets() {
        return fixedAssets;
    }
    public void setFixedAssets(List<FixedAsset> fixedAssets) {
        this.fixedAssets = fixedAssets;
    }
    public void addFixedAsset(FixedAsset fixedAsset) {
        fixedAssets.add(fixedAsset);
        fixedAsset.setLegalEntity(this);
    }

    public void removeFixedAsset(FixedAsset fixedAsset) {
        fixedAssets.remove(fixedAsset);
        fixedAsset.setLegalEntity(null);
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }
    public void addBankAccount(BankAccount bankAccount) {
        bankAccounts.add(bankAccount);
        bankAccount.setLegalEntity(this);
    }

    public void removeBankAccount(BankAccount bankAccount) {
        bankAccounts.remove(bankAccount);
        bankAccount.setLegalEntity(null);
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
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
   
    public String getLegalAddress() {
        return LegalAddress;
    }
    public void setLegalAddress(String legal_Address) {
        LegalAddress = legal_Address;
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
