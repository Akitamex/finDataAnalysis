package com.ubm.ubmweb.service;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.graphql.dto.CreateLegalEntityInput;
import com.ubm.ubmweb.graphql.dto.UpdateLegalEntityInput;
import com.ubm.ubmweb.model.BankAccount;
import com.ubm.ubmweb.model.Company;
import com.ubm.ubmweb.model.LegalEntity;
import com.ubm.ubmweb.repository.BankAccountRepository;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.LegalEntityRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LegalEntityService {

    private final LegalEntityRepository legalEntityRepository;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final CompanyRepository companyRepository;
    private final BankAccountRepository bankAccountRepository;

    @Transactional
    public LegalEntity createLegalEntity(UUID userId, CreateLegalEntityInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));

        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setName(input.getName());
        legalEntity.setFullName(input.getFullName());
        legalEntity.setIin(input.getIin());
        legalEntity.setCor(input.getCor());
        legalEntity.setMsrn(input.getMsrn());
        legalEntity.setLegalAddress(input.getLegalAddress());
        legalEntity.setPhoneNum(input.getPhoneNum());
        legalEntity.setVat(input.getVat());
        legalEntity.setCompany(company);

        company.addLegalEntity(legalEntity);
        return legalEntityRepository.save(legalEntity);
    }

    @Transactional(readOnly = true)
    public List<LegalEntity> findLegalEntities(UUID userId, UUID companyId, String name, String fullName, String IIN, String COR, String MSRN, String legalAddress, String phoneNum, Boolean VAT) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        Specification<LegalEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Mandatory company ID
            predicates.add(cb.equal(root.get("company").get("id"), companyId));

            // Optional parameters
            if (name != null) predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if (fullName != null) predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%"));
            if (IIN != null) predicates.add(cb.equal(root.get("IIN"), IIN));
            if (COR != null) predicates.add(cb.equal(root.get("COR"), COR));
            if (MSRN != null) predicates.add(cb.equal(root.get("MSRN"), MSRN));
            if (legalAddress != null) predicates.add(cb.like(cb.lower(root.get("legalAddress")), "%" + legalAddress.toLowerCase() + "%"));
            if (phoneNum != null) predicates.add(cb.equal(root.get("phoneNum"), phoneNum));
            if (VAT != null) predicates.add(cb.equal(root.get("VAT"), VAT));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return legalEntityRepository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public LegalEntity getLegalEntityByIdAndCompanyId(UUID userId, UUID companyId, UUID id){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        LegalEntity legalEntity = legalEntityRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + id));
        if (!legalEntity.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested LegalEntity does not belong to the provided company.");
        }

        return legalEntity;
    }

    @Transactional
    public LegalEntity updateLegalEntity(UUID userId, UpdateLegalEntityInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        LegalEntity legalEntity = legalEntityRepository.findById(input.getId())
            .orElseThrow(() -> new IllegalArgumentException("[update]LegalEntity not found for the given id: " + input.getId()));
        if (!legalEntity.getCompany().getId().equals(input.getCompanyId())) {
            throw new UnauthorizedAccessException("[update]Requested LegalEntity does not belong to the provided company.");
        }
        legalEntity.setName(input.getName());
        legalEntity.setFullName(input.getFullName());
        legalEntity.setIin(input.getIin());
        legalEntity.setCor(input.getCor());
        legalEntity.setMsrn(input.getMsrn());
        legalEntity.setLegalAddress(input.getLegalAddress());
        legalEntity.setPhoneNum(input.getPhoneNum());
        legalEntity.setVat(input.getVat());
        return legalEntityRepository.save(legalEntity);
    }

    @Transactional
    public void deleteLegalEntity(UUID userId, UUID companyId, UUID id){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        LegalEntity legalEntity = legalEntityRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("[delete]LegalEntity not found for the given id: " + id));
        if (!legalEntity.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("[delete]Requested LegalEntity does not belong to the provided company.");
        }
        List<BankAccount> bankAccounts = bankAccountRepository.findByLegalEntityId(id);

        if (!bankAccounts.isEmpty()) {
            throw new IllegalStateException("Cannot delete LegalEntity as it has associated bankAccounts.");
        }

        legalEntityRepository.deleteById(id);
    }
}
