package com.ubm.ubmweb.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID>, JpaSpecificationExecutor<Article>{
    List<Article> findByIdInAndCompanyId(List<UUID> ids, UUID companyId);
    
    List<Article> findByCompanyId(UUID companyId);

    List<Article> findByArticleGroupId(UUID articleGroupId);
    

}
