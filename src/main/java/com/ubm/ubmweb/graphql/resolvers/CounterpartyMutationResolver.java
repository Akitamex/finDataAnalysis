package com.ubm.ubmweb.graphql.resolvers;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Counterparty;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateCounterpartyInput;
import com.ubm.ubmweb.graphql.dto.UpdateCounterpartyInput;
import com.ubm.ubmweb.services.CounterpartyService;

@Component
@RequiredArgsConstructor
public class CounterpartyMutationResolver implements GraphQLMutationResolver{
    private final CounterpartyService counterpartyService;

    public Counterparty createCounterparty(CreateCounterpartyInput input, Long userId){
        try{
            Counterparty counterparty = counterpartyService.createCounterparty(userId, input);
            return counterparty;
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
    public Counterparty updateCounterparty(UpdateCounterpartyInput input, Long userId){
        try{
            Counterparty counterparty = counterpartyService.updateCounterparty(userId, input);
            return counterparty;
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
    public Boolean deleteCounterparty(Long id, Long userId, Long companyId) {
        try {
            counterpartyService.deleteCounterparty(userId, companyId, id);
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
