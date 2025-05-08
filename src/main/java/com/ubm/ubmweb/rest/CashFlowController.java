package com.ubm.ubmweb.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ubm.ubmweb.helper.JwtUtil;
import com.ubm.ubmweb.model.CashFlow;
import com.ubm.ubmweb.service.CashFlowService;

import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/cashflow")
@RequiredArgsConstructor
public class CashFlowController {
    private final CashFlowService cashFlowService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<CashFlow> cashFlow(@RequestParam UUID companyId,
                            ServletRequest servletRequest,
                            @RequestParam String type,  //"article", "activity", "bank", "counterparty", "project"
                            @RequestParam String timeframe, // format:"dd.MM.yyyy-dd.MM.yyyy"  for example "01.01.2024-31.12.2024"
                            @RequestParam String grouping, //"day", "week", "month", "quarter"
                            @RequestParam(required = false) List<UUID> bankAccountIds,
                            @RequestParam(required = false) List<UUID> projectIds
                            ) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        return ResponseEntity.ok(cashFlowService.cashFlow(companyId, userId, type, timeframe, grouping, 
        bankAccountIds != null ? bankAccountIds : List.of(), 
        projectIds != null ? projectIds : List.of()));
    }
}
