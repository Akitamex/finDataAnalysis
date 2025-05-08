package com.ubm.ubmweb.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.Loan;



@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID>, JpaSpecificationExecutor<Loan>  {


    @Query("SELECT l FROM Loan l WHERE l.company.id =:companyId AND (:name IS NULL OR l.name =:name)")
    List<Loan> findLoans(@Param("companyId") UUID companyId, @Param("name") String name);

    @Query("SELECT l FROM Loan l WHERE l.id =:loanId AND l.company.id =:companyId")
    Optional<Loan> findLoanById(@Param("loanId") UUID loanId, @Param("companyId") UUID companyId);
}