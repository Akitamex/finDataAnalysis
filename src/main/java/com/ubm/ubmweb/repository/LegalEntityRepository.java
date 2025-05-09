package com.ubm.ubmweb.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.LegalEntity;



@Repository
public interface LegalEntityRepository extends JpaRepository<LegalEntity, UUID>, JpaSpecificationExecutor<LegalEntity> {
    List<LegalEntity> findByIdInAndCompanyId(List<UUID> ids, UUID companyId);

    List<LegalEntity> findByCompanyId(UUID companyId);
}
