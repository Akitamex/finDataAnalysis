package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.LegalEntity;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.LegalEntityService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LegalEntityQueryResolver implements GraphQLQueryResolver{
    private final LegalEntityService legalEntityService;

    public List<LegalEntity> legalEntities(Long companyId, Long userId, String name, String fullName, String IIN, String COR, String MSRN, String legalAddress, String phoneNum, Boolean VAT){
        try{
            return legalEntityService.findLegalEntities(userId, companyId, name, fullName, IIN, COR, MSRN, legalAddress, phoneNum, VAT);
        }
        catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public LegalEntity legalEntityById(Long id, Long userId, Long companyId){
        try{
            return legalEntityService.getLegalEntityByIdAndCompanyId(userId, companyId, id);
        }
        catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }
    
}
