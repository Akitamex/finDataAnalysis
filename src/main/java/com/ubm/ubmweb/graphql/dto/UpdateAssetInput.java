package com.ubm.ubmweb.graphql.dto;

import java.util.UUID;

public class UpdateAssetInput {
    private UUID id;
    private UUID companyId;
    private String name;
    
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
}
