package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.CounterpartyGroup;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.CounterpartyGroupService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CounterpartyGroupQueryResolver implements GraphQLQueryResolver{
    private final CounterpartyGroupService counterpartyGroupService;

    
    public List<CounterpartyGroup> counterpartyGroups(Long companyId, Long userId, String name){
        try{
            return counterpartyGroupService.findCounterpartyGroups(userId, companyId, name);
        }
        catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public CounterpartyGroup counterpartyGroupById(Long id, Long userId, Long companyId){
        try{
            return counterpartyGroupService.getCounterpartyGroupByIdAndCompanyId(userId, companyId, id);
        } catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }
}
