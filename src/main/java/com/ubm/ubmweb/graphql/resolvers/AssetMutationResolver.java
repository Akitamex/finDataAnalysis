package com.ubm.ubmweb.graphql.resolvers;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Asset;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateAssetInput;
import com.ubm.ubmweb.graphql.dto.UpdateAssetInput;
import com.ubm.ubmweb.services.AssetService;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AssetMutationResolver implements GraphQLMutationResolver{
    private final AssetService assetService;


    public Asset createAsset(CreateAssetInput input, Long userId){
        return assetService.createAsset(input, userId);
    }

    
    public Asset updateAsset(Long userId, UpdateAssetInput input){
        return assetService.updateAsset(userId, input);
    }

    public Boolean deleteAssetById(Long userId, Long companyId, Long id){
        try{
            assetService.deleteAssetById(userId, companyId, id);
            return true;
        }
        catch (Exception e){
            if (e instanceof IllegalStateException)
                throw new IllegalStateException(e.getMessage());
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return false;
        }
    }
}
