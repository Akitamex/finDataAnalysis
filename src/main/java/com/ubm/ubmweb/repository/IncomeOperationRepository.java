package com.ubm.ubmweb.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.IncomeOperation;

@Repository
public interface IncomeOperationRepository extends JpaRepository<IncomeOperation, UUID>, JpaSpecificationExecutor<IncomeOperation> {
    
    
}
