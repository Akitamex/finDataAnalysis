package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Obligation;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.DateRangeInput;
import com.ubm.ubmweb.services.ObligationService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ObligationQueryResolver implements GraphQLQueryResolver{
    private final ObligationService obligationService;

    public List<Obligation> obligations(Long userId, Long companyId, DateRangeInput dateRange, List<String> types, List<Long> counterpartyIds, List<Long> legalEntityIds, List<Long> projectIds, String description){
        try{
            return obligationService.findObligations(userId, companyId, dateRange, types, counterpartyIds, legalEntityIds, projectIds, description);
        }
        catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            if(e instanceof IllegalStateException)
                throw new IllegalStateException(e.getMessage());
            return null;
        }
    }

    public Obligation obligationById(Long id, Long userId, Long companyId){
        try{
            return obligationService.getObligationById(userId, id, companyId);
        } catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }        
    }
    
}
