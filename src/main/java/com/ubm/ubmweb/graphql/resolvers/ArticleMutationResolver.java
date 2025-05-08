package com.ubm.ubmweb.graphql.resolvers;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Article;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateArticleInput;
import com.ubm.ubmweb.graphql.dto.UpdateArticleInput;
import com.ubm.ubmweb.services.ArticleService;


@Component
@RequiredArgsConstructor
public class ArticleMutationResolver implements GraphQLMutationResolver{
    private final ArticleService articleService;

    
    public Article createArticle(CreateArticleInput input, Long userId){
        try {
            Article article = articleService.createArticle(userId, input);
            return article;
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
    public Article updateArticle(UpdateArticleInput input, Long userId){
        try{
            Article article = articleService.updateArticle(userId, input);
            return article;
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
    public Boolean deleteArticle(Long id, Long userId, Long companyId) {
        try {
            articleService.deleteArticle(id, userId, companyId);
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
