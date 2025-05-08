package com.ubm.ubmweb.graphql.resolvers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Asset;
import com.ubm.ubmweb.services.AssetService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AssetQueryResolver implements GraphQLQueryResolver{
    private final AssetService assetService;
    
    public List<Asset> assets(Long userId, Long companyId, String name, Long quantity, BigDecimal remainingCost, BigDecimal wholeCost) {
        return assetService.findAssets(userId, companyId, name, quantity, remainingCost, wholeCost);
    }

    public Asset assetById(Long id, Long userId, Long companyId) {
        return assetService.getAssetById(userId, id, companyId);
    }
}
