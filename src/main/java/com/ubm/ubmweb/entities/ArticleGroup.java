package com.ubm.ubmweb.entities;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "article_groups", uniqueConstraints = {@UniqueConstraint(columnNames = {"company_id", "name"})})
public class ArticleGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @OneToMany(mappedBy = "articleGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Article> articles;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Article> getArticles() {
        return articles;
    }
    
    public void setArticles(List<Article> articles) {
        this.articles = articles;
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
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}
