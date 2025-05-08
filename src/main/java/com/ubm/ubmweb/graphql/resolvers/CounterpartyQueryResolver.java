package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Counterparty;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.CounterpartyService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CounterpartyQueryResolver implements GraphQLQueryResolver{
    private final CounterpartyService counterpartyService;

    public List<Counterparty> counterparties(Long companyId, Long userId, List<Long> groupIds, String title, String fullName, String email, String phoneNum, String description){
        try{
            return counterpartyService.findCounterparties(userId, companyId, groupIds, title, fullName, email, phoneNum, description);
        }
        catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public Counterparty counterpartyById(Long id, Long userId, Long companyId){
        try{
            return counterpartyService.getCounterpartyByIdAndCompanyId(userId, companyId, id);
        } catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }        
    }
    
}
