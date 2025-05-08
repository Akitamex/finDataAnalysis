package com.ubm.ubmweb.entities;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "projects", uniqueConstraints = {@UniqueConstraint(columnNames = {"company_id", "name"})})
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "type",nullable = true)  //PROJECT or DEAL
    private String type;

    @Column(name = "status", columnDefinition = "VARCHAR(255) DEFAULT 'IN_PROGRESS'")
    private String status; // FINISHED VS IN_PROGRESS
    
    @Column(name = "name",nullable = false)
    private String name;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<Operation> operations;
    
    @ManyToOne
    @JoinColumn(name = "project_direction_id")
    private ProjectDirection projectDirection;
    
    @Column(name = "description",nullable = true)
    private String description;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;


    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
        this.company = company;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Operation> getOperations() {
        return operations;
    }
    
    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public void addOperation(Operation operation) {
        operations.add(operation);
        operation.setProject(this);
    }

    public void removeOperation(Operation operation) {
        operations.remove(operation);
        operation.setProject(null);
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ProjectDirection getProjectDirection() {
        return projectDirection;
    }

    public void setProjectDirection(ProjectDirection projectDirection) {
        this.projectDirection = projectDirection;
    }
}