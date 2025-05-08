package com.ubm.ubmweb.graphql.resolvers;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Obligation;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateObligationInput;
import com.ubm.ubmweb.graphql.dto.UpdateObligationInput;
import com.ubm.ubmweb.services.ObligationService;

@Component
@RequiredArgsConstructor
public class ObligationMutationResolver implements GraphQLMutationResolver{
    private final ObligationService obligationService;
    
    public Obligation createObligation(CreateObligationInput input, Long userId){
        try {
            Obligation obligation = obligationService.createObligation(userId, input);
            return obligation;
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
    public Obligation updateObligation(UpdateObligationInput input, Long userId){
        try{
            Obligation obligation = obligationService.updateObligation(userId, input);
            return obligation;
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
    public Boolean deleteObligation(Long id, Long userId, Long companyId) {
        try {
            obligationService.deleteObligation(userId, companyId, id);
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
