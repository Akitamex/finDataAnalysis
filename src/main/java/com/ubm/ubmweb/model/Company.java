package com.ubm.ubmweb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

import lombok.Data;

@Data
@Entity
@Table(name = "companies",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"}),
        @UniqueConstraint(columnNames = {"short_url"})
    }
)
public class Company {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    //
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    @Column(name = "short_url", unique = true, nullable = false)
    private String shortUrl;
    @Column(name = "description")
    private String description;
    @Column(name = "logo_url")
    private String logoUrl;
    @Column(name = "branding_reports", nullable = false)
    private String brandingReports;
    @Column(name = "branding_emails", nullable = false)
    private String brandingEmails;
    @Column(name = "twitter_profile")
    private String twitterProfile;
    @Column(name = "facebook_profile")
    private String facebookProfile;
    @Column(name = "linkedin_profile")
    private String linkedinProfile;
    

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Operation> operations;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<BankAccount> bankAccounts;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<LegalEntity> legalEntities;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Article> articles;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ArticleGroup> articleGroups;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Counterparty> counterparties;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<CounterpartyGroup> counterpartyGroups;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Project> projects;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ProjectDirection> projectDirections;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<FixedAsset> fixedAssets;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Asset> assets;


    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserCompanyRelationship> userCompanies = new HashSet<>();

    public List<FixedAsset> getFixedAssets() {
        return fixedAssets;
    }
    public void setFixedAssets(List<FixedAsset> fixedAssets) {
        this.fixedAssets = fixedAssets;
    }
    public void addFixedAsset(FixedAsset fixedAsset) {
        fixedAssets.add(fixedAsset);
        fixedAsset.setCompany(this);
    }

    public void removeFixedAsset(FixedAsset fixedAsset) {
        fixedAssets.remove(fixedAsset);
        fixedAsset.setCompany(null);
    }

    public List<Asset> getAssets() {
        return assets;
    }
    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }
    public void addAsset(Asset asset) {
        assets.add(asset);
        asset.setCompany(this);
    }

    public void removeAsset(Asset asset) {
        assets.remove(asset);
        asset.setCompany(null);
    }

    public void addUserCompany(UserCompanyRelationship userCompany) {
        userCompanies.add(userCompany);
        userCompany.setCompany(this);
    }

    public void removeUserCompany(UserCompanyRelationship userCompany) {
        userCompanies.remove(userCompany);
        userCompany.setCompany(null);
    }

    public Company() {}

    public List<ProjectDirection> getProjectDirections() {
        return projectDirections;
    }
    public void setProjectDirections(List<ProjectDirection> projectDirections) {
        this.projectDirections = projectDirections;
    }
    public void addProjectDirection(ProjectDirection projectDirection) {
        projectDirections.add(projectDirection);
        projectDirection.setCompany(this);
    }

    public void removeProjectDirection(ProjectDirection projectDirection) {
        projectDirections.remove(projectDirection);
        projectDirection.setCompany(null);
    }

    public List<Project> getProjects() {
        return projects;
    }
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
    public void addProject(Project project) {
        projects.add(project);
        project.setCompany(this);
    }

    public void removeProject(Project project) {
        projects.remove(project);
        project.setCompany(null);
    }
    public List<CounterpartyGroup> getCounterpartyGroups() {
        return counterpartyGroups;
    }
    public void setCounterpartyGroups(List<CounterpartyGroup> counterpartyGroups) {
        this.counterpartyGroups = counterpartyGroups;
    }
    public void addCounterpartyGroup(CounterpartyGroup counterpartyGroup) {
        counterpartyGroups.add(counterpartyGroup);
        counterpartyGroup.setCompany(this);
    }

    public void removeCounterpartyGroup(CounterpartyGroup counterpartyGroup) {
        counterpartyGroups.remove(counterpartyGroup);
        counterpartyGroup.setCompany(null);
    }
    public List<Counterparty> getCounterparties() {
        return counterparties;
    }
    public void setCounterparties(List<Counterparty> counterparties) {
        this.counterparties = counterparties;
    }
    public void addCounterparty(Counterparty counterparty) {
        counterparties.add(counterparty);
        counterparty.setCompany(this);
    }

    public void removeCounterparty(Counterparty counterparty) {
        counterparties.remove(counterparty);
        counterparty.setCompany(null);
    }
    public List<ArticleGroup> getArticleGroups() {
        return articleGroups;
    }
    public void setArticleGroups(List<ArticleGroup> articleGroups) {
        this.articleGroups = articleGroups;
    }
    public void addArticleGroup(ArticleGroup articleGroup) {
        articleGroups.add(articleGroup);
        articleGroup.setCompany(this);
    }

    public void removeArticleGroup(ArticleGroup articleGroup) {
        articleGroups.remove(articleGroup);
        articleGroup.setCompany(null);
    }
    public List<Article> getArticles() {
        return articles;
    }
    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
    public void addArticle(Article article) {
        articles.add(article);
        article.setCompany(this);
    }

    public void removeArticle(Article article) {
        articles.remove(article);
        article.setCompany(null);
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
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
        operation.setCompany(this);
    }
    public void removeOperation(Operation operation) {
        operations.remove(operation);
        operation.setCompany(null);
    }
    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }
    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }
    public void addBankAccount(BankAccount bankAccount) {
        bankAccounts.add(bankAccount);
        bankAccount.setCompany(this);
    }

    public void removeBankAccount(BankAccount bankAccount) {
        bankAccounts.remove(bankAccount);
        bankAccount.setCompany(null);
    }
    public List<LegalEntity> getLegalEntities() {
        return legalEntities;
    }
    public void setLegalEntities(List<LegalEntity> legalEntities) {
        this.legalEntities = legalEntities;
    }
    public void addLegalEntity(LegalEntity legalEntity) {
        legalEntities.add(legalEntity);
        legalEntity.setCompany(this);
    }

    public void removeLegalEntity(LegalEntity legalEntity) {
        legalEntities.remove(legalEntity);
        legalEntity.setCompany(null);
    }

    
}
