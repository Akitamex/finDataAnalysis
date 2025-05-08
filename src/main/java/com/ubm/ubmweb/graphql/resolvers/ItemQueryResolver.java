package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Item;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.ItemService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ItemQueryResolver implements GraphQLQueryResolver{
    private final ItemService itemService;

    public List<Item> items(Long userId, Long companyId, String name, List<String> type, String description){
        try{
            return itemService.findItems(userId, companyId, name, type, description);
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

    public Item itemById(Long id, Long userId, Long companyId){
        try{
            return itemService.getItemById(userId, id, companyId);
        } catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }        
    }
    
}
