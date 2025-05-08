package com.ubm.ubmweb.services;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.Article;
import com.ubm.ubmweb.entities.ArticleGroup;
import com.ubm.ubmweb.entities.Company;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.repository.ArticleGroupRepository;
import com.ubm.ubmweb.repository.ArticleRepository;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleGroupService {
    
    private final ArticleGroupRepository articleGroupRepository;

    private final ArticleRepository articleRepository;

    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;

    private final CompanyRepository companyRepository;

    @Transactional
    public ArticleGroup createArticleGroup(Long userId, Long companyId, String type, String name){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        ArticleGroup articleGroup = new ArticleGroup();
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
        new IllegalArgumentException("Company not found for the given id: " + companyId));

        
        articleGroup.setName(name);
        articleGroup.setType(type);
        articleGroup.setCompany(company);
        
        company.addArticleGroup(articleGroup);

        return articleGroupRepository.save(articleGroup);
    }

    @Transactional
    public void deleteArticleGroup(Long userId, Long companyId, Long id){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        ArticleGroup articleGroup = articleGroupRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ArticleGroup not found for id: " + id));
        if(!articleGroup.getCompany().getId().equals(companyId)){
            throw new UnauthorizedAccessException("Attempt to access articleGroup with invalid companyId");
        }


        List<Article> articles = articleRepository.findByArticleGroupId(id);
        if(!articles.isEmpty()){
            throw new IllegalStateException("Cannot delete ArticleGroup as it has associated Articles.");
        }

        articleGroupRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ArticleGroup> findArticleGroups(Long companyId, Long userId, String name, List<String> type) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        
        Specification<ArticleGroup> spec = Specification.where(null);

        spec = spec.and((root, query, cb) -> cb.equal(root.get("company").get("id"), companyId));

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        // Add type condition if types are provided
        if (type != null && !type.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("type").in(type));
        }

        return articleGroupRepository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public ArticleGroup getArticleGroupByIdAndCompanyId(Long userId, Long id, Long companyId){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        ArticleGroup articleGroup = articleGroupRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ArticleGroup not found for id: " + id));
        if(!articleGroup.getCompany().getId().equals(companyId)){
            throw new UnauthorizedAccessException("Attempt to access articleGroup with invalid companyId");
        }
        return articleGroup; 
    }

    @Transactional
    public ArticleGroup updateArticleGroup(Long userId, Long id, Long companyId, String name){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        
        ArticleGroup articleGroup = articleGroupRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ArticleGroup not found with id: " + id));
        
        if(!articleGroup.getCompany().getId().equals(companyId)){
            throw new UnauthorizedAccessException("Attempt to modify articleGroup that doesn't belong to user's company");
        }

        articleGroup.setName(name);
        return articleGroupRepository.save(articleGroup);
    }
}