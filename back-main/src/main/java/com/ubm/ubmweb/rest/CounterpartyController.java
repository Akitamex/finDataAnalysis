package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.graphql.dto.CreateCounterpartyInput;
import com.ubm.ubmweb.graphql.dto.UpdateCounterpartyInput;
import com.ubm.ubmweb.helper.JwtUtil;
import com.ubm.ubmweb.model.Counterparty;
import com.ubm.ubmweb.service.CounterpartyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/counterparties")
@RequiredArgsConstructor
public class CounterpartyController {

    private final CounterpartyService counterpartyService;
    private final JwtUtil jwtUtil;

    @PostMapping("/user/{userId}")
    public ResponseEntity<Counterparty> createCounterparty(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateCounterpartyInput input) {
        return ResponseEntity.ok(counterpartyService.createCounterparty(userId, input));
    }

    @GetMapping
    public ResponseEntity<List<Counterparty>> findCounterparties(
            @RequestParam UUID userId, @RequestParam UUID companyId,
            @RequestParam(required = false) List<UUID> groupIds,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNum,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(counterpartyService.findCounterparties(
                userId, companyId, groupIds != null ? groupIds : List.of(), title, fullName, email, phoneNum, description));
    }

    @GetMapping("/{id}/user/{userId}/company/{companyId}")
    public ResponseEntity<Counterparty> getCounterpartyById(
            @PathVariable UUID userId, @PathVariable UUID companyId, @PathVariable UUID id) {
        return ResponseEntity.ok(counterpartyService.getCounterpartyByIdAndCompanyId(userId, companyId, id));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<Counterparty> updateCounterparty(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateCounterpartyInput input) {
        return ResponseEntity.ok(counterpartyService.updateCounterparty(userId, input));
    }

    @DeleteMapping("/{id}/user/{userId}/company/{companyId}")
    public ResponseEntity<Void> deleteCounterparty(
            @PathVariable UUID userId, @PathVariable UUID companyId, @PathVariable UUID id) {
        counterpartyService.deleteCounterparty(userId, companyId, id);
        return ResponseEntity.noContent().build();
    }
}
