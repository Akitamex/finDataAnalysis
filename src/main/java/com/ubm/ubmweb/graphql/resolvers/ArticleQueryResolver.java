package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Article;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.ArticleService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleQueryResolver implements GraphQLQueryResolver{
    private final ArticleService articleService;
    
    public List<Article> articles(Long companyId, Long userId, String name, List<Long> articleGroupIds, List<String> types, String description, Integer category){
        try{
            return articleService.findArticles(userId, companyId, name, articleGroupIds, types, description, category);
        } catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public Article articleById(Long id, Long userId, Long companyId){
        try{
            return articleService.getArticleByIdAndCompanyId(userId, id, companyId);
        } catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }
}
