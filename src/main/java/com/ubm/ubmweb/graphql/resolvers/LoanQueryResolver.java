package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.Loan;
import com.ubm.ubmweb.entities.LoanPayment;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.repository.LoanPaymentRepository;
import com.ubm.ubmweb.repository.LoanRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;
import com.ubm.ubmweb.services.LoanService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class LoanQueryResolver implements GraphQLQueryResolver{
    private final LoanService loanService;
    private final LoanPaymentRepository loanPaymentRepository;
    private final LoanRepository loanRepository;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;

    public List<Loan> findLoans(Long userId, Long companyId, String name) {
        return loanService.findLoans(userId, companyId, name);
    }

    public Loan findLoanById(Long userId, Long companyId, Long loanId) {
        return loanService.findLoanById(userId, companyId, loanId);
    }

    public List<LoanPayment> loanPayments(Long userId, Long companyId, Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new IllegalArgumentException("loan not found for id:" + loanId));
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(loan.getCompany().getId(), userId));
        if(!isAssociated){
            throw new UnauthorizedAccessException("User does not have acces to the company of the loan with id:" + loanId);
        }
        isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if(!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to the company in which they request a loan");
        }
        if(companyId != loan.getCompany().getId()){
            throw new UnauthorizedAccessException("The Loan exists in another company owned by user");
        }
        return loanPaymentRepository.findByLoanId(loanId);
    }
}