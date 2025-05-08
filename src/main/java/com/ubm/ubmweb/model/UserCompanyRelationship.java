package com.ubm.ubmweb.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ubm.ubmweb.compositeKey.UserCompanyId;

@Entity
@Table(name = "user_company")
public class UserCompanyRelationship {

    @EmbeddedId
    private UserCompanyId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @MapsId("companyId")
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;

    @Column(name = "role")
    private String role;
    
    public UserCompanyRelationship() {}

    public UserCompanyRelationship(Company company, User user) {
        this.company = company;
        this.user = user;
        this.id = new UserCompanyId(company.getId(), user.getId());
    }

    public UserCompanyId getId() {
        return this.id;
    }

    public void setId(UserCompanyId id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}