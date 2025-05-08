package com.ubm.ubmweb.entities;

import jakarta.persistence.*;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.models.User;

@Entity
@Table(name = "user_company")
public class UserCompanyRelationship {

    @EmbeddedId
    private UserCompanyId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("companyId")
    @JoinColumn(name = "company_id")
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