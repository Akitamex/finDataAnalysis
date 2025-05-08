package com.ubm.ubmweb.graphql.resolvers;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.CounterpartyGroup;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.CounterpartyGroupService;


@Component
@RequiredArgsConstructor
public class CounterpartyGroupMutationResolver implements GraphQLMutationResolver{
    private final CounterpartyGroupService counterpartyGroupService;

    public CounterpartyGroup createCounterpartyGroup(Long companyId, Long userId, String name){
        try{
            CounterpartyGroup counterpartyGroup = counterpartyGroupService.createCounterpartyGroup(userId, companyId, name);
            return counterpartyGroup;
        } catch (Exception e) {
            // Log the exception, handle it as needed
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
        
    }
    public CounterpartyGroup updateCounterpartyGroup(Long id, Long userId, Long companyId, String name){
        try{
            CounterpartyGroup counterpartyGroup = counterpartyGroupService.updateCounterpartyGroup(userId, companyId, id, name);
            return counterpartyGroup;
        }
        catch (Exception e) {
            // Log the exception, handle it as needed
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }
    public Boolean deleteCounterpartyGroup(Long id, Long userId, Long companyId) {
        try {
            counterpartyGroupService.deleteCounterpartyGroup(userId, companyId, id);
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
