package com.ubm.ubmweb.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.FundBankAccount;

@Repository
public interface FundBankAccountRepository extends JpaRepository<FundBankAccount, UUID>, JpaSpecificationExecutor<FundBankAccount> {
}
