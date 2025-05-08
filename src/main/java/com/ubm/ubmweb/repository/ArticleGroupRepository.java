package com.ubm.ubmweb.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.ArticleGroup;

@Repository
public interface ArticleGroupRepository extends JpaRepository<ArticleGroup, UUID>, JpaSpecificationExecutor<ArticleGroup> {

    List<ArticleGroup> findByIdInAndCompanyId(List<UUID> ids, UUID companyId);
    
    List<ArticleGroup> findByCompanyId(UUID companyId);

}
