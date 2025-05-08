package com.ubm.ubmweb.graphql.resolvers;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.ArticleGroup;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.ArticleGroupService;


@Component
@RequiredArgsConstructor
public class ArticleGroupMutationResolver  implements GraphQLMutationResolver{
    private final ArticleGroupService articleGroupService;

    public ArticleGroup createArticleGroup(Long companyId, Long userId, String name, String type){
        try {
            ArticleGroup articleGroup = articleGroupService.createArticleGroup(userId, companyId, type, name);
            return articleGroup;
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
    public ArticleGroup updateArticleGroup(Long id, Long userId, Long companyId, String name){
        try {
            ArticleGroup articleGroup = articleGroupService.updateArticleGroup(userId, id, companyId, name);
            return articleGroup;
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
    public Boolean deleteArticleGroup(Long id, Long userId, Long companyId) {
        try {
            articleGroupService.deleteArticleGroup(userId, companyId, id);
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
