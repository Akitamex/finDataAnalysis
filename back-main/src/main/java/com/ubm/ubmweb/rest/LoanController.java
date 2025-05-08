package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.graphql.dto.CreateLoanInput;
import com.ubm.ubmweb.graphql.dto.UpdateLoanInput;
import com.ubm.ubmweb.model.Loan;
// import com.ubm.ubmweb.model.LoanPayment;
import com.ubm.ubmweb.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestParam UUID userId, @RequestBody CreateLoanInput input) {
        Loan loan = loanService.createLoan(userId, input);
        return ResponseEntity.ok(loan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Loan> updateLoan(@RequestParam UUID userId, @RequestBody UpdateLoanInput input) {
        Loan loan = loanService.updateLoan(userId, input);
        return ResponseEntity.ok(loan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@RequestParam UUID userId, @RequestParam UUID companyId, @PathVariable UUID id) {
        loanService.deleteLoan(userId, companyId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Loan>> findLoans(@RequestParam UUID userId, @RequestParam UUID companyId, @RequestParam(required = false) String name) {
        List<Loan> loans = loanService.findLoans(userId, companyId, name);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> findLoanById(@RequestParam UUID userId, @RequestParam UUID companyId, @PathVariable UUID id) {
        Loan loan = loanService.findLoanById(userId, companyId, id);
        return ResponseEntity.ok(loan);
    }

    @PostMapping("/payments/apply")
    public ResponseEntity<Void> applyOperationAsPayment(@RequestParam UUID userId, @RequestParam UUID loanPaymentId, @RequestParam UUID operationId) {
        loanService.applyOperationAsPayment(userId, loanPaymentId, operationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payments/remove")
    public ResponseEntity<Void> removeOperationFromPayment(@RequestParam UUID userId, @RequestParam UUID operationId) {
        loanService.removeOperationFromPayment(userId, operationId);
        return ResponseEntity.ok().build();
    }
}
