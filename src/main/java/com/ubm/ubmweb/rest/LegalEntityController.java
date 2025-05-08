package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.graphql.dto.CreateLegalEntityInput;
import com.ubm.ubmweb.graphql.dto.UpdateLegalEntityInput;
import com.ubm.ubmweb.model.LegalEntity;
import com.ubm.ubmweb.service.LegalEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/legal-entities")
@RequiredArgsConstructor
public class LegalEntityController {

    private final LegalEntityService legalEntityService;

    @PostMapping
    public ResponseEntity<LegalEntity> createLegalEntity(@RequestHeader("userId") UUID userId,
                                                          @RequestBody CreateLegalEntityInput input) {
        return ResponseEntity.ok(legalEntityService.createLegalEntity(userId, input));
    }

    @GetMapping
    public ResponseEntity<List<LegalEntity>> getLegalEntities(@RequestHeader("userId") UUID userId,
                                                               @RequestParam UUID companyId,
                                                               @RequestParam(required = false) String name,
                                                               @RequestParam(required = false) String fullName,
                                                               @RequestParam(required = false) String IIN,
                                                               @RequestParam(required = false) String COR,
                                                               @RequestParam(required = false) String MSRN,
                                                               @RequestParam(required = false) String legalAddress,
                                                               @RequestParam(required = false) String phoneNum,
                                                               @RequestParam(required = false) Boolean VAT) {
        return ResponseEntity.ok(legalEntityService.findLegalEntities(userId, companyId, name, fullName, IIN, COR, MSRN, legalAddress, phoneNum, VAT));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalEntity> getLegalEntityById(@RequestHeader("userId") UUID userId,
                                                           @RequestParam UUID companyId,
                                                           @PathVariable UUID id) {
        return ResponseEntity.ok(legalEntityService.getLegalEntityByIdAndCompanyId(userId, companyId, id));
    }

    @PutMapping
    public ResponseEntity<LegalEntity> updateLegalEntity(@RequestHeader("userId") UUID userId,
                                                          @RequestBody UpdateLegalEntityInput input) {
        return ResponseEntity.ok(legalEntityService.updateLegalEntity(userId, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLegalEntity(@RequestHeader("userId") UUID userId,
                                                   @RequestParam UUID companyId,
                                                   @PathVariable UUID id) {
        legalEntityService.deleteLegalEntity(userId, companyId, id);
        return ResponseEntity.noContent().build();
    }
}