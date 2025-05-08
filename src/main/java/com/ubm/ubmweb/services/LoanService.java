package com.ubm.ubmweb.services;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.Counterparty;
import com.ubm.ubmweb.entities.ExpenseOperation;
import com.ubm.ubmweb.entities.LegalEntity;
import com.ubm.ubmweb.entities.Loan;
import com.ubm.ubmweb.entities.LoanPayment;
import com.ubm.ubmweb.entities.Operation;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.UpdateLoanInput;
import com.ubm.ubmweb.repository.CounterpartyRepository;
import com.ubm.ubmweb.repository.LegalEntityRepository;
import com.ubm.ubmweb.repository.LoanPaymentRepository;
import com.ubm.ubmweb.repository.LoanRepository;
import com.ubm.ubmweb.repository.OperationsRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final OperationsRepository operationsRepository;
    private final CounterpartyRepository counterpartyRepository;
    private final LegalEntityRepository legalEntityRepository;


    @Transactional
    public Loan updateLoan(Long userId, UpdateLoanInput input) {

        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        Loan loan = loanRepository.findById(input.getId())
            .orElseThrow(() -> new RuntimeException("Loan not found with id " + input.getId()));

        isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(loan.getCompany().getId(), userId));
        if(!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to the company to which the loan belongs");
        }

        boolean requiresPaymentUpdate = !loan.getAmount().equals(input.getAmount()) ||
                                        !loan.getInterestRate().equals(input.getInterestRate()) ||
                                        !loan.getIssueDate().equals(input.getIssueDate()) ||
                                        !loan.getLoanTermMonths().equals(input.getLoanTermMonths());

        loan.setName(input.getName());
        loan.setCurrency(input.getCurrency());
        loan.setAmount(input.getAmount());
        loan.setInterestRate(input.getInterestRate());
        loan.setIssueDate(input.getIssueDate());
        loan.setLoanTermMonths(input.getLoanTermMonths());

        Counterparty oldCounterparty = loan.getCounterparty();
        
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
        oldCounterparty.removeLoan(loan);

        if(counterparty != null)
            counterparty.addLoan(loan);
        loan.setLegalEntity(legalEntity);

        loanRepository.save(loan);

        if (requiresPaymentUpdate) {
            // Remove existing LoanPayments
            List<LoanPayment> existingPayments = loanPaymentRepository.findByLoanId(loan.getId());
            loanPaymentRepository.deleteAll(existingPayments);

            // Generate new LoanPayments
            List<LoanPayment> paymentSchedule = generatePaymentSchedule(loan);
            loanPaymentRepository.saveAll(paymentSchedule);
            loan.setTotalAmount(calculateTotalAmount(paymentSchedule));
        }

        return loan;
    }

    private BigDecimal calculateTotalAmount(List<LoanPayment> paymentSchedule) {
        return paymentSchedule.stream()
            .map(LoanPayment::getTotalPayment)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void deleteLoan(Long userId, Long companyId, Long id) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found with id " + id));
        
        isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(loan.getCompany().getId(), userId));

        List<LoanPayment> loanPayments = loanPaymentRepository.findByLoanId(id);
        for (LoanPayment payment : loanPayments) {
                    // Unlink any operations linked to the LoanPayment
            List<Operation> operations = operationsRepository.findByLoanPaymentId(payment.getId());
            for (Operation operation : operations) {
                operation.setLoanPayment(null);
                operationsRepository.save(operation);
            }
            loanPaymentRepository.delete(payment);
        }
        Counterparty counterparty = loan.getCounterparty();
        counterparty.removeLoan(loan);
        
        loanRepository.delete(loan);
    }


    @Transactional
    public void applyOperationAsPayment(Long userId, Long loanPaymentId, Long operationId) {
        LoanPayment loanPayment = loanPaymentRepository.findById(loanPaymentId)
            .orElseThrow(() -> new RuntimeException("LoanPayment not found with id " + loanPaymentId));
    
        Operation operation = operationsRepository.findById(operationId)
            .orElseThrow(() -> new RuntimeException("Operation not found with id " + operationId));
        
        if (operation.getCompany().getId() != loanPayment.getLoan().getCompany().getId()) {
            throw new UnauthorizedAccessException("Operation and the Loan are from different Companies");
        }
        
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(operation.getCompany().getId(), userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
    
        if (!(operation instanceof ExpenseOperation)) {
            throw new IllegalArgumentException("Operation chosen for payment must be ExpenseOperation");
        }
    
        if (operation.getLoanPayment() != null) {
            throw new IllegalStateException("Operation already used for payment");
        }
    
        BigDecimal amount = operation.getBalance();
        BigDecimal newAmountPaid = loanPayment.getAmountPaid().add(amount);
    
        Loan loan = loanRepository.findById(loanPayment.getLoan().getId())
            .orElseThrow(() -> new IllegalArgumentException("THIS SHOULD NEVER HAPPEN"));
    
        BigDecimal checkFull = loan.getAmountPaid().add(amount);
        if (checkFull.compareTo(loan.getTotalAmount()) > 0) {
            loan.setAmountPaid(loan.getTotalAmount());
        } else {
            loan.setAmountPaid(checkFull);
        }
    
        if (newAmountPaid.compareTo(loanPayment.getTotalPayment()) >= 0) {
            loanPayment.setIsPaid(true);
            loanPayment.setAmountPaid(loanPayment.getTotalPayment());
            BigDecimal overflow = newAmountPaid.subtract(loanPayment.getTotalPayment());
            carryOverOverflow(loanPayment, overflow);
        } else {
            loanPayment.setAmountPaid(newAmountPaid);
        }
    
        loanPaymentRepository.save(loanPayment);
        loanRepository.save(loan); // Ensure loan is saved after updating amountPaid
    
        // Link operation to loan payment
        operation.setLoanPayment(loanPayment);
        operationsRepository.save(operation);
    }

    @Transactional
    public void removeOperationFromPayment(Long operationId) {
        Operation operation = operationsRepository.findById(operationId)
            .orElseThrow(() -> new RuntimeException("Operation not found with id " + operationId));
    
        LoanPayment loanPayment = operation.getLoanPayment();
        if (loanPayment == null) {
            throw new IllegalStateException("Operation is not linked to any loan payment");
        }
    
        BigDecimal amount = operation.getBalance();
        BigDecimal newAmountPaid = loanPayment.getAmountPaid().subtract(amount);
    
        loanPayment.setAmountPaid(newAmountPaid);
        if (newAmountPaid.compareTo(loanPayment.getTotalPayment()) < 0) {
            loanPayment.setIsPaid(false);
        }
    
        Loan loan = loanRepository.findById(loanPayment.getLoan().getId())
            .orElseThrow(() -> new IllegalArgumentException("THIS SHOULD NEVER HAPPEN"));
        BigDecimal checkFull = loan.getAmountPaid().subtract(amount);
        if (checkFull.compareTo(BigDecimal.ZERO) < 0) {
            loan.setAmountPaid(BigDecimal.ZERO);
        } else {
            loan.setAmountPaid(checkFull);
        }
    
        loanPaymentRepository.save(loanPayment);
        loanRepository.save(loan); // Ensure loan is saved after updating amountPaid
    
        // Unlink operation from loan payment
        operation.setLoanPayment(null);
        operationsRepository.save(operation);
    }
    
    private void carryOverOverflow(LoanPayment currentPayment, BigDecimal overflow) {
        Loan loan = currentPayment.getLoan();
        List<LoanPayment> payments = loanPaymentRepository.findByLoanId(loan.getId());
    
        for (LoanPayment payment : payments) {
            if (payment.getDueDate().isAfter(currentPayment.getDueDate()) && !payment.getIsPaid()) {
                BigDecimal newAmountPaid = payment.getAmountPaid().add(overflow);
                if (newAmountPaid.compareTo(payment.getTotalPayment()) >= 0) {
                    payment.setIsPaid(true);
                    payment.setAmountPaid(payment.getTotalPayment());
                    overflow = newAmountPaid.subtract(payment.getTotalPayment());
                } else {
                    payment.setAmountPaid(newAmountPaid);
                    overflow = BigDecimal.ZERO;
                }
                loanPaymentRepository.save(payment);
                if (overflow.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }
            }
        }
    }

    public List<LoanPayment> generatePaymentSchedule(Loan loan) {
        List<LoanPayment> paymentSchedule = new ArrayList<>();
        BigDecimal monthlyInterestRate = loan.getInterestRate().divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
        LocalDate paymentDate = loan.getIssueDate();

        switch (loan.getPaymentType()) {
            case 1: // Annuity
                BigDecimal annuityMonthlyPayment = calculateAnnuityMonthlyPayment(loan.getAmount(), monthlyInterestRate, loan.getLoanTermMonths());
                generateAnnuityPaymentSchedule(paymentSchedule, loan, monthlyInterestRate, annuityMonthlyPayment, paymentDate);
                break;
            case 2: // Differentiated
                generateDifferentiatedPaymentSchedule(paymentSchedule, loan, monthlyInterestRate, paymentDate);
                break;
            case 3: // Equal Shares
                generateEqualSharesPaymentSchedule(paymentSchedule, loan, monthlyInterestRate, paymentDate);
                break;
            default:
                throw new IllegalArgumentException("Invalid payment type: " + loan.getPaymentType());
        }

        return paymentSchedule;
    }

    private void generateAnnuityPaymentSchedule(List<LoanPayment> paymentSchedule, Loan loan, BigDecimal monthlyInterestRate, BigDecimal monthlyPayment, LocalDate paymentDate) {
        BigDecimal remainingPrincipal = loan.getAmount();

        for (int i = 0; i < loan.getLoanTermMonths(); i++) {
            BigDecimal interestAmount = remainingPrincipal.multiply(monthlyInterestRate);
            BigDecimal principalAmount = monthlyPayment.subtract(interestAmount);

            LoanPayment loanPayment = new LoanPayment();
            loanPayment.setLoan(loan);
            loanPayment.setDueDate(paymentDate.plusMonths(i + 1));
            loanPayment.setPrincipalAmount(principalAmount);
            loanPayment.setInterestAmount(interestAmount);
            loanPayment.setTotalPayment(monthlyPayment);
            loanPayment.setAmountPaid(BigDecimal.ZERO);
            loanPayment.setIsPaid(false);

            paymentSchedule.add(loanPayment);

            remainingPrincipal = remainingPrincipal.subtract(principalAmount);
        }
    }

    private void generateDifferentiatedPaymentSchedule(List<LoanPayment> paymentSchedule, Loan loan, BigDecimal monthlyInterestRate, LocalDate paymentDate) {
        BigDecimal remainingPrincipal = loan.getAmount();
        BigDecimal principalAmount = loan.getAmount().divide(BigDecimal.valueOf(loan.getLoanTermMonths()), RoundingMode.HALF_UP);

        for (int i = 0; i < loan.getLoanTermMonths(); i++) {
            BigDecimal interestAmount = remainingPrincipal.multiply(monthlyInterestRate);
            BigDecimal totalPayment = principalAmount.add(interestAmount);

            LoanPayment loanPayment = new LoanPayment();
            loanPayment.setLoan(loan);
            loanPayment.setDueDate(paymentDate.plusMonths(i + 1));
            loanPayment.setPrincipalAmount(principalAmount);
            loanPayment.setInterestAmount(interestAmount);
            loanPayment.setTotalPayment(totalPayment);
            loanPayment.setAmountPaid(BigDecimal.ZERO);
            loanPayment.setIsPaid(false);

            paymentSchedule.add(loanPayment);

            remainingPrincipal = remainingPrincipal.subtract(principalAmount);
        }
    }

    private void generateEqualSharesPaymentSchedule(List<LoanPayment> paymentSchedule, Loan loan, BigDecimal monthlyInterestRate, LocalDate paymentDate) {
        BigDecimal principalAmount = loan.getAmount().divide(BigDecimal.valueOf(loan.getLoanTermMonths()), RoundingMode.HALF_UP);
        BigDecimal totalInterest = loan.getAmount().multiply(loan.getInterestRate()).multiply(BigDecimal.valueOf(loan.getLoanTermMonths())).divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
        BigDecimal equalInterestShare = totalInterest.divide(BigDecimal.valueOf(loan.getLoanTermMonths()), RoundingMode.HALF_UP);
        BigDecimal totalPayment = principalAmount.add(equalInterestShare);

        for (int i = 0; i < loan.getLoanTermMonths(); i++) {
            LoanPayment loanPayment = new LoanPayment();
            loanPayment.setLoan(loan);
            loanPayment.setDueDate(paymentDate.plusMonths(i + 1));
            loanPayment.setPrincipalAmount(principalAmount);
            loanPayment.setInterestAmount(equalInterestShare);
            loanPayment.setTotalPayment(totalPayment);
            loanPayment.setAmountPaid(BigDecimal.ZERO);
            loanPayment.setIsPaid(false);

            paymentSchedule.add(loanPayment);
        }
    }

    private BigDecimal calculateAnnuityMonthlyPayment(BigDecimal principal, BigDecimal monthlyInterestRate, int termMonths) {
        if (monthlyInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(termMonths), RoundingMode.HALF_UP);
        } else {
            BigDecimal numerator = principal.multiply(monthlyInterestRate);
            MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
            BigDecimal denominator = BigDecimal.ONE.subtract(BigDecimal.ONE.add(monthlyInterestRate, mc).pow(-termMonths, mc));
            return numerator.divide(denominator, RoundingMode.HALF_UP);
        }
    }

    @Transactional(readOnly = true)
    public List<Loan> findLoans(Long userId, Long companyId, String name) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if(!isAssociated){
            throw new UnauthorizedAccessException("User does not have acces to the company with id:" + companyId);
        }
        return loanRepository.findLoans(companyId, name);
    }

    @Transactional(readOnly = true)
    public Loan findLoanById(Long userId, Long companyId, Long loanId) {
        Loan loan = loanRepository.findLoanById(loanId, companyId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found for id: " + loanId));
        
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

        return loan;
    }

}