package com.ubm.ubmweb.graphql.resolvers;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.FixedAsset;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.FixedAssetService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FixedAssetQueryResolver implements GraphQLQueryResolver {

    private final FixedAssetService fixedAssetService;

    public List<FixedAsset> fixedAssets(Long userId, Long companyId, Boolean amortise, String name, String purchaseDate,
                                        List<Long> counterpartyIds, List<Long> legalEntityIds, Boolean includeVat) {
        LocalDate parsedPurchaseDate = purchaseDate != null ? LocalDate.parse(purchaseDate) : null;
        try{
            return fixedAssetService.findFixedAssets(userId, companyId, amortise, name, parsedPurchaseDate, counterpartyIds, legalEntityIds, includeVat);
        }catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            if(e instanceof IllegalStateException)
                throw new IllegalStateException(e.getMessage());
            return null;
        }
    }

    public FixedAsset fixedAssetById(Long id, Long userId, Long companyId) {
        return fixedAssetService.getFixedAssetById(userId, id, companyId);
    }
}