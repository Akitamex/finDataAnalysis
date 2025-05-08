package com.ubm.ubmweb.service;        //История отгрузок товарных запасов

import com.ubm.ubmweb.compositeKey.UserCompanyId;
// import com.ubm.ubmweb.entities.Obligation;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateItemHistoryInput;
import com.ubm.ubmweb.graphql.dto.DateRangeInput;
import com.ubm.ubmweb.graphql.dto.TimeHelper;
import com.ubm.ubmweb.graphql.dto.UpdateItemHistoryInput;
import com.ubm.ubmweb.model.Company;
import com.ubm.ubmweb.model.Counterparty;
import com.ubm.ubmweb.model.Item;
import com.ubm.ubmweb.model.ItemHistory;
import com.ubm.ubmweb.model.LegalEntity;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.CounterpartyRepository;
import com.ubm.ubmweb.repository.ItemHistoryRepository;
import com.ubm.ubmweb.repository.ItemRepository;
import com.ubm.ubmweb.repository.LegalEntityRepository;
// import com.ubm.ubmweb.repository.ObligationRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ItemHistoryService {

    private final ItemHistoryRepository itemHistoryRepository;
    private final ItemRepository itemRepository;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final CompanyRepository companyRepository;
    private final CounterpartyRepository counterpartyRepository;
    private final LegalEntityRepository legalEntityRepository;

    // private ObligationRepository obligationRepository;

    // public List<Item> getAllItems() {
    //     return itemRepository.findAll();
    // }

    // public Optional<Item> getItemById(Long id) {
    //     return itemRepository.findById(id);
    // }

    @Transactional
    public ItemHistory createItemHistory(UUID userId, CreateItemHistoryInput input) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));

        Item item = itemRepository.findById(input.getItemId())
            .orElseThrow(() -> new IllegalArgumentException("Item not found for the given id: " + input.getItemId()));
        if(!item.getCompany().getId().equals(input.getCompanyId())){
            throw new IllegalArgumentException("Item with id:" + item.getId() + "does not belong to the company with id:" + input.getCompanyId());
        }

        Counterparty counterparty = null;
        if (input.getCounterpartyId() != null) {
            counterparty = counterpartyRepository.findById(input.getCounterpartyId())
                .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + input.getCounterpartyId()));
            if (!counterparty.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested Counterparty does not belong to the provided company.");
            }
        }
        LegalEntity legalEntity = null;
        if (input.getLegalEntityId() != null) {
            legalEntity = legalEntityRepository.findById(input.getLegalEntityId())
                .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getCounterpartyId()));
            if (!legalEntity.getCompany().getId().equals(company.getId())) {
                throw new UnauthorizedAccessException("Requested LegalEntity does not belong to the provided company.");
            }
        }
        else{
            throw new IllegalArgumentException("LegalEntity can not be null");
        }

        

        ItemHistory itemHistory = new ItemHistory();
        itemHistory.setCompany(company);
        itemHistory.setItem(item);
        TimeHelper date = new TimeHelper();
        itemHistory.setDate(date.strToDateISO(input.getDate()));
        BigDecimal amount = input.getQuantity();
        BigDecimal quantity = input.isIncoming() ? amount.negate() : amount; // Negate the amount if it's an incoming shipment
        if (!canCreateShipment(item, quantity, date.strToDateISO(input.getDate()))) {
            throw new IllegalStateException("Insufficient quantity for the shipment");
        }

        if(item.getEarliestShipment() == null){
            item.setEarliestShipment(itemHistory.getDate());
        }
        else{
            if(item.getEarliestShipment().isAfter(itemHistory.getDate())){
                item.setEarliestShipment(itemHistory.getDate());
            }
        }
        
        itemHistory.setQuantity(quantity);
        itemHistory.setCurrency(input.getCurrency());
        itemHistory.setPrice(input.getPrice());
        itemHistory.setTotal(input.getPrice().multiply(input.getQuantity()));
        itemHistory.setCounterparty(counterparty);
        itemHistory.setLegalEntity(legalEntity);

        item.setQuantity(item.getQuantity().add(input.getQuantity()));
        if(item.getQuantity().compareTo(BigDecimal.ZERO) == -1){
            throw new IllegalArgumentException("The quantity added would deplete the amount available");
        }
        item.setTotalPrice(item.getTotalPrice().add(itemHistory.getTotal()));
        itemRepository.save(item);

        // Obligation obligation = new Obligation();
        // obligation.setBalance(itemHistory.getTotal());
        // obligation.setCompany(company);
        // obligation.setCounterparty(counterparty);
        // obligation.setCurrency(input.getCurrency());
        // obligation.setDate(date.strToDateISO(input.getDate()));

        return itemHistoryRepository.save(itemHistory);
    }

    @Transactional
    public ItemHistory updateItemHistory(UUID userId, UpdateItemHistoryInput input) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        ItemHistory itemHistory = itemHistoryRepository.findById(input.getId())
            .orElseThrow(() -> new IllegalArgumentException("Item's history entry not found for id:" + input.getId()));

        Item item = itemHistory.getItem();

        BigDecimal originalQuantity = itemHistory.getQuantity();
        BigDecimal newQuantity = input.getQuantity();
        BigDecimal quantityChange = newQuantity.subtract(originalQuantity);

        if (!canCreateShipment(item, quantityChange, itemHistory.getDate())) {
            throw new IllegalStateException("Insufficient quantity for the shipment");
        }

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

        itemHistory.setCurrency(input.getCurrency());
        itemHistory.setCounterparty(counterparty);
        itemHistory.setLegalEntity(legalEntity);
        itemHistory.setPrice(input.getPrice());
        itemHistory.setQuantity(input.getQuantity());
        itemHistory.setTotal(input.getPrice().multiply(input.getQuantity()));

        // Update the item's quantity and total price based on the change in item history
        item.setQuantity(item.getQuantity().add(quantityChange));
        if (item.getQuantity().compareTo(BigDecimal.ZERO) == -1) {
            throw new IllegalArgumentException("The quantity update would deplete the amount available");
        }
        item.setTotalPrice(item.getTotalPrice().add(itemHistory.getTotal().subtract(itemHistory.getTotal())));

        itemRepository.save(item);
        return itemHistoryRepository.save(itemHistory);
    }

    @Transactional
    public void deleteById(UUID userId, UUID companyId, UUID id) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to modify this company.");
        }
        ItemHistory itemHistory = itemHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id " + id));
        
        Item item = itemRepository.findById(itemHistory.getItem().getId())
        .orElseThrow(() -> new IllegalArgumentException("IF YOU SEE THIS, SOMETHING IS VERY WRONG"));

        item.setQuantity(item.getQuantity().subtract(itemHistory.getQuantity()));
        item.setTotalPrice(item.getTotalPrice().subtract(itemHistory.getTotal()));
        
        if(itemHistory.getCompany().getId().equals(companyId)){
            itemHistoryRepository.deleteById(id);
        }
    }

    @Transactional(readOnly = true)
    public List<ItemHistory> getItemHistoryByItem(Item item) {
        return itemHistoryRepository.findByItem(item);
    }

    public BigDecimal getBalanceAtDate(Item item, LocalDate date) {
        List<ItemHistory> histories = itemHistoryRepository.findByItemAndDateLessThanEqualOrderByDateAsc(item, date);
        BigDecimal balance = BigDecimal.ZERO;
        for (ItemHistory history : histories) {
            balance = balance.add(history.getQuantity());
        }
        return balance;
    }

    public boolean canCreateShipment(Item item, BigDecimal quantity, LocalDate date) {
        BigDecimal balance = getBalanceAtDate(item, date);
        return balance.compareTo(quantity) >= 0;
    }

    @Transactional(readOnly = true)
    public List<ItemHistory> findItemHistories(UUID userId, UUID companyId, UUID itemId, DateRangeInput dateRange,
                                               List<UUID> counterpartyIds, List<UUID> legalEntityIds, Boolean isIncoming) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        return itemHistoryRepository.findItemHistories(companyId, itemId, dateRange, counterpartyIds, legalEntityIds, isIncoming);
    }

    @Transactional(readOnly = true)
    public ItemHistory getItemHistoryById(UUID userId, UUID id, UUID companyId) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        ItemHistory itemHistory = itemHistoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ItemHistory not found for the given id: " + id));
        if (!itemHistory.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested ItemHistory does not belong to the provided company.");
        }
        return itemHistory;
    }

    @Transactional(readOnly = true)
    public void validateItemQuantity(Item item, BigDecimal quantityChange, LocalDate date) {
        List<ItemHistory> histories = itemHistoryRepository.findByItemAndDateLessThanEqualOrderByDateAsc(item, date);
        BigDecimal balance = BigDecimal.ZERO;
        for (ItemHistory history : histories) {
            balance = balance.add(history.getQuantity());
        }
        if (balance.add(quantityChange).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient quantity for the shipment");
        }
    }

    @Transactional
    public void updateItemBalanceAndQuantity(Item item, BigDecimal quantityChange) {
        item.setQuantity(item.getQuantity().add(quantityChange));
        if (item.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The quantity update would deplete the amount available");
        }
        item.setTotalPrice(item.getTotalPrice().multiply(item.getQuantity()));
        itemRepository.save(item);
    }
}