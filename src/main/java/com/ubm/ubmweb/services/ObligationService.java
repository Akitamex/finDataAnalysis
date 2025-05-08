package com.ubm.ubmweb.services;


import com.ubm.ubmweb.entities.Project;
import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.Company;
import com.ubm.ubmweb.entities.Counterparty;
import com.ubm.ubmweb.entities.Item;
import com.ubm.ubmweb.entities.ItemHistory;
import com.ubm.ubmweb.entities.LegalEntity;
import com.ubm.ubmweb.entities.Obligation;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.CounterpartyRepository;
import com.ubm.ubmweb.repository.ItemHistoryRepository;
import com.ubm.ubmweb.repository.ItemRepository;
import com.ubm.ubmweb.repository.LegalEntityRepository;
import com.ubm.ubmweb.repository.ObligationRepository;
import com.ubm.ubmweb.repository.ProjectRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import lombok.RequiredArgsConstructor;

import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ubm.ubmweb.graphql.dto.*;

@Service
@RequiredArgsConstructor
public class ObligationService {

    private final ObligationRepository obligationRepository;
    
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final CompanyRepository companyRepository;

    private final ProjectRepository projectRepository;
    private final CounterpartyRepository counterpartyRepository;

    private final LegalEntityRepository legalEntityRepository;

    private final ItemRepository itemRepository;

    private final ItemHistoryService itemHistoryService;

    private final ItemHistoryRepository itemHistoryRepository;

    
    @Transactional
    public Obligation createObligation(Long userId, CreateObligationInput input) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Obligation obligation = new Obligation();
        obligation.setCurrency(input.getCurrency());
        obligation.setType(input.getType());
        obligation.setDate(LocalDate.parse(input.getDate())); // Assuming input.getDate() is a string in ISO_LOCAL_DATE format
        obligation.setDescription(input.getDescription());

