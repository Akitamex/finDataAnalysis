package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.model.CounterpartyGroup;
import com.ubm.ubmweb.service.CounterpartyGroupService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/counterparty-groups")
@RequiredArgsConstructor
public class CounterpartyGroupController {

    private final CounterpartyGroupService counterpartyGroupService;

    @PostMapping
    public ResponseEntity<CounterpartyGroup> createCounterpartyGroup(
            @RequestParam @NotNull UUID userId,
            @RequestParam @NotNull UUID companyId,
            @RequestParam @NotBlank String name) {
        return ResponseEntity.ok(counterpartyGroupService.createCounterpartyGroup(userId, companyId, name));
    }

    @GetMapping
    public ResponseEntity<List<CounterpartyGroup>> findCounterpartyGroups(
            @RequestParam @NotNull UUID userId,
            @RequestParam @NotNull UUID companyId,
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(counterpartyGroupService.findCounterpartyGroups(userId, companyId, name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CounterpartyGroup> getCounterpartyGroupById(
            @RequestParam @NotNull UUID userId,
            @RequestParam @NotNull UUID companyId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(counterpartyGroupService.getCounterpartyGroupByIdAndCompanyId(userId, companyId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CounterpartyGroup> updateCounterpartyGroup(
            @RequestParam @NotNull UUID userId,
            @RequestParam @NotNull UUID companyId,
            @PathVariable UUID id,
            @RequestParam @NotBlank String name) {
        return ResponseEntity.ok(counterpartyGroupService.updateCounterpartyGroup(userId, companyId, id, name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCounterpartyGroup(
            @RequestParam @NotNull UUID userId,
            @RequestParam @NotNull UUID companyId,
            @PathVariable UUID id) {
        counterpartyGroupService.deleteCounterpartyGroup(userId, companyId, id);
        return ResponseEntity.noContent().build();
    }
}