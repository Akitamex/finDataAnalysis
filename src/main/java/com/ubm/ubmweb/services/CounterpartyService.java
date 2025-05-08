package com.ubm.ubmweb.services;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.Company;
import com.ubm.ubmweb.entities.Counterparty;
import com.ubm.ubmweb.entities.CounterpartyGroup;
import com.ubm.ubmweb.entities.Operation;
import com.ubm.ubmweb.graphql.dto.CreateCounterpartyInput;
import com.ubm.ubmweb.graphql.dto.UpdateCounterpartyInput;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.CounterpartyGroupRepository;
import com.ubm.ubmweb.repository.CounterpartyRepository;
import com.ubm.ubmweb.repository.OperationsRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
// import org.springframework.lang.NonNull;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
// import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CounterpartyService {

    private final CounterpartyRepository counterpartyRepository;

    private final CounterpartyGroupRepository counterpartyGroupRepository;

    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;

    private final CompanyRepository companyRepository;
    
    private final OperationsRepository operationsRepository;

    @Transactional
    public Counterparty createCounterparty(Long userId, CreateCounterpartyInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));
        
        CounterpartyGroup counterpartyGroup = null;
        if(input.getCounterpartyGroupId() != null){
            counterpartyGroup = counterpartyGroupRepository.findById(input.getCounterpartyGroupId())
                .orElseThrow(() -> new IllegalArgumentException("CounterpartyGroup not found for the given id: " + input.getCounterpartyGroupId()));
            if (!counterpartyGroup.getCompany().getId().equals(input.getCompanyId())) {
                throw new UnauthorizedAccessException("Requested CounterpartyGroup does not belong to the provided company.");
            }
        }
        
        Counterparty counterparty = new Counterparty();
        counterparty.setCounterpartyGroup(counterpartyGroup);
        counterparty.setTitle(input.getTitle());
        counterparty.setFullName(input.getFullName());
        counterparty.setEmail(input.getEmail());
        counterparty.setPhoneNum(input.getPhoneNum());
        counterparty.setDescription(input.getDescription());
        counterparty.setCompany(company);
        counterparty.setDebt(BigDecimal.ZERO);

        company.addCounterparty(counterparty);
        return counterpartyRepository.save(counterparty);
    }

    @Transactional(readOnly = true)
    public List<Counterparty> findCounterparties(Long userId, Long companyId, List<Long> groupIds, String title, String fullName, String email, String phoneNum, String description) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        if (groupIds != null && !groupIds.isEmpty()) {
            List<CounterpartyGroup> groups = counterpartyGroupRepository.findByIdInAndCompanyId(groupIds, companyId);
            if (groups.size() != groupIds.size()) {
                throw new IllegalArgumentException("One or more CounterpartyGroups do not belong to the specified company.");
            }
        }

        Specification<Counterparty> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Mandatory filter for companyId
            predicates.add(cb.equal(root.get("company").get("id"), companyId));
            
            // Optional filters
            if (groupIds != null && !groupIds.isEmpty()) {
                predicates.add(root.get("counterpartyGroup").get("id").in(groupIds));
            }
            if (title != null) predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            if (fullName != null) predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%"));
            if (email != null) predicates.add(cb.equal(root.get("email"), email));
            if (phoneNum != null) predicates.add(cb.equal(root.get("phoneNum"), phoneNum));
            if (description != null) predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return counterpartyRepository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public Counterparty getCounterpartyByIdAndCompanyId(Long userId, Long companyId, Long id){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Counterparty counterparty = counterpartyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + id));
        if (!counterparty.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested Counterparty does not belong to the provided company.");
        }

        return counterparty;
    }

    @Transactional
    public Counterparty updateCounterparty(Long userId, UpdateCounterpartyInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Counterparty counterparty = counterpartyRepository.findById(input.getId())
            .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + input.getId()));
        if (!counterparty.getCompany().getId().equals(input.getCompanyId())) {
            throw new UnauthorizedAccessException("Requested Counterparty does not belong to the provided company.");
        }
        CounterpartyGroup counterpartyGroup = null;
        if(input.getCounterpartyGroupId() != null){
            counterpartyGroup = counterpartyGroupRepository.findById(input.getCounterpartyGroupId())
                .orElseThrow(() -> new IllegalArgumentException("CounterpartyGroup not found for the given id: " + input.getCounterpartyGroupId()));
            if (!counterpartyGroup.getCompany().getId().equals(input.getCompanyId())) {
                throw new UnauthorizedAccessException("Requested CounterpartyGroup does not belong to the provided company.");
            }
        }

        counterparty.setCounterpartyGroup(counterpartyGroup);
        counterparty.setTitle(input.getTitle());
        counterparty.setFullName(input.getFullName());
        counterparty.setEmail(input.getEmail());
        counterparty.setPhoneNum(input.getPhoneNum());
        counterparty.setDescription(input.getDescription());
        return counterpartyRepository.save(counterparty);
    }

    @Transactional
    public void deleteCounterparty(Long userId, Long companyId, Long id){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Counterparty counterparty = counterpartyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + id));
        if (!counterparty.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested Counterparty does not belong to the provided company.");
        }
        List<Operation> operations = operationsRepository.findByCounterpartyId(id);
        if(!operations.isEmpty()){
            throw new IllegalStateException("Cannot delete Counterparty as it has associated operations.");
        }

        counterpartyRepository.deleteById(id);
    }
}
