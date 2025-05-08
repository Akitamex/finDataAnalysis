package com.ubm.ubmweb.rest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ubm.ubmweb.model.ProfitsLosses;
import com.ubm.ubmweb.service.ProfitsLossesService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profits-losses")
@RequiredArgsConstructor
public class ProfitsLossesController {

    private final ProfitsLossesService profitsLossesService;

    @GetMapping()
    public ResponseEntity<ProfitsLosses> getProfitsLosses(
            @RequestParam UUID companyId,
            @RequestParam UUID userId,
            @RequestParam String type,          //"article", "project"
            @RequestParam String timeframe,     // format:"dd.MM.yyyy-dd.MM.yyyy"  for example "01.01.2024-31.12.2024"
            @RequestParam(required = false) List<UUID> bankAccountIds,
            @RequestParam(required = false) List<UUID> projectIds) {
        
        ProfitsLosses result = profitsLossesService.profitsLosses(companyId, userId, type, timeframe, bankAccountIds, projectIds);
        return ResponseEntity.ok(result);
    }
}
