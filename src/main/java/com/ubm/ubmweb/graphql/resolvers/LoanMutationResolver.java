package com.ubm.ubmweb.graphql.resolvers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.Company;
import com.ubm.ubmweb.entities.Counterparty;
import com.ubm.ubmweb.entities.LegalEntity;
import com.ubm.ubmweb.entities.Loan;
import com.ubm.ubmweb.entities.LoanPayment;
import com.ubm.ubmweb.entities.Operation;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateLoanInput;
import com.ubm.ubmweb.graphql.dto.UpdateLoanInput;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.CounterpartyRepository;
import com.ubm.ubmweb.repository.LegalEntityRepository;
import com.ubm.ubmweb.repository.LoanPaymentRepository;
import com.ubm.ubmweb.repository.LoanRepository;
import com.ubm.ubmweb.repository.OperationsRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;
import com.ubm.ubmweb.services.LoanService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoanMutationResolver implements GraphQLMutationResolver{
    private final LoanService loanService;
    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final CounterpartyRepository counterpartyRepository;
    private final LegalEntityRepository legalEntityRepository;
    private final OperationsRepository operationsRepository;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public Loan createLoan(Long userId, CreateLoanInput input) {

        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company does not exist for id: " + input.getCompanyId()));

        Loan loan = new Loan();
        loan.setCompany(company);
        loan.setName(input.getName());
        loan.setCurrency(input.getCurrency());
        loan.setAmount(input.getAmount());
        loan.setInterestRate(input.getInterestRate());
        loan.setIssueDate(LocalDate.parse(input.getIssueDate()));
        loan.setLoanTermMonths(input.getLoanTermMonths());
        loan.setPaymentType(input.getType());
        loan.setAmountPaid(BigDecimal.ZERO);
        
        Counterparty counterparty = null;
        if (input.getCounterpartyId() != null) {
            counterparty = counterpartyRepository.findById(input.getCounterpartyId())
                .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + input.getCounterpartyId()));
            if (!counterparty.getCompany().getId().equals(input.getCompanyId())) {
                throw new UnauthorizedAccessException("Requested Counterparty does not belong to the provided company.");
            }
        }
        LegalEntity legalEntity = null;
        if (input.getLegalEntityId() != null) {
            legalEntity = legalEntityRepository.findById(input.getLegalEntityId())
                .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getCounterpartyId()));
            if (!legalEntity.getCompany().getId().equals(input.getCompanyId())) {
                throw new UnauthorizedAccessException("Requested LegalEntity does not belong to the provided company.");
            }
        } else {
            throw new IllegalArgumentException("LegalEntity cannot be null");
        }
        loan.setCounterparty(counterparty);
        loan.setLegalEntity(legalEntity);
        if(counterparty != null)
            counterparty.addLoan(loan);
        

        loan = loanRepository.save(loan);
        List<LoanPayment> paymentSchedule = loanService.generatePaymentSchedule(loan);
        loan.setTotalAmount(calculateTotalAmount(paymentSchedule));
        loanPaymentRepository.saveAll(paymentSchedule);

        return loan;
    }
    private BigDecimal calculateTotalAmount(List<LoanPayment> paymentSchedule) {
        return paymentSchedule.stream()
            .map(LoanPayment::getTotalPayment)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public LoanPayment applyOperationAsPayment(Long userId, Long loanPaymentId, Long operationId) {
        loanService.applyOperationAsPayment(userId, loanPaymentId, operationId);
        return loanPaymentRepository.findById(loanPaymentId)
            .orElseThrow(() -> new RuntimeException("LoanPayment not found with id " + loanPaymentId));
    }

    public LoanPayment removeOperationFromPayment(Long userId, Long operationId) {
        Operation operation = operationsRepository.findById(operationId)
            .orElseThrow(() -> new RuntimeException("Operation not found with id " + operationId));

        Boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(operation.getCompany().getId(),userId));
        if(!isAssociated){
            throw new UnauthorizedAccessException("User doesn't have acces to the company of the operation with id:" + operationId);
        }

        
        loanService.removeOperationFromPayment(operationId);
        LoanPayment loanPayment = operation.getLoanPayment();

        return loanPayment;
    }

    public Loan updateLoan(Long userId, UpdateLoanInput input) {
        return loanService.updateLoan(userId, input);
    }

    public Boolean deleteLoan(Long userId, Long companyId, Long id){
        try {
            loanService.deleteLoan(userId, companyId, id);
            return true;
        } catch (Exception e) {
            // Log the exception, handle it as needed
            if (e instanceof IllegalStateException)
                throw new IllegalStateException(e.getMessage());
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return false;
        }
    }
}
