package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.graphql.dto.*;
import com.ubm.ubmweb.model.Obligation;
import com.ubm.ubmweb.service.ObligationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/obligations")
@RequiredArgsConstructor
public class ObligationController {

    private final ObligationService obligationService;

    @PostMapping
    public ResponseEntity<Obligation> createObligation(@RequestParam UUID userId, @RequestBody CreateObligationInput input) {
        Obligation obligation = obligationService.createObligation(userId, input);
        return ResponseEntity.ok(obligation);
    }

    @GetMapping
    public ResponseEntity<List<Obligation>> getObligations(
            @RequestParam UUID userId,
            @RequestParam UUID companyId,
            @RequestParam(required = false) DateRangeInput dateRange,
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) List<UUID> counterpartyIds,
            @RequestParam(required = false) List<UUID> legalEntityIds,
            @RequestParam(required = false) List<UUID> projectIds,
            @RequestParam(required = false) String description) {
        List<Obligation> obligations = obligationService.findObligations(userId, companyId, dateRange, 
        types != null ? types : List.of(), 
        counterpartyIds != null ? counterpartyIds : List.of(), 
        legalEntityIds != null ? legalEntityIds : List.of(), 
        projectIds != null ? projectIds : List.of(), 
        description);
        return ResponseEntity.ok(obligations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Obligation> getObligationById(@RequestParam UUID userId, @PathVariable UUID id, @RequestParam UUID companyId) {
        Obligation obligation = obligationService.getObligationById(userId, id, companyId);
        return ResponseEntity.ok(obligation);
    }

    @PutMapping
    public ResponseEntity<Obligation> updateObligation(@RequestParam UUID userId, @RequestBody UpdateObligationInput input) {
        Obligation updatedObligation = obligationService.updateObligation(userId, input);
        return ResponseEntity.ok(updatedObligation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObligation(@RequestParam UUID userId, @RequestParam UUID companyId, @PathVariable UUID id) {
        obligationService.deleteObligation(userId, companyId, id);
        return ResponseEntity.noContent().build();
    }
}
