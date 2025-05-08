package com.ubm.ubmweb.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.Operation;

@Repository
public interface OperationsRepository extends JpaRepository<Operation, UUID>, JpaSpecificationExecutor<Operation> {
    List<Operation> findByCompanyId(UUID companyId);

    List<Operation> findByBankAccountIdIn(List<UUID> bankAccountIds);

    List<Operation> findByBankAccountId(UUID bankAccountId);

    List<Operation> findByProjectId(UUID projectId);

    List<Operation> findByArticleId(UUID articleId);

    List<Operation> findByCounterpartyId(UUID counterpartyId);

    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id=:companyId AND o.type='INCOME' AND (a IS NULL OR a.category=1) AND o.project.type='PROJECT' AND (o.date BETWEEN :startDate AND :endDate)")
    List<Operation> findRevenue(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id =:companyId AND o.type='EXPENSE' AND (a IS NULL OR a.category=1) AND o.project IS NOT NULL AND o.date BETWEEN :startDate AND :endDate")
    List<Operation> findDirectCostsVariable(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id =:companyId AND o.type='EXPENSE' AND (a IS NULL OR a.category=1) AND o.project IS NULL AND o.projectDirection IS NOT NULL AND o.date BETWEEN :startDate AND :endDate")
    List<Operation> findDirectCostsConstant(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id =:companyId AND o.type='INCOME' AND o.project IS NULL AND (a IS NULL OR a.category=1) AND o.date BETWEEN :startDate AND :endDate")
    List<Operation> findOtherIncome(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id =:companyId AND o.type='EXPENSE' AND o.project IS NULL AND o.projectDirection IS NULL AND (a IS NULL OR a.category=1) AND o.date BETWEEN :startDate AND :endDate")
    List<Operation> findIndirectCosts(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id =:companyId AND o.type='EXPENSE' AND a.category=6 AND o.date BETWEEN :startDate AND :endDate")
    List<Operation> findTaxes(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id =:companyId AND o.type='EXPENSE' AND a.category=5 AND o.date BETWEEN :startDate AND :endDate")
    List<Operation> findWithdrawalOfProfits(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id =:companyId AND o.type='INCOME' AND o.project.type='PROJECT' AND (a IS NULL OR a.category=1) AND o.date BETWEEN :startDate AND :endDate")
    List<Operation> findProjectRevenue(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id =:companyId AND o.type='EXPENSE' AND o.project IS NOT NULL AND o.projectDirection IS NULL AND (a IS NULL OR a.category=1) AND o.date BETWEEN :startDate AND :endDate")
    List<Operation> findProjectVariable(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id =:companyId AND o.type='EXPENSE' AND o.project IS NULL AND o.projectDirection IS NOT NULL AND (a IS NULL OR a.category=1) AND o.date BETWEEN :startDate AND :endDate")
    List<Operation> findProjectConstant(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id =:companyId AND o.type='EXPENSE' AND a.category=6 AND o.project IS NOT NULL AND o.date BETWEEN :startDate AND :endDate")
    List<Operation> findProjectTaxes(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id =:companyId AND o.type='EXPENSE' AND a.cashFlowType=1 AND o.date BETWEEN :startDate AND :endDate")
    List<Operation> findCostAnalysis(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Operation o LEFT JOIN o.article a WHERE o.company.id =:companyId AND o.type='INCOME' AND a.cashFlowType=1 AND o.date BETWEEN :startDate AND :endDate")
    List<Operation> findCARevenue(@Param("companyId") UUID companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    

    @Query("SELECT o FROM Operation o WHERE o.loanPayment.id =:loanPaymentId")
    List<Operation> findByLoanPaymentId(@Param("loanPaymentId") UUID loanPaymentId);
}
