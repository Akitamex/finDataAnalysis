package com.ubm.ubmweb.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.Counterparty;

@Repository
public interface CounterpartyRepository extends JpaRepository<Counterparty, UUID>, JpaSpecificationExecutor<Counterparty> {
    List<Counterparty> findByIdInAndCompanyId(List<UUID> ids, UUID companyId);

    List<Counterparty> findByCompanyId(UUID companyId);

    List<Counterparty> findByCounterpartyGroupId(UUID counterpartyGroupId);

    // @Query("SELECT c FROM Counterparty c WHERE c.company.id =:companyId AND c.debt > 0")
    // List<Counterparty> findAccountsRecievable(@Param("companyId") Long companyId);

    // @Query("SELECT c FROM Counterparty c WHERE c.company.id =:companyId AND c.debt < 0")
    // List<Counterparty> findAccountsPayable(@Param("companyId") Long companyId);
}
