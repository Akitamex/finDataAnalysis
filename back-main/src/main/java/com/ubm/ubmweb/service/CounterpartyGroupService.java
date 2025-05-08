package com.ubm.ubmweb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.model.Company;
import com.ubm.ubmweb.model.Counterparty;
import com.ubm.ubmweb.model.CounterpartyGroup;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.CounterpartyGroupRepository;
import com.ubm.ubmweb.repository.CounterpartyRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CounterpartyGroupService {
    
    private final CounterpartyGroupRepository counterpartyGroupRepository;

    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;

    private final CompanyRepository companyRepository;

    private final CounterpartyRepository counterpartyRepository;

    @Transactional
    public CounterpartyGroup createCounterpartyGroup(UUID userId, UUID companyId, String name){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + companyId));

        CounterpartyGroup counterpartyGroup = new CounterpartyGroup();
        
        counterpartyGroup.setName(name);
        counterpartyGroup.setCompany(company);

        company.addCounterpartyGroup(counterpartyGroup);
        return counterpartyGroupRepository.save(counterpartyGroup);
    }

    @Transactional(readOnly = true)
    public List<CounterpartyGroup> findCounterpartyGroups(UUID userId, UUID companyId, String name) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Specification<CounterpartyGroup> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Mandatory filter for companyId
            predicates.add(cb.equal(root.get("company").get("id"), companyId));
            
            // Optional filter for name
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return counterpartyGroupRepository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public CounterpartyGroup getCounterpartyGroupByIdAndCompanyId(UUID userId, UUID companyId, UUID id){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        CounterpartyGroup counterpartyGroup = counterpartyGroupRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("CounterpartyGroup not found for the given id: " + id));
        if (!counterpartyGroup.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested CounterpartyGroup does not belong to the provided company.");
        }
        return counterpartyGroup;
    }

    @Transactional
    public CounterpartyGroup updateCounterpartyGroup(UUID userId, UUID companyId, UUID id, String name){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        CounterpartyGroup counterpartyGroup = counterpartyGroupRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("CounterpartyGroup not found for the given id: " + id));
        if (!counterpartyGroup.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested CounterpartyGroup does not belong to the provided company.");
        }
        counterpartyGroup.setName(name);
        return counterpartyGroupRepository.save(counterpartyGroup);
    }

    @Transactional
    public void deleteCounterpartyGroup(UUID userId, UUID companyId, UUID id){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        CounterpartyGroup counterpartyGroup = counterpartyGroupRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("CounterpartyGroup not found for the given id: " + id));
        if (!counterpartyGroup.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested CounterpartyGroup does not belong to the provided company.");
        }

        List<Counterparty> counterparties = counterpartyRepository.findByCounterpartyGroupId(id);
        if(!counterparties.isEmpty()){
            throw new IllegalStateException("Cannot delete CounterpartyGroup as it has associated Counterparties.");
        }

        counterpartyGroupRepository.deleteById(id);
    }
}
