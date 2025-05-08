package com.ubm.ubmweb.entities;

import jakarta.persistence.*;


@Entity
@DiscriminatorValue("EXPENSE")
public class ExpenseOperation extends Operation {
    @ManyToOne
    @JoinColumn(name="project_direction",nullable = true)
    private ProjectDirection projectDirection;

    public ProjectDirection getProjectDirection() {
        return projectDirection;
    }

    public void setProjectDirection(ProjectDirection projectDirection) {
        this.projectDirection = projectDirection;
    }

}
