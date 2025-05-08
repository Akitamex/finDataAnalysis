package com.ubm.ubmweb.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "project_directions", uniqueConstraints = {@UniqueConstraint(columnNames = {"company_id", "name"})})
public class ProjectDirection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
}