package com.ubm.ubmweb.service;


import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateAssetInput;
import com.ubm.ubmweb.graphql.dto.CreateFixedAssetInput;
import com.ubm.ubmweb.graphql.dto.UpdateFixedAssetInput;
import com.ubm.ubmweb.model.Asset;
import com.ubm.ubmweb.model.Company;
import com.ubm.ubmweb.model.Counterparty;
import com.ubm.ubmweb.model.FixedAsset;
import com.ubm.ubmweb.model.LegalEntity;
import com.ubm.ubmweb.repository.AssetRepository;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.CounterpartyRepository;
import com.ubm.ubmweb.repository.FixedAssetRepository;
import com.ubm.ubmweb.repository.LegalEntityRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class FixedAssetService {

    
    private final FixedAssetRepository fixedAssetRepository;

    
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;

    
    private final CompanyRepository companyRepository;

    
    private final LegalEntityRepository legalEntityRepository;

    
    private final CounterpartyRepository counterpartyRepository;

    
    private final AssetRepository assetRepository;

    
    private final AssetService assetService;
    

    @Transactional
    public FixedAsset createFixedAsset(CreateFixedAssetInput input, UUID userId) {
        if (input.getQuantity() < 1) {
            throw new IllegalArgumentException("Fixed Asset Quantity should be >= 1");
        }

        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));

        Counterparty counterparty = counterpartyRepository.findById(input.getCounterpartyId())
            .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + input.getCounterpartyId()));
        if (!counterparty.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("Requested Counterparty does not belong to the provided company.");
        }

        LegalEntity legalEntity = legalEntityRepository.findById(input.getLegalEntityId())
            .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getLegalEntityId()));
        if (!legalEntity.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("Requested LegalEntity does not belong to the provided company.");
        }

        Asset asset;
        FixedAsset fixedAsset = new FixedAsset();

        fixedAsset.setAmortise(input.getAmortise());
        fixedAsset.setQuantity(input.getQuantity());
        fixedAsset.setUnitPrice(input.getUnitPrice());
        BigDecimal totalCost = input.getUnitPrice().multiply(BigDecimal.valueOf(input.getQuantity()));
        fixedAsset.setTotalCost(totalCost);
        fixedAsset.setCurrency(input.getCurrency());
        LocalDate date = LocalDate.parse(input.getPurchaseDate());
        fixedAsset.setPurchaseDate(date);
        fixedAsset.setServiceLifeMonths(input.getServiceLifeMonths());
        fixedAsset.setCounterparty(counterparty);
        fixedAsset.setLegalEntity(legalEntity);
        fixedAsset.setIncludeVat(input.getIncludeVat() != null ? true : false);
        fixedAsset.setVat(input.getVat() != null && input.getIncludeVat() ? input.getVat() : null);
        fixedAsset.setCompany(company);
        BigDecimal remainingCost;
        if(input.getAmortise()){
            remainingCost = calculateRemainingCost(totalCost, input.getUnitPrice(), date, input.getServiceLifeMonths());
        }
        else{
            remainingCost = BigDecimal.ZERO;
        }
        // fixedAsset.setRemainingCost(input.getAmortise() ?
        //     calculateRemainingCost(fixedAsset.getTotalCost(), input.getUnitPrice(), date, input.getServiceLifeMonths()) :
        //     fixedAsset.getTotalCost());
        fixedAsset.setRemainingCost(remainingCost);

        if (input.getAssetId() == null) {
            if(input.getName() == null){
                throw new IllegalArgumentException("Name can not be null if Asset not specified");
            }
            CreateAssetInput assetInput = new CreateAssetInput();
            assetInput.setName(input.getName());
            assetInput.setQuantity(input.getQuantity());
            assetInput.setWholeCost(input.getUnitPrice().multiply(BigDecimal.valueOf(input.getQuantity())));
            fixedAsset.setName(input.getName());
            
            assetInput.setRemainingCost(remainingCost);
            assetInput.setCompanyId(input.getCompanyId());
            Asset newAsset = assetService.createAsset(assetInput, userId);
            fixedAsset.setAsset(newAsset);
        } else {
            asset = assetRepository.findById(input.getAssetId())
                .orElseThrow(() -> new IllegalArgumentException("Asset not found for id: " + input.getAssetId()));
            if(asset.getCompany().getId() != company.getId()){
                throw new UnauthorizedAccessException("requested Asset does not belon to the provided company");
            }
            asset.setQuantity(asset.getQuantity() + input.getQuantity());
            asset.setWholeCost(asset.getWholeCost().add(input.getUnitPrice().multiply(BigDecimal.valueOf(input.getQuantity()))));
            fixedAsset.setName(asset.getName());
            asset.setRemainingCost(asset.getRemainingCost().add(remainingCost));
            fixedAsset.setAsset(asset);
        }


        // assetRepository.save(asset);

        // fixedAsset.setAsset(asset);
        // asset.addFixedAsset(fixedAsset);

        return fixedAssetRepository.save(fixedAsset);
    }

    @Transactional
    public FixedAsset updateFixedAsset(UUID userId, UpdateFixedAssetInput input) {
        if (input.getQuantity() < 1) {
            throw new IllegalArgumentException("Fixed Asset Quantity should be >= 1");
        }

        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));

        Counterparty counterparty = counterpartyRepository.findById(input.getCounterpartyId())
            .orElseThrow(() -> new IllegalArgumentException("Counterparty not found for the given id: " + input.getCounterpartyId()));
        if (!counterparty.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("Requested Counterparty does not belong to the provided company.");
        }

        LegalEntity legalEntity = legalEntityRepository.findById(input.getLegalEntityId())
            .orElseThrow(() -> new IllegalArgumentException("LegalEntity not found for the given id: " + input.getLegalEntityId()));
        if (!legalEntity.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedAccessException("Requested LegalEntity does not belong to the provided company.");
        }

        FixedAsset fixedAsset = fixedAssetRepository.findById(input.getId())
            .orElseThrow(() -> new RuntimeException("Fixed asset not found with id " + input.getId()));
        UUID assetId = fixedAsset.getAsset().getId();
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new IllegalArgumentException("Asset not found for id: " + assetId));

        asset.setQuantity(asset.getQuantity() - fixedAsset.getQuantity() + input.getQuantity());
        asset.setWholeCost(asset.getWholeCost().subtract(fixedAsset.getTotalCost()).add(input.getUnitPrice().multiply(BigDecimal.valueOf(input.getQuantity()))));
        asset.setRemainingCost(asset.getRemainingCost().subtract(fixedAsset.getRemainingCost()));

        fixedAsset.setQuantity(input.getQuantity());
        fixedAsset.setUnitPrice(input.getUnitPrice());
        LocalDate date = LocalDate.parse(input.getPurchaseDate());
        fixedAsset.setPurchaseDate(date);
        fixedAsset.setServiceLifeMonths(input.getServiceLifeMonths());
        fixedAsset.setCounterparty(counterparty);
        fixedAsset.setLegalEntity(legalEntity);
        fixedAsset.setTotalCost(input.getUnitPrice().multiply(BigDecimal.valueOf(input.getQuantity())));
        fixedAsset.setCurrency(input.getCurrency());
        fixedAsset.setIncludeVat(input.getIncludeVat());
        fixedAsset.setVat(input.getVat());

        fixedAsset.setRemainingCost(fixedAsset.getAmortise() ?
            calculateRemainingCost(fixedAsset.getTotalCost(), input.getUnitPrice(), date, input.getServiceLifeMonths()) :
            fixedAsset.getTotalCost());
        asset.setRemainingCost(asset.getRemainingCost().add(fixedAsset.getRemainingCost()));
    

        assetRepository.save(asset);
        return fixedAssetRepository.save(fixedAsset);
    }

    @Transactional
    public void deleteById(UUID id, UUID userId, UUID companyId) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to modify this company.");
        }
        FixedAsset fixedAsset = fixedAssetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fixed asset not found with id " + id));
        if(fixedAsset.getCompany().getId() != companyId){
            throw new UnauthorizedAccessException("the company of the fixedAsset does not belong to the provided company");
        }
        UUID assetId = fixedAsset.getAsset().getId();
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Asset not found with id " + assetId));
        asset.setQuantity(asset.getQuantity() - fixedAsset.getQuantity());
        asset.setWholeCost(asset.getWholeCost().subtract(fixedAsset.getTotalCost()));
        asset.setRemainingCost(asset.getRemainingCost().subtract(fixedAsset.getRemainingCost()));
        assetRepository.save(asset);
        fixedAssetRepository.delete(fixedAsset);
    }



    public BigDecimal calculateRemainingCost(BigDecimal totalCost, BigDecimal unitPrice, LocalDate purchaseDate, int serviceLifeMonths) {
        LocalDate today = LocalDate.now();
        Long monthsElapsed = ChronoUnit.MONTHS.between(purchaseDate, today);
    
        if (monthsElapsed >= serviceLifeMonths) {
            return BigDecimal.ZERO;
        } else {
            BigDecimal monthlyDepreciation = totalCost.divide(BigDecimal.valueOf(serviceLifeMonths), RoundingMode.HALF_UP);
            BigDecimal depreciationToDate = monthlyDepreciation.multiply(BigDecimal.valueOf(monthsElapsed));
            return totalCost.subtract(depreciationToDate);
        }
    }
    

    @Transactional(readOnly = true)
    public List<FixedAsset> findFixedAssets(UUID userId, UUID companyId, Boolean amortise, String name, LocalDate purchaseDate,
                                            List<UUID> counterpartyIds, List<UUID> legalEntityIds, Boolean includeVat) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        return fixedAssetRepository.findFixedAssets(companyId, amortise, name, purchaseDate, counterpartyIds, legalEntityIds, includeVat);
    }

    @Transactional(readOnly = true)
    public FixedAsset getFixedAssetById(UUID userId, UUID id, UUID companyId) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        FixedAsset fixedAsset = fixedAssetRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("FixedAsset not found for the given id: " + id));
        if (!fixedAsset.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested FixedAsset does not belong to the provided company.");
        }
        return fixedAsset;
    }

    @Transactional
    public void updateRemainingCosts() {
        List<FixedAsset> fixedAssets = fixedAssetRepository.findAll();
        LocalDate today = LocalDate.now();
        Map<UUID, BigDecimal> assetRemainingCosts = new HashMap<>();

        for (FixedAsset fixedAsset : fixedAssets) {
            long monthsElapsed = ChronoUnit.MONTHS.between(fixedAsset.getPurchaseDate(), today);
            BigDecimal newRemainingCost;
            
            if (monthsElapsed > fixedAsset.getServiceLifeMonths()) {
                newRemainingCost = BigDecimal.ZERO;
            } else {
                BigDecimal monthlyDepreciation = fixedAsset.getTotalCost()
                        .divide(BigDecimal.valueOf(fixedAsset.getServiceLifeMonths()), RoundingMode.HALF_UP);
                BigDecimal depreciationToDate = monthlyDepreciation.multiply(BigDecimal.valueOf(monthsElapsed));
                newRemainingCost = fixedAsset.getTotalCost().subtract(depreciationToDate);
            }

            fixedAsset.setRemainingCost(newRemainingCost);
            
            UUID assetId = fixedAsset.getAsset().getId();
            assetRemainingCosts.put(assetId, assetRemainingCosts.getOrDefault(assetId, BigDecimal.ZERO).add(newRemainingCost));
        }

        fixedAssetRepository.saveAll(fixedAssets);

        for (Map.Entry<UUID, BigDecimal> entry : assetRemainingCosts.entrySet()) {
            Asset asset = assetRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Asset not found for id: " + entry.getKey()));
            asset.setRemainingCost(entry.getValue());
            assetRepository.save(asset);
        }
    }


}
