package com.ubm.ubmweb.graphql.resolvers;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.FixedAsset;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateFixedAssetInput;
import com.ubm.ubmweb.graphql.dto.UpdateFixedAssetInput;
import com.ubm.ubmweb.services.FixedAssetService;

@Component
@RequiredArgsConstructor
public class FixedAssetMutationResolver implements GraphQLMutationResolver{
    
    private final FixedAssetService fixedAssetService;
    
    public FixedAsset createFixedAsset(CreateFixedAssetInput input, Long userId){
        try {
            FixedAsset fixedAsset = fixedAssetService.createFixedAsset(input, userId);
            return fixedAsset;
        }
        catch (Exception e){
            if(e instanceof UnauthorizedAccessException){
                throw new UnauthorizedAccessException(e.getMessage());
            }
            if(e instanceof IllegalArgumentException){
                throw new IllegalArgumentException(e.getMessage());
            }
            return null;
        }
    }
    public FixedAsset updateFixedAsset(UpdateFixedAssetInput input, Long userId){
        try{
            FixedAsset fixedAsset = fixedAssetService.updateFixedAsset(userId, input);
            return fixedAsset;
        }
        catch (Exception e){
            if(e instanceof UnauthorizedAccessException){
                throw new UnauthorizedAccessException(e.getMessage());
            }
            if(e instanceof IllegalArgumentException){
                throw new IllegalArgumentException(e.getMessage());
            }
            return null;
        }
    }
    public Boolean deleteFixedAsset(Long id, Long userId, Long companyId) {
        try {
            fixedAssetService.deleteById(id, userId, companyId);
            return true;
        } catch (Exception e) {
            // Log the exception, handle it as needed
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
