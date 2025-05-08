package com.ubm.ubmweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;


@Entity
@DiscriminatorValue("EXPENSE")
public class ExpenseOperation extends Operation {
    @ManyToOne
    @JoinColumn(name="project_direction",nullable = true)
    @JsonIgnore
    private ProjectDirection projectDirection;

    public ProjectDirection getProjectDirection() {
        return projectDirection;
    }

    public void setProjectDirection(ProjectDirection projectDirection) {
        this.projectDirection = projectDirection;
    }

}
