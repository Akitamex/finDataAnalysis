package com.ubm.ubmweb.rest;
import com.ubm.ubmweb.graphql.dto.CreateExpenseOperationInput;
import com.ubm.ubmweb.graphql.dto.CreateIncomeOperationInput;
import com.ubm.ubmweb.graphql.dto.CreateTransferOperationInput;
import com.ubm.ubmweb.graphql.dto.DateRangeInput;
import com.ubm.ubmweb.graphql.dto.UpdateExpenseOperationInput;
import com.ubm.ubmweb.graphql.dto.UpdateIncomeOperationInput;
import com.ubm.ubmweb.graphql.dto.UpdateTransferOperationInput;
import com.ubm.ubmweb.model.ExpenseOperation;
import com.ubm.ubmweb.model.IncomeOperation;
import com.ubm.ubmweb.model.Operation;
import com.ubm.ubmweb.model.TransferOperation;
import com.ubm.ubmweb.service.OperationService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
public class OperationController {
    private final OperationService operationService;

    @PostMapping("/income")
    public ResponseEntity<IncomeOperation> createIncomeOperation(
            @RequestParam UUID userId,
            @RequestBody CreateIncomeOperationInput input) {
        IncomeOperation operation = operationService.createIncomeOperation(userId, input);
        return ResponseEntity.status(HttpStatus.CREATED).body(operation);
    }

    @PostMapping("/expense")
    public ResponseEntity<ExpenseOperation> createExpenseOperation(
            @RequestParam UUID userId,
            @RequestBody CreateExpenseOperationInput input) {
        ExpenseOperation operation = operationService.createExpenseOperation(userId, input);
        return ResponseEntity.status(HttpStatus.CREATED).body(operation);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferOperation> createTransferOperation(
            @RequestParam UUID userId,
            @RequestBody CreateTransferOperationInput input) {
        TransferOperation operation = operationService.createTransferOperation(userId, input);
        return ResponseEntity.status(HttpStatus.CREATED).body(operation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Operation> getOperationByIdAndCompanyId(
            @RequestParam UUID userId,
            @PathVariable UUID id,
            @RequestParam UUID companyId) {
        
        Operation operation = operationService.getOperationByIdAndCompanyId(userId, id, companyId);
        return ResponseEntity.ok(operation);
    }
    
    @GetMapping
    public ResponseEntity<List<Operation>> findOperations(
            @RequestParam UUID userId,
            @RequestParam UUID companyId,
            @RequestBody(required = false) DateRangeInput dateRange,
            @RequestParam(required = false) List<UUID> articleIds,
            @RequestParam(required = false) List<UUID> articleGroupIds,
            @RequestParam(required = false) List<UUID> counterpartyIds,
            @RequestParam(required = false) List<UUID> counterpartyGroupIds,
            @RequestParam(required = false) List<UUID> projectIds,
            @RequestParam(required = false) List<UUID> projectDirectionIds,
            @RequestParam(required = false) List<String> operationTypes,
            @RequestParam(required = false) List<UUID> legalEntityIds,
            @RequestParam(required = false) List<UUID> bankAccountIds,
            @RequestParam(required = false) String description) {
        
        List<Operation> operations = operationService.findOperations(
                userId, companyId, dateRange, articleIds, articleGroupIds, counterpartyIds,
                counterpartyGroupIds, projectIds, projectDirectionIds, operationTypes,
                legalEntityIds, bankAccountIds, description);
        
        return ResponseEntity.ok(operations);
    }
    
    @PutMapping("/income")
    public ResponseEntity<IncomeOperation> updateIncomeOperation(
            @RequestParam UUID userId, 
            @RequestBody UpdateIncomeOperationInput input) {
        return ResponseEntity.ok(operationService.updateIncomeOperation(userId, input));
    }

    @PutMapping("/expense")
    public ResponseEntity<ExpenseOperation> updateExpenseOperation(
            @RequestParam UUID userId, 
            @RequestBody UpdateExpenseOperationInput input) {
        return ResponseEntity.ok(operationService.updateExpenseOperation(userId, input));
    }

    @PutMapping("/transfer")
    public ResponseEntity<TransferOperation> updateTransferOperation(
            @RequestParam UUID userId, 
            @RequestBody UpdateTransferOperationInput input) {
        return ResponseEntity.ok(operationService.updateTransferOperation(userId, input));
    }

    @DeleteMapping("/{companyId}/{id}")
    public ResponseEntity<Void> deleteOperation(
            @PathVariable UUID companyId, 
            @PathVariable UUID id, 
            @RequestParam UUID userId) {
        
        operationService.deleteOperation(id, userId, companyId);
        return ResponseEntity.noContent().build();
    }


}
