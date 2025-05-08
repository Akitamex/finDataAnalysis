package com.ubm.ubmweb.graphql.resolvers;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.Loan;
import com.ubm.ubmweb.entities.Obligation;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.models.DebtAnalysis;
import com.ubm.ubmweb.models.DebtAnalysisProjectsPayable;
import com.ubm.ubmweb.models.DebtAnalysisProjectsRecievable;
import com.ubm.ubmweb.repository.LoanRepository;
// import com.ubm.ubmweb.repository.CounterpartyRepository;
import com.ubm.ubmweb.repository.ObligationRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DebtAnalysisQueryResolver implements GraphQLQueryResolver{

    private final ObligationRepository obligationRepository;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final LoanRepository loanRepository;
    
    public DebtAnalysis debtAnalysis(Long companyId, Long userId,  Boolean group){
        Boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId,userId));
        if(!isAssociated){
            throw new UnauthorizedAccessException("User does not have acces to company with id: " + companyId);
        }

        DebtAnalysis debtAnalysis = new DebtAnalysis();
        List<String> debtNames = new ArrayList<String>();
        List<BigDecimal> debts = new ArrayList<BigDecimal>();
        BigDecimal accountsRecievableSum = BigDecimal.ZERO;
        BigDecimal accountsPayableSum = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        // final BigDecimal[] arSum = { BigDecimal.ZERO };
        
        List<Obligation> accountsRecievableObligations = obligationRepository.findAccountsRecievable(companyId); //Дебиторская
        List<Obligation> accountsPayableObligations = obligationRepository.findAccountsPayable(companyId); //Кредиторская
       
        List<Loan> loans = loanRepository.findLoans(companyId, null);
            // List<Counterparty> accountsRecievableCounterparties = counterpartyRepository.findAccountsRecievable(companyId);
        // List<Counterparty> accountsPayableCounterparties = counterpartyRepository.findAccountsPayable(companyId);    
        List<String> rows = new ArrayList<String>();
        rows.add("ЗАДОЛЖЕННОСТЬ");
        rows.add("Дебиторская задолженность");
        rows.add("Кредиторская задолженность");
        rows.add("Итого");
        debtAnalysis.setRows(rows);

        if(!group){
            List<String> accountsRecievableNames = new ArrayList<String>();
            List<BigDecimal> accountsRecievable = new ArrayList<BigDecimal>();
            // final BigDecimal[] arSum = { BigDecimal.ZERO };

            List<String> accountsPayableNames = new ArrayList<String>();
            List<BigDecimal> accountsPayable = new ArrayList<BigDecimal>();


            Map<String, List<Obligation>> accountsRecievableByCounterparty = accountsRecievableObligations.stream()
            .collect(Collectors.groupingBy(obligation -> obligation.getCounterparty().getTitle()));

            Map<String, List<Obligation>> accountsPayableByCounterparty = accountsPayableObligations.stream()
            .collect(Collectors.groupingBy(obligation -> obligation.getCounterparty().getTitle()));

            // accountsRecievableByCounterparty.forEach((counterpartyName, obligations) -> {
            //     accountsRecievableNames.add(counterpartyName);
            //     BigDecimal sum = BigDecimal.ZERO;
            //     for (Obligation obligation : obligations) {
            //         sum = sum.add(obligation.getBalance());
            //     }
            //     accountsRecievable.add(sum);
            //     arSum[0] = arSum[0].add(sum);
            // });
            for (Map.Entry<String, List<Obligation>> entry : accountsRecievableByCounterparty.entrySet()) {
                String counterpartyName = entry.getKey();
                List<Obligation> counterpartyObligations = entry.getValue();
    
                accountsRecievableNames.add(counterpartyName);
    
                BigDecimal sum = BigDecimal.ZERO;
                for (Obligation obligation : counterpartyObligations) {
                    sum = sum.add(obligation.getBalance());
                }
                accountsRecievable.add(sum);
                accountsRecievableSum = accountsRecievableSum.add(sum);
            }

            for (Map.Entry<String, List<Obligation>> entry : accountsPayableByCounterparty.entrySet()) {
                String counterpartyName = entry.getKey();
                List<Obligation> counterpartyObligations = entry.getValue();
    
                accountsPayableNames.add(counterpartyName);
    
                BigDecimal sum = BigDecimal.ZERO;
                for (Obligation obligation : counterpartyObligations) {
                    sum = sum.add(obligation.getBalance());
                }
                accountsPayable.add(sum);
                accountsPayableSum = accountsPayableSum.add(sum);
            }

            for(Loan loan: loans){
                if(loan.getAmountPaid().compareTo(loan.getTotalAmount()) >= 0){
                    continue;
                }
                debtNames.add(loan.getName());
                BigDecimal loanTotal = BigDecimal.ZERO;
                loanTotal = loan.getTotalAmount().subtract(loan.getAmountPaid());
                debts.add(loanTotal);
                accountsPayableSum = accountsPayableSum.add(loanTotal);
            }

            debtAnalysis.setAccountsRecievableNames(accountsRecievableNames);
            debtAnalysis.setAccountsRecievableSum(accountsRecievableSum);
            debtAnalysis.setAccountsRecievable(accountsRecievable);
            debtAnalysis.setAccountsPayableNames(accountsPayableNames);
            debtAnalysis.setAccountsPayableSum(accountsPayableSum);
            debtAnalysis.setAccountsPayable(accountsPayable);
            debtAnalysis.setDebtNames(debtNames);
            debtAnalysis.setDebts(debts);
        }
        else{
            List<DebtAnalysisProjectsRecievable> debtAnalysisProjectsRecievable = new ArrayList<DebtAnalysisProjectsRecievable>();
            List<DebtAnalysisProjectsPayable> debtAnalysisProjectsPayable = new ArrayList<DebtAnalysisProjectsPayable>();
            
            Map<String, List<Obligation>> accountsRecievableByProject = accountsRecievableObligations.stream()
                .collect(Collectors.groupingBy(obligation -> obligation.getProject() != null ? obligation.getProject().getName() : "Без проекта"));

            Map<String, List<Obligation>> accountsPayableByProject = accountsPayableObligations.stream()
                .collect(Collectors.groupingBy(obligation -> obligation.getProject() != null ? obligation.getProject().getName() : "Без проекта"));

            for(Map.Entry<String, List<Obligation>> i : accountsRecievableByProject.entrySet()){
                DebtAnalysisProjectsRecievable projectRecievable = new DebtAnalysisProjectsRecievable();
                List<String> accountsRecievableNames = new ArrayList<String>();
                List<BigDecimal> accountsRecievable = new ArrayList<BigDecimal>();
                BigDecimal projectSum = BigDecimal.ZERO;

                projectRecievable.setProjectName(i.getKey());
                List<Obligation> obligationsRecievable = i.getValue();
                
                Map<String, List<Obligation>> accountsRecievableByCounterparty = obligationsRecievable.stream()
                    .collect(Collectors.groupingBy(obligation -> obligation.getCounterparty().getTitle()));
                
                for (Map.Entry<String, List<Obligation>> entry : accountsRecievableByCounterparty.entrySet()) {
                    String counterpartyName = entry.getKey();
                    List<Obligation> counterpartyObligations = entry.getValue();
        
                    accountsRecievableNames.add(counterpartyName);
        
                    BigDecimal sum = BigDecimal.ZERO;
                    for (Obligation obligation : counterpartyObligations) {
                        sum = sum.add(obligation.getBalance());
                    }
                    accountsRecievable.add(sum);
                    accountsRecievableSum = accountsRecievableSum.add(sum);
                    projectSum = projectSum.add(sum);
                }

                for(Loan loan: loans){
                    if(loan.getAmountPaid().compareTo(loan.getTotalAmount()) >= 0){
                        continue;
                    }
                    debtNames.add(loan.getName());
                    BigDecimal loanTotal = BigDecimal.ZERO;
                    loanTotal = loan.getTotalAmount().subtract(loan.getAmountPaid());
                    debts.add(loanTotal);
                    accountsPayableSum = accountsPayableSum.add(loanTotal);
                }

                projectRecievable.setAccountsRecievable(accountsRecievable);
                projectRecievable.setAccountsRecievableNames(accountsRecievableNames);
                projectRecievable.setAccountsRecievableSum(projectSum);
                debtAnalysisProjectsRecievable.add(projectRecievable);
                debtAnalysis.setDebtNames(debtNames);
                debtAnalysis.setDebts(debts);
            }
            debtAnalysis.setDebtAnalysisProjectsRecievable(debtAnalysisProjectsRecievable);

            for(Map.Entry<String, List<Obligation>> i : accountsPayableByProject.entrySet()){
                DebtAnalysisProjectsPayable projectPayable = new DebtAnalysisProjectsPayable();
                List<String> accountsPayableNames = new ArrayList<String>();
                List<BigDecimal> accountsPayable = new ArrayList<BigDecimal>();
                BigDecimal projectSum = BigDecimal.ZERO;

                projectPayable.setProjectName(i.getKey());
                List<Obligation> obligationsPayable = i.getValue();
                
                Map<String, List<Obligation>> accountsPayableByCounterparty = obligationsPayable.stream()
                    .collect(Collectors.groupingBy(obligation -> obligation.getCounterparty().getTitle()));
                
                for (Map.Entry<String, List<Obligation>> entry : accountsPayableByCounterparty.entrySet()) {
                    String counterpartyName = entry.getKey();
                    List<Obligation> counterpartyObligations = entry.getValue();
        
                    accountsPayableNames.add(counterpartyName);
        
                    BigDecimal sum = BigDecimal.ZERO;
                    for (Obligation obligation : counterpartyObligations) {
                        sum = sum.add(obligation.getBalance());
                    }
                    accountsPayable.add(sum);
                    accountsPayableSum = accountsPayableSum.add(sum);
                    projectSum = projectSum.add(sum);
                }
                projectPayable.setAccountsPayable(accountsPayable);
                projectPayable.setAccountsPayableNames(accountsPayableNames);
                projectPayable.setAccountsPayableSum(projectSum);
                debtAnalysisProjectsPayable.add(projectPayable);
            }

            debtAnalysis.setDebtAnalysisProjectsPayable(debtAnalysisProjectsPayable);
            debtAnalysis.setAccountsPayableSum(accountsPayableSum);
            debtAnalysis.setAccountsRecievableSum(accountsRecievableSum);
        }
        total = accountsRecievableSum.subtract(accountsPayableSum);
        debtAnalysis.setTotal(total);
        
        return debtAnalysis;
    }
}
