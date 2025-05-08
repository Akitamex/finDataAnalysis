package com.ubm.ubmweb.service;                //Товарные запасы

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateItemHistoryInput;
import com.ubm.ubmweb.graphql.dto.CreateItemInput;
// import com.ubm.ubmweb.graphql.dto.DateRangeInput;
import com.ubm.ubmweb.graphql.dto.TimeHelper;
import com.ubm.ubmweb.graphql.dto.UpdateItemInput;
import com.ubm.ubmweb.model.Company;
import com.ubm.ubmweb.model.Item;
import com.ubm.ubmweb.model.ItemHistory;
import com.ubm.ubmweb.model.LegalEntity;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.ItemHistoryRepository;
import com.ubm.ubmweb.repository.ItemRepository;
import com.ubm.ubmweb.repository.LegalEntityRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
// import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final CompanyRepository companyRepository;
    private final ItemHistoryRepository itemHistoryRepository;
    private final ItemHistoryService itemHistoryService;
    private final LegalEntityRepository legalEntityRepository;

    // public List<Item> getAllItems() {
    //     return itemRepository.findAll();
    // }

    public Optional<Item> getItemById(UUID id) {
        return itemRepository.findById(id);
    }

    @Transactional
    public Item createItem(CreateItemInput input, UUID userId) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));

        Item item = new Item();
        if(input.getType().equals("service")){
            input.setSetInitial(false);
            input.setUnit(null);
        }
        else if(input.getType().equals("product")){
            item.setUnit(input.getUnit());
            item.setQuantity(BigDecimal.ZERO);
            item.setTotalPrice(BigDecimal.ZERO);
        }
        else{
            throw new IllegalArgumentException("item type can only be 'product' or 'service'");
        }
        item.setCompany(company);
        item.setType(input.getType());
        item.setName(input.getName());
        item.setVendorCode(input.getVendorCode());
        item.setDescription(input.getDescription());
        if(input.getSetDefault() != null && input.getSetDefault()){
            item.setDefaultSellingPrice(input.getDefaultSellingPrice());
            item.setDspCurrency(input.getDspCurrency());
        }
        else{
            item.setDspCurrency("KZT");
            item.setDefaultSellingPrice(BigDecimal.ZERO);
        }
        if(input.getSetInitial() != null && input.getSetInitial()){
            Item savedItem = itemRepository.save(item);
            CreateItemHistoryInput firstShipment = new CreateItemHistoryInput();
            if(input.getInitialQuantity() != null && input.getDate() != null && input.getInitialPrice() != null && input.getInitialCurrency() != null && input.getLegalEntityId() != null){
                // LegalEntity legalEntity = null;
                // legalEntity = legalEntityRepository.findById(input.getLegalEntityId())
                //     .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getLegalEntityId()));
                // if (!legalEntity.getCompany().getId().equals(company.getId())) {
                //     throw new UnauthorizedAccessException("Requested LegalEntity does not belong to the provided company.");
                // }
                firstShipment.setLegalEntityId(input.getLegalEntityId());
                firstShipment.setDate(input.getDate());
                firstShipment.setQuantity(input.getInitialQuantity());
                firstShipment.setPrice(input.getInitialPrice());
                firstShipment.setCurrency(input.getInitialCurrency());
                firstShipment.setItemId(savedItem.getId());
                firstShipment.setCompanyId(company.getId());
                firstShipment.setInitialBalance(true);
                ItemHistory temp = itemHistoryService.createItemHistory(userId, firstShipment);
                savedItem.setFirstShipmentId(temp.getId());
                savedItem.setQuantity(input.getInitialQuantity());
                savedItem.setTotalPrice(temp.getTotal());
                // TimeHelper date = new TimeHelper();
                // date.setDateString(input.getDate());
                // savedItem.setEarliestShipment(date.strToDateISO(date.getDateString()));
                return itemRepository.save(savedItem);
            }
            else{
                throw new IllegalArgumentException("Initial shipment was uninitialazied: some mandatory arguements are null");
            }
        }
        else{
            return itemRepository.save(item);
        }
    }

    @Transactional
    public Item updateItem(UpdateItemInput itemDetails, UUID userId) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(itemDetails.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        companyRepository.findById(itemDetails.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + itemDetails.getCompanyId()));

        Item item = itemRepository.findById(itemDetails.getId())
        .orElseThrow(() -> new IllegalArgumentException("Item not found for the given id: " + itemDetails.getId()));
        
        item.setName(itemDetails.getName());
        item.setDescription(itemDetails.getDescription());
        item.setVendorCode(itemDetails.getVendorCode());
        item.setDefaultSellingPrice(itemDetails.getDefaultSellingPrice());
        item.setDspCurrency(itemDetails.getDspCurrency());
        if(itemDetails.getDefaultSellingPrice() == null){
            item.setDefaultSellingPrice(BigDecimal.ZERO);
        }
        if(itemDetails.getDspCurrency() == null){
            item.setDspCurrency("KZT");
        }
        if(item.getType().equals("product")){
            item.setUnit(itemDetails.getUnit());
        }
        if(!itemDetails.getSetInitial()){
            if(item.getFirstShipmentId() != null){
                itemHistoryService.deleteById(userId, itemDetails.getCompanyId(), item.getFirstShipmentId());
            }
            return itemRepository.save(item);
        }
        else if(item.getFirstShipmentId() != null && item.getType().equals("product")){
            if(itemDetails.getInitialQuantity() != null && itemDetails.getDate() != null && itemDetails.getInitialPrice() != null && itemDetails.getInitialCurrency() != null && itemDetails.getLegalEntityId() != null){
                ItemHistory currentShipment = itemHistoryRepository.findById(item.getFirstShipmentId())
                        .orElseThrow(() -> new IllegalArgumentException("InitialShipment not found for the given id: " + item.getFirstShipmentId()));
                LocalDate dateTemp = LocalDate.parse(itemDetails.getDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                TimeHelper date = new TimeHelper();
                if(item.getEarliestShipment() == null){
                    LegalEntity legalEntity = legalEntityRepository.findById(itemDetails.getLegalEntityId())
                    .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for id:" + itemDetails.getLegalEntityId()));

                    Company company = companyRepository.findById(legalEntity.getCompany().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + legalEntity.getCompany().getId()));

                    boolean associated = userCompanyRelationshipRepository.existsById(new UserCompanyId(company.getId(), userId));
                    if (!associated){
                        throw new UnauthorizedAccessException("User does not have access to this legalEntity.");
                    }
                    item.setQuantity(item.getQuantity().subtract(currentShipment.getQuantity().subtract(itemDetails.getInitialQuantity())));
                    currentShipment.setQuantity(itemDetails.getInitialQuantity());
                    currentShipment.setDate(date.strToDateISO(dateTemp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
                    currentShipment.setPrice(itemDetails.getInitialPrice());
                    currentShipment.setCurrency(itemDetails.getInitialCurrency());
                    currentShipment.setLegalEntity(legalEntity);
                    currentShipment.setInitialBalance(true);
                    itemHistoryRepository.save(currentShipment);
                }
                else{
                    if(!item.getEarliestShipment().isAfter(date.strToDateISO(itemDetails.getDate()))){
                        throw new IllegalArgumentException("Input date can not be after the earliest shipping");
                    }
                    else{
                        LegalEntity legalEntity = legalEntityRepository.findById(itemDetails.getLegalEntityId())
                        .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for id:" + itemDetails.getLegalEntityId()));

                        Company company = companyRepository.findById(legalEntity.getCompany().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + legalEntity.getCompany().getId()));

                        boolean associated = userCompanyRelationshipRepository.existsById(new UserCompanyId(company.getId(), userId));
                        if (!associated){
                            throw new UnauthorizedAccessException("User does not have access to this legalEntity.");
                        }
                        item.setQuantity(item.getQuantity().subtract(currentShipment.getQuantity().subtract(itemDetails.getInitialQuantity())));
                        currentShipment.setQuantity(itemDetails.getInitialQuantity());
                        currentShipment.setDate(date.strToDateISO(dateTemp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
                        currentShipment.setPrice(itemDetails.getInitialPrice());
                        currentShipment.setCurrency(itemDetails.getInitialCurrency());
                        currentShipment.setLegalEntity(legalEntity);
                        currentShipment.setInitialBalance(true);
                        itemHistoryRepository.save(currentShipment);
                    }
                }
            }
            else{
                throw new IllegalArgumentException("Item Update failed, missing non-nullable fields");
            }
        }
        else if(item.getFirstShipmentId() == null && item.getType().equals("product")){
            if(itemDetails.getInitialQuantity() != null && itemDetails.getDate() != null && itemDetails.getInitialPrice() != null && itemDetails.getInitialCurrency() != null && itemDetails.getLegalEntityId() != null){
                ItemHistory currentShipment = new ItemHistory();
                LocalDate dateTemp = LocalDate.parse(itemDetails.getDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                TimeHelper date = new TimeHelper();
                if(item.getEarliestShipment() == null){
                    LegalEntity legalEntity = legalEntityRepository.findById(itemDetails.getLegalEntityId())
                    .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for id:" + itemDetails.getLegalEntityId()));

                    Company company = companyRepository.findById(legalEntity.getCompany().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + legalEntity.getCompany().getId()));

                    boolean associated = userCompanyRelationshipRepository.existsById(new UserCompanyId(company.getId(), userId));
                    if (!associated){
                        throw new UnauthorizedAccessException("User does not have access to this legalEntity.");
                    }

                    currentShipment.setCompany(company);
                    currentShipment.setItem(item);
                    currentShipment.setQuantity(itemDetails.getInitialQuantity());
                    currentShipment.setDate(date.strToDateISO(dateTemp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
                    currentShipment.setPrice(itemDetails.getInitialPrice());
                    currentShipment.setTotal(currentShipment.getPrice().multiply(currentShipment.getQuantity()));
                    currentShipment.setCurrency(itemDetails.getInitialCurrency());
                    currentShipment.setLegalEntity(legalEntity);
                    currentShipment.setInitialBalance(true);
                    itemHistoryRepository.save(currentShipment);
                    item.setQuantity(item.getQuantity().add(itemDetails.getInitialQuantity()));
                }
                else{
                    if(!item.getEarliestShipment().isAfter(date.strToDateISO(itemDetails.getDate()))){
                        throw new IllegalArgumentException("Input date can not be after the earliest shipping");
                    }
                    else{
                        LegalEntity legalEntity = legalEntityRepository.findById(itemDetails.getLegalEntityId())
                        .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for id:" + itemDetails.getLegalEntityId()));

                        Company company = companyRepository.findById(legalEntity.getCompany().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + legalEntity.getCompany().getId()));

                        boolean associated = userCompanyRelationshipRepository.existsById(new UserCompanyId(company.getId(), userId));
                        if (!associated){
                            throw new UnauthorizedAccessException("User does not have access to this legalEntity.");
                        }

                        currentShipment.setQuantity(itemDetails.getInitialQuantity());
                        currentShipment.setDate(date.strToDateISO(dateTemp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
                        currentShipment.setPrice(itemDetails.getInitialPrice());
                        currentShipment.setCurrency(itemDetails.getInitialCurrency());
                        currentShipment.setLegalEntity(legalEntity);
                        currentShipment.setInitialBalance(true);
                        itemHistoryRepository.save(currentShipment);
                        item.setQuantity(item.getQuantity().add(itemDetails.getInitialQuantity()));
                    }
                }
            }
            else{
                throw new IllegalArgumentException("Item Update failed, missing non-nullable fields");
            }
        }
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(UUID id, UUID userId, UUID companyId) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id " + id));
        List<ItemHistory> itemHistories = itemHistoryRepository.findByItem(item);
        if (!itemHistories.isEmpty()) {
            if (itemHistories.size() == 1 && isInitialBalanceEntry(itemHistories.get(0))) {
                itemHistoryRepository.delete(itemHistories.get(0));
            } else {
                throw new IllegalStateException("Cannot delete item with existing item history entries");
            }
        }


        itemRepository.delete(item);
    }

    private boolean isInitialBalanceEntry(ItemHistory itemHistory) {
        return itemHistory.isInitialBalance();
    }

    @Transactional(readOnly = true)
    public List<Item> findItems(UUID userId, UUID companyId, String name, List<String> types, String description) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        return itemRepository.findItems(companyId, name, types, description);
    }

    @Transactional(readOnly = true)
    public Item getItemById(UUID userId, UUID id, UUID companyId) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Item not found for the given id: " + id));
        if (!item.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested Item does not belong to the provided company.");
        }
        return item;
    }
}
