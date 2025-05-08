package com.ubm.ubmweb.graphql.resolvers;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.ItemHistory;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateItemHistoryInput;
import com.ubm.ubmweb.graphql.dto.UpdateItemHistoryInput;
import com.ubm.ubmweb.services.ItemHistoryService;

@Component
@RequiredArgsConstructor
public class ItemHistoryMutationResolver implements GraphQLMutationResolver{
    private final ItemHistoryService itemHistoryService;

    public ItemHistory createItemHistory(CreateItemHistoryInput input, Long userId){
        try {
            ItemHistory itemHistory = itemHistoryService.createItemHistory(userId, input);
            return itemHistory;
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
    public ItemHistory updateItemHistory(UpdateItemHistoryInput input, Long userId){
        try{
            ItemHistory itemHistory = itemHistoryService.updateItemHistory(userId, input);
            return itemHistory;
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
    public Boolean deleteItemHistory(Long id, Long userId, Long companyId) {
        try {
            itemHistoryService.deleteById(userId, companyId, id);
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
