package com.ubm.ubmweb.services;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.BankAccount;
import com.ubm.ubmweb.entities.BankBankAccount;
import com.ubm.ubmweb.entities.CashBankAccount;
import com.ubm.ubmweb.entities.FundBankAccount;
import com.ubm.ubmweb.entities.LegalEntity;
import com.ubm.ubmweb.entities.Operation;
import com.ubm.ubmweb.entities.Company;
import com.ubm.ubmweb.graphql.dto.CreateBankBankAccountInput;
import com.ubm.ubmweb.graphql.dto.CreateCashBankAccountInput;
import com.ubm.ubmweb.graphql.dto.CreateFundBankAccountInput;
import com.ubm.ubmweb.graphql.dto.UpdateBankBankAccountInput;
import com.ubm.ubmweb.graphql.dto.UpdateCashBankAccountInput;
import com.ubm.ubmweb.graphql.dto.UpdateFundBankAccountInput;
import com.ubm.ubmweb.repository.BankAccountRepository;
import com.ubm.ubmweb.repository.BankBankAccountRepository;
import com.ubm.ubmweb.repository.CashBankAccountRepository;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.FundBankAccountRepository;
import com.ubm.ubmweb.repository.LegalEntityRepository;
import com.ubm.ubmweb.repository.OperationsRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    private final BankBankAccountRepository bankBankAccountRepository;

    private final CashBankAccountRepository cashBankAccountRepository;

    private final FundBankAccountRepository fundBankAccountRepository;

    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;

    private final OperationsRepository operationsRepository;

    private final CompanyRepository companyRepository;

    private final LegalEntityRepository legalEntityRepository;

    @Transactional(readOnly = true)
    public BankAccount getBankAccountByIdAndCompanyId(Long userId, Long companyId, Long id){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        BankAccount bankAccount = bankAccountRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("BankAccount not found for the given id: " + id));

        if (!bankAccount.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested BankAccount does not belong to the provided company.");
        }

        return bankAccount;
    }

    @Transactional
    public BankBankAccount createBankBankAccount(Long userId, CreateBankBankAccountInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        BankBankAccount bankAccount = new BankBankAccount();
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));
        LegalEntity legalEntity = legalEntityRepository.findById(input.getLegalEntityId())
            .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getLegalEntityId()));
        if (!legalEntity.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("[createBank]Requested LegalEntity does not belong to the provided company.");
        }

        bankAccount.setCompany(company);
        bankAccount.setName(input.getName());
        bankAccount.setCurrency(input.getCurrency());
        bankAccount.setBalance(input.getBalance());
        bankAccount.setBank(input.getBank());
        bankAccount.setBIC(input.getBIC());
        bankAccount.setCorrespondentAccount(input.getCorrespondentAccount());
        bankAccount.setAccountNumber(input.getAccountNumber());
        bankAccount.setType("BANK");
        bankAccount.setLegalEntity(legalEntity);
        bankAccount.setCompany(company);


        legalEntity.addBankAccount(bankAccount);
        company.addBankAccount(bankAccount);

        return bankBankAccountRepository.save(bankAccount);
    }

    @Transactional
    public CashBankAccount createCashBankAccount(Long userId, CreateCashBankAccountInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        CashBankAccount bankAccount = new CashBankAccount();
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));
        LegalEntity legalEntity = legalEntityRepository.findById(input.getLegalEntityId())
            .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getLegalEntityId()));
        if (!legalEntity.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("[createBank]Requested LegalEntity does not belong to the provided company.");
        }

        bankAccount.setCompany(company);
        bankAccount.setName(input.getName());
        bankAccount.setCurrency(input.getCurrency());
        bankAccount.setBalance(input.getBalance());
        bankAccount.setType("CASH");
        bankAccount.setLegalEntity(legalEntity);
        bankAccount.setCompany(company);
        legalEntity.addBankAccount(bankAccount);
        company.addBankAccount(bankAccount);

        return cashBankAccountRepository.save(bankAccount);
    }

    @Transactional
    public FundBankAccount createFundBankAccount(Long userId, CreateFundBankAccountInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        FundBankAccount bankAccount = new FundBankAccount();
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));
        LegalEntity legalEntity = legalEntityRepository.findById(input.getLegalEntityId())
            .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getLegalEntityId()));
        if (!legalEntity.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("[createBank]Requested LegalEntity does not belong to the provided company.");
        }

        bankAccount.setCompany(company);
        bankAccount.setName(input.getName());
        bankAccount.setCurrency(input.getCurrency());
        bankAccount.setBalance(input.getBalance());
        bankAccount.setType("FUND");
        bankAccount.setLegalEntity(legalEntity);
        bankAccount.setCompany(company);

        legalEntity.addBankAccount(bankAccount);
        company.addBankAccount(bankAccount);

        return fundBankAccountRepository.save(bankAccount);
    }

        

    @Transactional(readOnly = true)
    public List<BankAccount> findBankAccounts(Long userId, Long companyId, String name, String currency, String type, String bank, String BIC, String correspondentAccount, String accountNumber, List<Long> legalEntityIds, List<Long> ids) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        List<LegalEntity> legalEntities = legalEntityRepository.findByIdInAndCompanyId(legalEntityIds, companyId);
        if (legalEntityIds != null && !legalEntityIds.isEmpty() && legalEntities.size() != legalEntityIds.size()) {
            throw new IllegalArgumentException("One or more LegalEntity IDs do not belong to the specified company.");
        }
        
        Specification<BankAccount> spec = (Root<BankAccount> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Mandatory filter for companyId
            predicates.add(cb.equal(root.get("company").get("id"), companyId));
            
            // Optional filters
            if (name != null) predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if (currency != null) predicates.add(cb.equal(root.get("currency"), currency));
            if (type != null) predicates.add(cb.equal(root.get("type"), type));
            if (bank != null) predicates.add(cb.equal(root.get("bank"), bank));
            if (BIC != null) predicates.add(cb.equal(root.get("BIC"), BIC));
            if (correspondentAccount != null) predicates.add(cb.equal(root.get("correspondentAccount"), correspondentAccount));
            if (accountNumber != null) predicates.add(cb.equal(root.get("accountNumber"), accountNumber));
            
            // Filtering by multiple LegalEntity IDs
            if (legalEntityIds != null && !legalEntityIds.isEmpty()) {
                predicates.add(root.get("legalEntity").get("id").in(legalEntityIds));
            }
            
            if (ids != null && !ids.isEmpty()) {
                predicates.add(root.get("id").in(ids));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return bankAccountRepository.findAll(spec);
    }

    @Transactional
    public BankBankAccount updateBankBankAccount(Long userId, UpdateBankBankAccountInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        BankBankAccount bankAccount = bankBankAccountRepository.findById(input.getId())
            .orElseThrow(() -> new IllegalArgumentException("[UpdateBank]BankAccount not found for the given id: " + input.getId()));
        if (!bankAccount.getCompany().getId().equals(input.getCompanyId())) {
            throw new UnauthorizedAccessException("[UpdateBank]Requested BankAccount does not belong to the provided company.");
        }
        LegalEntity newLegalEntity = legalEntityRepository.findById(input.getLegalEntityId())
            .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getLegalEntityId()));
        if (!newLegalEntity.getCompany().getId().equals(input.getCompanyId())) {
            throw new UnauthorizedAccessException("[UpdateBank]Requested LegalEntity does not belong to the provided company.");
        }
        LegalEntity oldLegalEntity = bankAccount.getLegalEntity();
        oldLegalEntity.removeBankAccount(bankAccount);

        bankAccount.setName(input.getName());
        bankAccount.setCurrency(input.getCurrency());
        bankAccount.setBalance(input.getBalance());
        bankAccount.setBank(input.getBank());
        bankAccount.setBIC(input.getBIC());
        bankAccount.setCorrespondentAccount(input.getCorrespondentAccount());
        bankAccount.setAccountNumber(input.getAccountNumber());
        bankAccount.setLegalEntity(newLegalEntity);

        newLegalEntity.addBankAccount(bankAccount);

        return bankBankAccountRepository.save(bankAccount);
    }

    @Transactional
    public CashBankAccount updateCashBankAccount(Long userId, UpdateCashBankAccountInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        CashBankAccount bankAccount = cashBankAccountRepository.findById(input.getId())
            .orElseThrow(() -> new IllegalArgumentException("[UpdateBank]BankAccount not found for the given id: " + input.getId()));
        if (!bankAccount.getCompany().getId().equals(input.getCompanyId())) {
            throw new UnauthorizedAccessException("[UpdateBank]Requested BankAccount does not belong to the provided company.");
        }
        LegalEntity newLegalEntity = legalEntityRepository.findById(input.getLegalEntityId())
            .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getLegalEntityId()));
        if (!newLegalEntity.getCompany().getId().equals(input.getCompanyId())) {
            throw new UnauthorizedAccessException("[UpdateBank]Requested LegalEntity does not belong to the provided company.");
        }
        LegalEntity oldLegalEntity = bankAccount.getLegalEntity();
        oldLegalEntity.removeBankAccount(bankAccount);

        bankAccount.setName(input.getName());
        bankAccount.setCurrency(input.getCurrency());
        bankAccount.setBalance(input.getBalance());
        bankAccount.setLegalEntity(newLegalEntity);

        newLegalEntity.addBankAccount(bankAccount);

        return cashBankAccountRepository.save(bankAccount);
    }

    @Transactional
    public FundBankAccount updateFundBankAccount(Long userId, UpdateFundBankAccountInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        FundBankAccount bankAccount = fundBankAccountRepository.findById(input.getId())
            .orElseThrow(() -> new IllegalArgumentException("[UpdateBank]BankAccount not found for the given id: " + input.getId()));
        if (!bankAccount.getCompany().getId().equals(input.getCompanyId())) {
            throw new UnauthorizedAccessException("[UpdateBank]Requested BankAccount does not belong to the provided company.");
        }
        LegalEntity newLegalEntity = legalEntityRepository.findById(input.getLegalEntityId())
            .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getLegalEntityId()));
        if (!newLegalEntity.getCompany().getId().equals(input.getCompanyId())) {
            throw new UnauthorizedAccessException("[UpdateBank]Requested LegalEntity does not belong to the provided company.");
        }
        LegalEntity oldLegalEntity = bankAccount.getLegalEntity();
        oldLegalEntity.removeBankAccount(bankAccount);

        bankAccount.setName(input.getName());
        bankAccount.setCurrency(input.getCurrency());
        bankAccount.setBalance(input.getBalance());
        bankAccount.setLegalEntity(newLegalEntity);

        newLegalEntity.addBankAccount(bankAccount);

        return fundBankAccountRepository.save(bankAccount);
    }

    @Transactional
    public void deleteBankAccount(Long id, Long userId, Long companyId) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to this company.");
        }
        
        BankAccount bankAccount = bankAccountRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("BankAccount not found for the given id: " + id));
        
        if (!bankAccount.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested BankAccount does not belong to the provided company.");
        }
        List<Operation> operations = operationsRepository.findByBankAccountId(id);
        if(!operations.isEmpty()){
            throw new IllegalStateException("Cannot delete BankAccount as it has associated operations.");
        }

        // LegalEntity legalEntity = bankAccount.getLegalEntity();
        // legalEntity.removeBankAccount(bankAccount);
        
        bankAccountRepository.deleteById(id);
    }
}