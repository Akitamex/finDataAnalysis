package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.graphql.dto.CreateFixedAssetInput;
import com.ubm.ubmweb.graphql.dto.UpdateFixedAssetInput;
import com.ubm.ubmweb.model.FixedAsset;
import com.ubm.ubmweb.service.FixedAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fixed-assets")
@RequiredArgsConstructor
public class FixedAssetController {

    private final FixedAssetService fixedAssetService;

    @PostMapping
    public ResponseEntity<FixedAsset> createFixedAsset(@RequestBody CreateFixedAssetInput input, @RequestParam UUID userId) {
        FixedAsset fixedAsset = fixedAssetService.createFixedAsset(input, userId);
        return ResponseEntity.ok(fixedAsset);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FixedAsset> updateFixedAsset(@PathVariable UUID id, @RequestBody UpdateFixedAssetInput input, @RequestParam UUID userId) {
        FixedAsset updatedFixedAsset = fixedAssetService.updateFixedAsset(userId, input);
        return ResponseEntity.ok(updatedFixedAsset);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFixedAsset(@PathVariable UUID id, @RequestParam UUID userId, @RequestParam UUID companyId) {
        fixedAssetService.deleteById(id, userId, companyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FixedAsset>> getFixedAssets(@RequestParam UUID userId, @RequestParam UUID companyId,
                                                           @RequestParam(required = false) Boolean amortise,
                                                           @RequestParam(required = false) String name,
                                                           @RequestParam(required = false) LocalDate purchaseDate,
                                                           @RequestParam(required = false) List<UUID> counterpartyIds,
                                                           @RequestParam(required = false) List<UUID> legalEntityIds,
                                                           @RequestParam(required = false) Boolean includeVat) {
        List<FixedAsset> fixedAssets = fixedAssetService.findFixedAssets(userId, companyId, amortise, name, purchaseDate, counterpartyIds, legalEntityIds, includeVat);
        return ResponseEntity.ok(fixedAssets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FixedAsset> getFixedAssetById(@PathVariable UUID id, @RequestParam UUID userId, @RequestParam UUID companyId) {
        FixedAsset fixedAsset = fixedAssetService.getFixedAssetById(userId, id, companyId);
        return ResponseEntity.ok(fixedAsset);
    }
}