        Company company = companyRepository.findById(input.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));
        obligation.setCompany(company);

        
        Counterparty counterparty = counterpartyRepository.findById(input.getCounterpartyId())
                .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + input.getCounterpartyId()));
        obligation.setCounterparty(counterparty);    

        if (input.getLegalEntityId() != null) {
            LegalEntity legalEntity = legalEntityRepository.findById(input.getLegalEntityId())
                    .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getLegalEntityId()));
            obligation.setLegalEntity(legalEntity);
        }

        if (input.getProjectId() != null) {
            Project project = projectRepository.findById(input.getProjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found for the given id: " + input.getProjectId()));
            obligation.setProject(project);
        }

        List<ItemHistory> itemHistories = new ArrayList<>();
        if (input.getItemHistories() != null && !input.getItemHistories().isEmpty()) {

            

            for (CreateItemHistoryInput itemHistoryInput : input.getItemHistories()) {
                Item item = itemRepository.findById(itemHistoryInput.getItemId())
                        .orElseThrow(() -> new IllegalArgumentException("Item not found for the given id: " + itemHistoryInput.getItemId()));

                itemHistoryService.validateItemQuantity(item, itemHistoryInput.getQuantity(), LocalDate.parse(itemHistoryInput.getDate()));

                ItemHistory itemHistory = new ItemHistory();
                itemHistory.setItem(item);
                itemHistory.setCompany(obligation.getCompany());
                itemHistory.setIncoming(input.getType().equals("RECEIVED"));
                itemHistory.setDate(LocalDate.parse(itemHistoryInput.getDate()));
                itemHistory.setQuantity(itemHistoryInput.getQuantity());
                itemHistory.setCurrency(input.getCurrency());
                itemHistory.setPrice(itemHistoryInput.getPrice());
                itemHistory.setTotal(itemHistoryInput.getPrice().multiply(itemHistoryInput.getQuantity()));
                itemHistory.setCounterparty(obligation.getCounterparty());
                itemHistory.setLegalEntity(obligation.getLegalEntity());
                itemHistory.setObligation(obligation);

                itemHistoryService.updateItemBalanceAndQuantity(item, itemHistory.getQuantity());

                itemHistories.add(itemHistory);
            }
            obligation.setBalance(calculateObligationBalance(itemHistories));
        } else {
            obligation.setBalance(input.getBalance());
        }

        if(input.getType().equals("IN")){
            counterparty.setDebt(counterparty.getDebt().subtract(obligation.getBalance()));
        }
        else if(input.getType().equals("OUT")){
            counterparty.setDebt(counterparty.getDebt().add(obligation.getBalance()));
        }
        counterpartyRepository.save(counterparty);
        obligationRepository.save(obligation);
        itemHistoryRepository.saveAll(itemHistories);
        return obligation;
    }

    private final BigDecimal calculateObligationBalance(List<ItemHistory> itemHistories) {
        BigDecimal totalBalance = BigDecimal.ZERO;
        for (ItemHistory itemHistory : itemHistories) {
            totalBalance = totalBalance.add(itemHistory.getTotal());
        }
        return totalBalance;
    }

    @Transactional(readOnly = true)
    public List<Obligation> findObligations(Long userId, Long companyId, DateRangeInput dateRange, List<String> types,
                                            List<Long> counterpartyIds, List<Long> legalEntityIds, List<Long> projectIds, String description) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        return obligationRepository.findObligations(companyId, dateRange, types, counterpartyIds, legalEntityIds, projectIds, description);
    }

    @Transactional(readOnly = true)
    public Obligation getObligationById(Long userId, Long id, Long companyId) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Obligation obligation = obligationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Obligation not found for the given id: " + id));
        if (!obligation.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested Obligation does not belong to the provided company.");
        }
        return obligation;
    }

    @Transactional
    public Obligation updateObligation(Long userId, UpdateObligationInput input) {
        Long obligationId = input.getId();
        Obligation obligation = obligationRepository.findById(obligationId)
                .orElseThrow(() -> new IllegalArgumentException("Obligation not found for the given id: " + obligationId));

        Long companyId = obligation.getCompany().getId();
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        // Update fields
        obligation.setBalance(input.getBalance());
        obligation.setCurrency(input.getCurrency());
        obligation.setDescription(input.getDescription());

        if (input.getCounterpartyId() != null) {
            Counterparty counterparty = counterpartyRepository.findById(input.getCounterpartyId())
                    .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + input.getCounterpartyId()));
            obligation.setCounterparty(counterparty);
        }

        if (input.getLegalEntityId() != null) {
            LegalEntity legalEntity = legalEntityRepository.findById(input.getLegalEntityId())
                    .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getLegalEntityId()));
            obligation.setLegalEntity(legalEntity);
        }

        if (input.getProjectId() != null) {
            Project project = projectRepository.findById(input.getProjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found for the given id: " + input.getProjectId()));
            obligation.setProject(project);
        }

        // Remove existing ItemHistory entries associated with this Obligation
        List<ItemHistory> existingItemHistories = itemHistoryRepository.findByObligationId(obligationId);
        for (ItemHistory itemHistory : existingItemHistories) {
            itemHistoryService.updateItemBalanceAndQuantity(itemHistory.getItem(), itemHistory.getQuantity().negate());
        }
        itemHistoryRepository.deleteAll(existingItemHistories);

        // Add new ItemHistory entries
        List<ItemHistory> newItemHistories = new ArrayList<>();
        if (input.getItemHistories() != null && !input.getItemHistories().isEmpty()) {
            for (CreateItemHistoryInput itemHistoryInput : input.getItemHistories()) {
                Item item = itemRepository.findById(itemHistoryInput.getItemId())
                        .orElseThrow(() -> new IllegalArgumentException("Item not found for the given id: " + itemHistoryInput.getItemId()));

                itemHistoryService.validateItemQuantity(item, itemHistoryInput.getQuantity(), LocalDate.parse(itemHistoryInput.getDate()));

                ItemHistory itemHistory = new ItemHistory();
                itemHistory.setItem(item);
                itemHistory.setCompany(obligation.getCompany());
                itemHistory.setIncoming(obligation.getType().equals("RECEIVED"));
                itemHistory.setDate(LocalDate.parse(itemHistoryInput.getDate()));
                itemHistory.setQuantity(itemHistoryInput.getQuantity());
                itemHistory.setCurrency(input.getCurrency());
                itemHistory.setPrice(itemHistoryInput.getPrice());
                itemHistory.setTotal(itemHistoryInput.getPrice().multiply(itemHistoryInput.getQuantity()));
                itemHistory.setCounterparty(obligation.getCounterparty());
                itemHistory.setLegalEntity(obligation.getLegalEntity());
                itemHistory.setObligation(obligation);

                itemHistoryService.updateItemBalanceAndQuantity(item, itemHistory.getQuantity());

                newItemHistories.add(itemHistory);
            }
        }
        obligation.setBalance(calculateObligationBalance(newItemHistories));
        obligationRepository.save(obligation);
        itemHistoryRepository.saveAll(newItemHistories);
        return obligation;
    }

    @Transactional
    public void deleteObligation(Long userId, Long companyId, Long id) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        Obligation obligation = obligationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obligation not found with id " + id));

        List<ItemHistory> itemHistories = itemHistoryRepository.findByObligationId(id);
        for (ItemHistory itemHistory : itemHistories) {
            itemHistoryService.updateItemBalanceAndQuantity(itemHistory.getItem(), itemHistory.getQuantity().negate());
        }

        Counterparty counterparty = obligation.getCounterparty();
        counterparty.removeObligation(obligation);
        if(obligation.getType().equals("IN")){
            counterparty.setDebt(counterparty.getDebt().add(obligation.getBalance()));
        }
        else if(obligation.getType().equals("OUT")){
            counterparty.setDebt(counterparty.getDebt().subtract(obligation.getBalance()));
        }

        counterpartyRepository.save(counterparty);
        itemHistoryRepository.deleteAll(itemHistories);
        obligationRepository.delete(obligation);
    }

    
}
