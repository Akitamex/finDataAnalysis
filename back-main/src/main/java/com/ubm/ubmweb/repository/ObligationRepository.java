package com.ubm.ubmweb.repository;

import com.ubm.ubmweb.graphql.dto.DateRangeInput;
import com.ubm.ubmweb.model.Obligation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ObligationRepository extends JpaRepository<Obligation, UUID>, JpaSpecificationExecutor<Obligation> {

    @Query("SELECT o FROM Obligation o WHERE o.company.id =:companyId AND " +
       "(:types IS NULL OR o.type IN :types) AND " +
       "(:counterpartyIds IS NULL OR o.counterparty.id IN :counterpartyIds) AND " +
       "(:legalEntityIds IS NULL OR o.legalEntity.id IN :legalEntityIds) AND " +
       "(:projectIds IS NULL OR o.project.id IN :projectIds) AND " +
       "(:description IS NULL OR o.description LIKE %:description%) AND " +
       "(:#{#dateRange.startDate} IS NULL OR o.date >= :#{#dateRange.startDate}) AND " +
       "(:#{#dateRange.endDate} IS NULL OR o.date <= :#{#dateRange.endDate})")
List<Obligation> findObligations(@Param("companyId") UUID companyId,
                                 @Param("dateRange") DateRangeInput dateRange,
                                 @Param("types") List<String> types,
                                 @Param("counterpartyIds") List<UUID> counterpartyIds,
                                 @Param("legalEntityIds") List<UUID> legalEntityIds,
                                 @Param("projectIds") List<UUID> projectIds,
                                 @Param("description") String description);


    @Query("SELECT o FROM Obligation o WHERE o.company.id = :companyId AND o.type = 'OUT'")
    List<Obligation> findAccountsRecievable(@Param("companyId") UUID companyId); //Дебиторская задолженность

    @Query("SELECT o FROM Obligation o WHERE o.company.id = :companyId AND o.type = 'IN'")
    List<Obligation> findAccountsPayable(@Param("companyId") UUID companyId); //Кредиторская задолженность
}
