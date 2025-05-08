package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.helper.JwtUtil;
import com.ubm.ubmweb.model.CostAnalysis;
import com.ubm.ubmweb.service.CostAnalysisService;

import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cost-analysis")
@RequiredArgsConstructor
public class CostAnalysisController {
    
    private final CostAnalysisService costAnalysisService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<CostAnalysis> getCostAnalysis(
            @RequestParam UUID companyId,
            ServletRequest servletRequest,
            @RequestParam String timeframe, // format:"dd.MM.yyyy-dd.MM.yyyy"  for example "01.01.2024-31.12.2024"
            @RequestParam String grouping,  //"day", "week", "month", "quarter"
            @RequestParam(required = false) List<UUID> bankAccountIds,
            @RequestParam(required = false) List<UUID> projectIds,
            @RequestParam(required = false) List<UUID> articleIds) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        CostAnalysis analysis = costAnalysisService.costAnalysis(companyId, userId, timeframe, grouping, 
                bankAccountIds != null ? bankAccountIds : List.of(),
                projectIds != null ? projectIds : List.of(),
                articleIds != null ? articleIds : List.of());
        
        return ResponseEntity.ok(analysis);
    }
}