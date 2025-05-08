package com.ubm.ubmweb.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.CounterpartyGroup;

@Repository
public interface CounterpartyGroupRepository extends JpaRepository<CounterpartyGroup, UUID>, JpaSpecificationExecutor<CounterpartyGroup> {
    List<CounterpartyGroup> findByIdInAndCompanyId(List<UUID> ids, UUID companyId);

    List<CounterpartyGroup> findByCompanyId(UUID companyId);

}
