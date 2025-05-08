package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.model.Asset;
import com.ubm.ubmweb.service.AssetService;

import jakarta.servlet.ServletRequest;

import com.ubm.ubmweb.graphql.dto.CreateAssetInput;
import com.ubm.ubmweb.graphql.dto.UpdateAssetInput;
import com.ubm.ubmweb.helper.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {
    
    private final AssetService assetService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<Asset> createAsset(ServletRequest servletRequest,
                                             @RequestBody CreateAssetInput input) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        Asset asset = assetService.createAsset(input, userId);
        return ResponseEntity.ok(asset);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(ServletRequest servletRequest,
                                             @RequestBody UpdateAssetInput input) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        Asset updatedAsset = assetService.updateAsset(userId, input);
        return ResponseEntity.ok(updatedAsset);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(ServletRequest servletRequest,
                                            @RequestParam UUID companyId,
                                            @PathVariable UUID id) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        assetService.deleteAssetById(userId, companyId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Asset>> findAssets(ServletRequest servletRequest,
                                                  @RequestParam UUID companyId,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) Long quantity,
                                                  @RequestParam(required = false) BigDecimal remainingCost,
                                                  @RequestParam(required = false) BigDecimal wholeCost) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        List<Asset> assets = assetService.findAssets(userId, companyId, name, quantity, remainingCost, wholeCost);
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(ServletRequest servletRequest,
                                              @PathVariable UUID id,
                                              @RequestParam UUID companyId) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        Asset asset = assetService.getAssetById(userId, id, companyId);
        return ResponseEntity.ok(asset);
    }
}
