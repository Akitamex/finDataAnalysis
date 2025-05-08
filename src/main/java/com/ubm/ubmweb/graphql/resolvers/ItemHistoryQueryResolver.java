package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;
import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.ItemHistory;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.DateRangeInput;
import com.ubm.ubmweb.services.ItemHistoryService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ItemHistoryQueryResolver implements GraphQLQueryResolver{
    private final ItemHistoryService itemHistoryService;
    
    public List<ItemHistory> itemHistories(Long userId, Long companyId, Long itemId, DateRangeInput dateRange, List<Long> counterpartyIds, List<Long> legalEntityIds, Boolean isIncoming){
        try{
            return itemHistoryService.findItemHistories(userId, companyId, itemId, dateRange, counterpartyIds, legalEntityIds, isIncoming);
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

    public ItemHistory itemHistoryById(Long id, Long userId, Long companyId){
        try{
            return itemHistoryService.getItemHistoryById(userId, id, companyId);
        } catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }        
    }
    
}
