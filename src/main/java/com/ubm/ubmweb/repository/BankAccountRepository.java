package com.ubm.ubmweb.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.BankAccount;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID>, JpaSpecificationExecutor<BankAccount> {
    List<BankAccount> findByIdInAndCompanyId(List<UUID> ids, UUID companyId);

    List<BankAccount> findByCompanyId(UUID companyId);

    List<BankAccount> findByLegalEntityId(UUID legalEntityId);

    // @Query(value = "SELECT * FROM bank_accounts WHERE id IN (:ids)", nativeQuery = true)
    // List<BankAccount> findBankAccountsByIds(@Param("ids") List<Long> ids);
}
