package com.ubm.ubmweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.LoanPayment;

import java.util.List;
import java.util.UUID;


@Repository
public interface LoanPaymentRepository extends JpaRepository<LoanPayment, UUID>, JpaSpecificationExecutor<LoanPayment>  {
    @Query("SELECT lp FROM LoanPayment lp WHERE lp.loan.id =:loanId")
    List<LoanPayment> findByLoanId(@Param("loanId") UUID loanId);
}
