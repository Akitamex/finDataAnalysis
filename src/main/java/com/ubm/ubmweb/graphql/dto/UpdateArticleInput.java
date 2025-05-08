package com.ubm.ubmweb.graphql.dto;

import java.util.UUID;

public class UpdateArticleInput {
    private UUID id;
    private UUID companyId;
    private String name;
    private String type;
    private UUID articleGroupId;
    private String description;
    private int category;
    
    
    public int getCategory() {
        return category;
    }
    public void setCategory(int category) {
        this.category = category;
    }
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public UUID getArticleGroupId() {
        return articleGroupId;
    }
    public void setArticleGroupId(UUID articleGroupId) {
        this.articleGroupId = articleGroupId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
