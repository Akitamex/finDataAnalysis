package com.ubm.ubmweb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateArticleInput;
import com.ubm.ubmweb.graphql.dto.UpdateArticleInput;
import com.ubm.ubmweb.model.Article;
import com.ubm.ubmweb.model.ArticleGroup;
import com.ubm.ubmweb.model.Company;
import com.ubm.ubmweb.model.Operation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.repository.ArticleGroupRepository;
import com.ubm.ubmweb.repository.ArticleRepository;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.OperationsRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
    
    private final ArticleRepository articleRepository;

    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;

    private final ArticleGroupRepository articleGroupRepository;

    private final CompanyRepository companyRepository;

    private final OperationsRepository operationsRepository;

    @Transactional
    public Article createArticle(UUID userId, CreateArticleInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        ArticleGroup articleGroup = null;
        if(input.getArticleGroupId() != null){
            articleGroup = articleGroupRepository.findById(input.getArticleGroupId())
                .orElseThrow(() -> new IllegalArgumentException("ArticleGroup not found for the given id: " + input.getArticleGroupId()));
            if (!articleGroup.getCompany().getId().equals(input.getCompanyId())) {
                throw new UnauthorizedAccessException("ArticleGroup does not belong to the provided company.");
            }
        }
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));

        if(!(input.getType().equals("INCOME") || input.getType().equals("EXPENSE") || input.getType().equals("TRANSFER"))){
            throw new UnauthorizedAccessException("Article has invalid \"type\" (INCOME, EXPENSE, TRANSFER)");
        }
        if(input.getType().equals("INCOME") && !(input.getCategory() >= 1 && input.getCategory() < 5)){
            throw new UnauthorizedAccessException("Article of type \"INCOME\" can not have the specified category");
        }
        if(input.getType().equals("EXPENSE") && !(input.getCategory() >= 1 && input.getCategory() < 7)){
            throw new UnauthorizedAccessException("Article of type \"EXPENSE\" can not have the specified category");
        }
        Article article = new Article();
        
        if(input.getType().equals("INCOME")){
            if(input.getCategory() == 4){
                article.setCashFlowType(3);
            }
            else{
                article.setCashFlowType(input.getCategory());
            }
        }
        else if(input.getType().equals("EXPENSE")){
            if(input.getCategory() == 1 || input.getCategory() == 2){
                article.setCashFlowType(1);
            }
            else if(input.getCategory() == 3){
                article.setCashFlowType(2);
            }
            else if(input.getCategory() > 3 && input.getCategory() < 6){
                article.setCashFlowType(3);
            }
            else if(input.getCategory() == 6){
                article.setCashFlowType(1);
            }
        }
        else if(input.getType().equals("TRANSFER")){
            input.setCategory(-1);
        }
        else{
            throw new UnauthorizedAccessException("Input type incorrect, must be one of [INCOME,EXPENSE,TRANSFER]");
        }
        article.setName(input.getName());
        article.setType(input.getType());
        article.setCategory(input.getCategory());
        article.setArticleGroup(articleGroup);
        article.setDescription(input.getDescription());
        article.setCompany(company);
        
        return articleRepository.save(article);
    }

    @Transactional(readOnly = true)
    public List<Article> findArticles(UUID userId, UUID companyId, String name, List<UUID> articleGroupIds, List<String> types, String description, Integer category) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        if (articleGroupIds != null && !articleGroupIds.isEmpty()) {
            List<ArticleGroup> validGroups = articleGroupRepository.findByIdInAndCompanyId(articleGroupIds, companyId);
            if (validGroups.size() != articleGroupIds.size()) {
                throw new IllegalArgumentException("One or more ArticleGroups do not belong to the specified company.");
            }
        }
        return articleRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Condition for companyId (mandatory)
            predicates.add(criteriaBuilder.equal(root.get("company").get("id"), companyId));
            
            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            // Condition for groupIds (optional)
            if (articleGroupIds != null && !articleGroupIds.isEmpty()) {
                predicates.add(root.get("articleGroup").get("id").in(articleGroupIds));
            }
    
            // Condition for types (optional)
            if (types != null && !types.isEmpty()) {
                predicates.add(root.get("type").in(types));
            }
    
            // Condition for description (optional)
            if (description != null && !description.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            if(category != null){
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }
    
            query.distinct(true); // Ensure distinct results
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
    

    @Transactional(readOnly = true)
    public Article getArticleByIdAndCompanyId(UUID userId, UUID id, UUID companyId){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Article not found for the given id: " + id));
        if (!article.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested Article does not belong to the provided company.");
        }
        return article;       
    }

    @Transactional
    public Article updateArticle(UUID userId, UpdateArticleInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Article article = articleRepository.findById(input.getId())
            .orElseThrow(() -> new IllegalArgumentException("Article not found for the given id: " + input.getId()));
        if (!article.getCompany().getId().equals(input.getCompanyId())) {
            throw new UnauthorizedAccessException("Requested Article does not belong to the provided company.");
        }

        ArticleGroup articleGroup = null;
        if(input.getArticleGroupId() != null){
            articleGroup = articleGroupRepository.findById(input.getArticleGroupId())
                .orElseThrow(() -> new IllegalArgumentException("ArticleGroup not found for the given id: " + input.getArticleGroupId()));
            if (!articleGroup.getCompany().getId().equals(input.getCompanyId())) {
                throw new UnauthorizedAccessException("ArticleGroup does not belong to the provided company.");
            }
        }
        if(!article.getType().equals(input.getType())){
            throw new UnauthorizedAccessException("New Article type must match the old type");
        }
        if(article.getType().equals("INCOME") && !(input.getCategory() >= 0 && input.getCategory() < 4)){
            throw new UnauthorizedAccessException("Article of type \"INCOME\" can not have the specified category");
        }
        if(article.getType().equals("EXPENSE") && !(input.getCategory() >= 0 && input.getCategory() < 5)){
            throw new UnauthorizedAccessException("Article of type \"EXPENSE\" can not have the specified category");
        }

        if(input.getType().equals("INCOME")){
            if(input.getCategory() == 3){
                article.setCashFlowType(2);
            }
            else{
                article.setCashFlowType(input.getCategory());
            }
        }
        else if(input.getType().equals("EXPENSE")){
            if(input.getCategory() == 0 || input.getCategory() == 1){
                article.setCashFlowType(0);
            }
            else if(input.getCategory() == 2){
                article.setCashFlowType(1);
            }
            else if(input.getCategory() > 2){
                article.setCashFlowType(2);
            }
        }

        article.setName(input.getName());
        article.setType(input.getType());
        article.setCategory(input.getCategory());
        article.setDescription(input.getDescription());
        article.setArticleGroup(articleGroup);
        return articleRepository.save(article);
    }

    @Transactional
    public void deleteArticle(UUID id, UUID userId, UUID companyId){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Article not found for the given id: " + id));
        if (!article.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested Article does not belong to the provided company.");
        }

        
        List<Operation> operations = operationsRepository.findByArticleId(id);
        if(!operations.isEmpty()){
            throw new IllegalStateException("Cannot delete Article as it has associated operations.");
        }

        articleRepository.deleteById(id);
    }
    
}