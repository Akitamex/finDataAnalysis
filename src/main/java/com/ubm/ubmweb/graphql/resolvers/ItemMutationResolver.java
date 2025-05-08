package com.ubm.ubmweb.graphql.resolvers;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Item;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateItemInput;
import com.ubm.ubmweb.graphql.dto.UpdateItemInput;
import com.ubm.ubmweb.services.ItemService;

@Component
@RequiredArgsConstructor
public class ItemMutationResolver implements GraphQLMutationResolver{
    private final ItemService itemService;

    public Item createItem(CreateItemInput input, Long userId){
        try {
            Item item = itemService.createItem(input, userId);
            return item;
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
    public Item updateItem(UpdateItemInput input, Long userId){
        try{
            Item item = itemService.updateItem(input, userId);
            return item;
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
    public Boolean deleteItem(Long id, Long userId, Long companyId) {
        try {
            itemService.deleteItem(id, userId, companyId);
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
