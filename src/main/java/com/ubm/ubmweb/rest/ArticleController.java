package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.model.Article;
import com.ubm.ubmweb.service.ArticleService;

import jakarta.servlet.ServletRequest;

import com.ubm.ubmweb.graphql.dto.CreateArticleInput;
import com.ubm.ubmweb.graphql.dto.UpdateArticleInput;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ubm.ubmweb.helper.JwtUtil;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final JwtUtil jwtUtil;
    

    @PostMapping
    public ResponseEntity<Article> createArticle(ServletRequest servletRequest,
                                                 @RequestBody CreateArticleInput input) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        Article article = articleService.createArticle(userId, input);
        return ResponseEntity.ok(article);
    }

    @GetMapping
    public ResponseEntity<List<Article>> findArticles(ServletRequest servletRequest,
                                                       @RequestParam UUID companyId,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(required = false) List<UUID> articleGroupIds,
                                                       @RequestParam(required = false) List<String> types,
                                                       @RequestParam(required = false) String description,
                                                       @RequestParam(required = false) Integer category) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        List<Article> articles = articleService.findArticles(userId, companyId, name, 
        articleGroupIds != null ? articleGroupIds : List.of(), 
        types != null ? types : List.of(),
        description, 
        category);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleByIdAndCompanyId(ServletRequest servletRequest,
                                                               @PathVariable UUID id,
                                                               @RequestParam UUID companyId) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        Article article = articleService.getArticleByIdAndCompanyId(userId, id, companyId);
        return ResponseEntity.ok(article);
    }

    @PutMapping
    public ResponseEntity<Article> updateArticle(ServletRequest servletRequest,
                                                  @RequestBody UpdateArticleInput input) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        Article updatedArticle = articleService.updateArticle(userId, input);
        return ResponseEntity.ok(updatedArticle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID id,
                                               ServletRequest servletRequest,
                                               @RequestParam UUID companyId) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        articleService.deleteArticle(id, userId, companyId);
        return ResponseEntity.noContent().build();
    }
}
