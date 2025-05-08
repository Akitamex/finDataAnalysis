package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.helper.JwtUtil;
import com.ubm.ubmweb.model.ArticleGroup;
import com.ubm.ubmweb.service.ArticleGroupService;

import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/article-groups")
@RequiredArgsConstructor
public class ArticleGroupController {

    private final ArticleGroupService articleGroupService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ArticleGroup> createArticleGroup(ServletRequest servletRequest,
                                                            @RequestParam UUID companyId,
                                                            @RequestParam String type,
                                                            @RequestParam String name) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        ArticleGroup articleGroup = articleGroupService.createArticleGroup(userId, companyId, type, name);
        return ResponseEntity.ok(articleGroup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticleGroup(ServletRequest servletRequest,
                                                    @RequestParam UUID companyId,
                                                    @PathVariable UUID id) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        articleGroupService.deleteArticleGroup(userId, companyId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ArticleGroup>> findArticleGroups(@RequestParam UUID companyId,
                                                                 ServletRequest servletRequest,
                                                                 @RequestParam(required = false) String name,
                                                                 @RequestParam(required = false) List<String> type) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        List<ArticleGroup> articleGroups = articleGroupService.findArticleGroups(companyId, userId, name, 
        type != null ? type : List.of()
        );
        return ResponseEntity.ok(articleGroups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleGroup> getArticleGroupByIdAndCompanyId(ServletRequest servletRequest,
                                                                         @PathVariable UUID id,
                                                                         @RequestParam UUID companyId) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        ArticleGroup articleGroup = articleGroupService.getArticleGroupByIdAndCompanyId(userId, id, companyId);
        return ResponseEntity.ok(articleGroup);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleGroup> updateArticleGroup(ServletRequest servletRequest,
                                                            @PathVariable UUID id,
                                                            @RequestParam UUID companyId,
                                                            @RequestParam String name) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        ArticleGroup updatedArticleGroup = articleGroupService.updateArticleGroup(userId, id, companyId, name);
        return ResponseEntity.ok(updatedArticleGroup);
    }
}
