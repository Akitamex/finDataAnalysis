package com.ubm.ubmweb.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.ExpenseOperation;

@Repository
public interface ExpenseOperationRepository extends JpaRepository<ExpenseOperation, UUID>, JpaSpecificationExecutor<ExpenseOperation> {
    
    
}
