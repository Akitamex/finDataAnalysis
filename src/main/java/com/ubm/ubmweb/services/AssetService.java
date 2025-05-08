package com.ubm.ubmweb.services;

import java.math.BigDecimal;
import java.util.List;
 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.Asset;
import com.ubm.ubmweb.entities.Company;
import com.ubm.ubmweb.entities.FixedAsset;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateAssetInput;
import com.ubm.ubmweb.graphql.dto.UpdateAssetInput;
import com.ubm.ubmweb.repository.AssetRepository;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.FixedAssetRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetService {
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;

    private final CompanyRepository companyRepository;

    private final AssetRepository assetRepository;

    private final FixedAssetRepository fixedAssetRepository;

    @Transactional
    public Asset createAsset(CreateAssetInput input, Long userId){

        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the provided Asset"));
        
        Asset asset = new Asset();
        if(input.getName() == null){
            throw new IllegalArgumentException("Name can not be null");
        }
        asset.setName(input.getName());
        asset.setCompany(company);
        asset.setQuantity(input.getQuantity() != null ? input.getQuantity() : 0l);
        asset.setRemainingCost(input.getRemainingCost() != null ? input.getRemainingCost() : BigDecimal.ZERO);
        asset.setWholeCost(input.getWholeCost() != null ? input.getWholeCost() : BigDecimal.ZERO);

        return assetRepository.save(asset);
    }

    @Transactional
    public Asset updateAsset(Long userId, UpdateAssetInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Asset asset = assetRepository.findById(input.getId())
            .orElseThrow(() -> new IllegalArgumentException("Asset not found for id:" + input.getId()));
        
        Company company = companyRepository.findById(asset.getCompany().getId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the provided Asset"));
        isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(company.getId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this Asset.");
        }

        if(input.getName().equals(null)){
            throw new IllegalArgumentException("Name can not be null");
        }
        asset.setName(input.getName());
        List<FixedAsset> fixedAssets = fixedAssetRepository.findByAssetId(asset.getId());
        for (FixedAsset fixedAsset : fixedAssets) {
            fixedAsset.setName(input.getName());
        }
        return assetRepository.save(asset);
    }

    @Transactional
    public void deleteAssetById(Long userId, Long companyId, Long id) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id " + id));

        fixedAssetRepository.deleteByAssetId(id);
        assetRepository.delete(asset);
    }

    @Transactional(readOnly = true)
    public List<Asset> findAssets(Long userId, Long companyId, String name, Long quantity, BigDecimal remainingCost, BigDecimal wholeCost) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        return assetRepository.findAssets(companyId, name, quantity, remainingCost, wholeCost);
    }

    @Transactional(readOnly = true)
    public Asset getAssetById(Long userId, Long id, Long companyId) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Asset not found for the given id: " + id));
        if (!asset.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested Asset does not belong to the provided company.");
        }
        return asset;
    }
}