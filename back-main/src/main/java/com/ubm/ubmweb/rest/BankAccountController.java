package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.graphql.dto.*;
import com.ubm.ubmweb.helper.JwtUtil;
import com.ubm.ubmweb.model.*;
import com.ubm.ubmweb.service.BankAccountService;

import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final JwtUtil jwtUtil;

    @GetMapping("/{id}")
    public ResponseEntity<BankAccount> getBankAccount(ServletRequest servletRequest, @RequestParam UUID companyId, @PathVariable UUID id) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        return ResponseEntity.ok(bankAccountService.getBankAccountByIdAndCompanyId(userId, companyId, id));
    }

    @PostMapping("/bank")
    public ResponseEntity<BankBankAccount> createBankBankAccount(ServletRequest servletRequest, @RequestBody CreateBankBankAccountInput input) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        return ResponseEntity.ok(bankAccountService.createBankBankAccount(userId, input));
    }

    @PostMapping("/cash")
    public ResponseEntity<CashBankAccount> createCashBankAccount(ServletRequest servletRequest, @RequestBody CreateCashBankAccountInput input) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        return ResponseEntity.ok(bankAccountService.createCashBankAccount(userId, input));
    }

    @PostMapping("/fund")
    public ResponseEntity<FundBankAccount> createFundBankAccount(ServletRequest servletRequest, @RequestBody CreateFundBankAccountInput input) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        return ResponseEntity.ok(bankAccountService.createFundBankAccount(userId, input));
    }

    @GetMapping
    public ResponseEntity<List<BankAccount>> findBankAccounts(
            ServletRequest servletRequest, @RequestParam UUID companyId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String bank,
            @RequestParam(required = false) String BIC,
            @RequestParam(required = false) String correspondentAccount,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) List<UUID> legalEntityIds,
            @RequestParam(required = false) List<UUID> ids) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        return ResponseEntity.ok(bankAccountService.findBankAccounts(userId, companyId, name, currency, type, bank, BIC, correspondentAccount, accountNumber,
        legalEntityIds != null ? legalEntityIds : List.of(), 
        ids != null ? ids : List.of()));
    }

    @PutMapping("/bank")
    public ResponseEntity<BankBankAccount> updateBankBankAccount(ServletRequest servletRequest, @RequestBody UpdateBankBankAccountInput input) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        return ResponseEntity.ok(bankAccountService.updateBankBankAccount(userId, input));
    }

    @PutMapping("/cash")
    public ResponseEntity<CashBankAccount> updateCashBankAccount(ServletRequest servletRequest, @RequestBody UpdateCashBankAccountInput input) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        return ResponseEntity.ok(bankAccountService.updateCashBankAccount(userId, input));
    }

    @PutMapping("/fund")
    public ResponseEntity<FundBankAccount> updateFundBankAccount(ServletRequest servletRequest, @RequestBody UpdateFundBankAccountInput input) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        return ResponseEntity.ok(bankAccountService.updateFundBankAccount(userId, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankAccount(@PathVariable UUID id, ServletRequest servletRequest, @RequestParam UUID companyId) {
        UUID userId = jwtUtil.userIdFromRequest(servletRequest);
        bankAccountService.deleteBankAccount(id, userId, companyId);
        return ResponseEntity.noContent().build();
    }
}