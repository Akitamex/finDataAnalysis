package com.ubm.ubmweb.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "project_directions", uniqueConstraints = {@UniqueConstraint(columnNames = {"company_id", "name"})})
public class ProjectDirection {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "name",nullable = false)
    private String name;
    
    @OneToMany(mappedBy = "projectDirection", cascade = CascadeType.REMOVE, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<Project> projects;

    @OneToMany(mappedBy = "projectDirection", orphanRemoval = false)
    private List<ExpenseOperation> operations;

    public List<ExpenseOperation> getOperations() {
        return operations;
    }
    public void setOperations(List<ExpenseOperation> operations) {
        this.operations = operations;
    }

    public void addOperation(ExpenseOperation operation) {
        operations.add(operation);
        operation.setProjectDirection(this);
    }

    public void removeOperation(ExpenseOperation operation) {
        operations.remove(operation);
        operation.setProjectDirection(null);
    }
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;
    

    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
        this.company = company;
    }
    public List<Project> getProjects() {
        return projects;
    }
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void addProject(Project project) {
        projects.add(project);
        project.setProjectDirection(this);
    }

    public void removeProject(Project project) {
        projects.remove(project);
        project.setProjectDirection(null);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    
}