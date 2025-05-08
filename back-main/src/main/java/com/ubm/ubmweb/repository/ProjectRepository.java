package com.ubm.ubmweb.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID>, JpaSpecificationExecutor<Project> {
    List<Project> findByIdInAndCompanyId(List<UUID> ids, UUID companyId);

    List<Project> findByCompanyId(UUID companyId);

    List<Project> findByProjectDirectionId(UUID projectDirectionId);
}
