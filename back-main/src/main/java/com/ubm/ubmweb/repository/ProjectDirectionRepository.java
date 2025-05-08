package com.ubm.ubmweb.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.ProjectDirection;

@Repository
public interface ProjectDirectionRepository extends JpaRepository<ProjectDirection, UUID>, JpaSpecificationExecutor<ProjectDirection> {
    List<ProjectDirection> findByIdInAndCompanyId(List<UUID> ids, UUID companyId);

    List<ProjectDirection> findByCompanyId(UUID companyId);
}
