package com.ubm.ubmweb.entities;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "articles", uniqueConstraints = {@UniqueConstraint(columnNames = {"company_id", "name"})})
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "type",nullable = false)
    private String type;

    @Column(name = "description", nullable = true)
    private String description;

    @ManyToOne
    @JoinColumn(name = "article_group_id",nullable = true)
    private ArticleGroup articleGroup;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<Operation> operations;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "category")
    private int category;

    @Column(name = "cash_flow_type")
    private int cashFlowType;
    

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Operation> getOperations() {
        return operations;
    }
    
    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public void addOperation(Operation operation) {
        operations.add(operation);
        operation.setArticle(this);
    }

    public void removeOperation(Operation operation) {
        operations.remove(operation);
        operation.setArticle(null);
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        if(name == null){
            return "";
        }
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        if(description == null){
            return "";
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public ArticleGroup getArticleGroup() {
        return articleGroup;
    }
    public void setArticleGroup(ArticleGroup group) {
        if (group == null || group.getType().equals(type)) {
            this.articleGroup = group;
        } else {
            throw new IllegalArgumentException("Invalid group for the article type");
        }
    }

    public int getCashFlowType() {
        return cashFlowType;
    }

    public void setCashFlowType(int cashFlowType) {
        this.cashFlowType = cashFlowType;
    }

    
}
