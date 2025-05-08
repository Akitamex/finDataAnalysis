package com.ubm.ubmweb.graphql.dto;

import java.util.UUID;

public class CreateProjectInput {
    private UUID companyId;
    private String type;
    private String status;
    private UUID directionId;
    private String name;
    private String description;

    public UUID getCompanyId() {
        return companyId;
    }
    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public UUID getDirectionId() {
        return directionId;
    }
    public void setDirectionId(UUID directionId) {
        this.directionId = directionId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
