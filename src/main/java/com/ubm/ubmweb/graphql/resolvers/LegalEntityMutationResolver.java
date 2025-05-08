package com.ubm.ubmweb.graphql.resolvers;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.LegalEntity;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateLegalEntityInput;
import com.ubm.ubmweb.graphql.dto.UpdateLegalEntityInput;
import com.ubm.ubmweb.services.LegalEntityService;

@Component
@RequiredArgsConstructor
public class LegalEntityMutationResolver implements GraphQLMutationResolver{
    private final LegalEntityService legalEntityService;
    
    public LegalEntity createLegalEntity(CreateLegalEntityInput input, Long userId){
        try{
            LegalEntity legalEntity = legalEntityService.createLegalEntity(userId, input);
            return legalEntity;
        }
        catch (Exception e) {
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }
    public LegalEntity updateLegalEntity(UpdateLegalEntityInput input, Long userId){
        try{
            LegalEntity legalEntity = legalEntityService.updateLegalEntity(userId, input);
            return legalEntity;
        } catch (Exception e) {
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }
    public Boolean deleteLegalEntity(Long id, Long userId, Long companyId) {
        try {
            legalEntityService.deleteLegalEntity(userId, companyId, id);
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
