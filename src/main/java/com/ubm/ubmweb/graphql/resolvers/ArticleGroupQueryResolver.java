package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.ArticleGroup;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.ArticleGroupService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleGroupQueryResolver implements GraphQLQueryResolver{
    private final ArticleGroupService articleGroupService;

    public List<ArticleGroup> articleGroups(Long companyId, Long userId, String name, List<String> type){
        try{
            return articleGroupService.findArticleGroups(companyId, userId,  name, type);
        } catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
                return null;
        }
    }

    public ArticleGroup articleGroupById(Long id, Long userId, Long companyId){
        try{
            return articleGroupService.getArticleGroupByIdAndCompanyId(userId, id, companyId);
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
