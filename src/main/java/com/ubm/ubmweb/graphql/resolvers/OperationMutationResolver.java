package com.ubm.ubmweb.graphql.resolvers;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.TransferOperation;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.entities.ExpenseOperation;
import com.ubm.ubmweb.entities.IncomeOperation;
import com.ubm.ubmweb.graphql.dto.*;

import com.ubm.ubmweb.services.OperationsService;

@Component
@RequiredArgsConstructor
public class OperationMutationResolver implements GraphQLMutationResolver {
    private final OperationsService operationsService;

    public TransferOperation createTransferOperation(CreateTransferOperationInput input, Long userId) {
        try{
            TransferOperation operation = operationsService.createTransferOperation(userId, input);
            return operation;
        }
        catch (Exception e) {
            // Log the exception, handle it as needed
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public IncomeOperation createIncomeOperation(CreateIncomeOperationInput input, Long userId) {
        try{
            IncomeOperation operation = operationsService.createIncomeOperation(userId ,input);
            return operation;
        }
        catch (Exception e) {
            // Log the exception, handle it as needed
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public ExpenseOperation createExpenseOperation(CreateExpenseOperationInput input, Long userId) {
        try{
            ExpenseOperation operation = operationsService.createExpenseOperation(userId ,input);
            return operation;
        }
        catch (Exception e) {
            // Log the exception, handle it as needed
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public TransferOperation updateTransferOperation(UpdateTransferOperationInput input, Long userId) {
        try{
            TransferOperation operation = operationsService.updateTransferOperation(userId ,input);
            return operation;
        }
        catch (Exception e) {
            // Log the exception, handle it as needed
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public IncomeOperation updateIncomeOperation(UpdateIncomeOperationInput input, Long userId) {
        try{
            IncomeOperation operation = operationsService.updateIncomeOperation(userId, input);
            return operation;
        } catch (Exception e) {
            // Log the exception, handle it as needed
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public ExpenseOperation updateExpenseOperation(UpdateExpenseOperationInput input, Long userId) {
        try{
            ExpenseOperation operation = operationsService.updateExpenseOperation(userId, input);
            return operation;
        } catch (Exception e) {
            // Log the exception, handle it as needed
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public Boolean deleteOperation(Long id, Long userId, Long companyId) {
        try {
            operationsService.deleteOperation(id, userId, companyId);
            return true;
        } catch (Exception e) {
            // Log the exception, handle it as needed
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return false;
        }
    }
}
