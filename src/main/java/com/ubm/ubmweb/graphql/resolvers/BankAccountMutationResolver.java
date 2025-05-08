package com.ubm.ubmweb.graphql.resolvers;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.BankBankAccount;
import com.ubm.ubmweb.entities.CashBankAccount;
import com.ubm.ubmweb.entities.FundBankAccount;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.*;

import com.ubm.ubmweb.services.BankAccountService;

@Component
@RequiredArgsConstructor
public class BankAccountMutationResolver implements GraphQLMutationResolver {

    private final BankAccountService bankAccountService;
    
    public BankBankAccount createBankBankAccount(CreateBankBankAccountInput input, Long userId) {
        try{
            BankBankAccount bankAccount = bankAccountService.createBankBankAccount(userId, input);
            return bankAccount;
        }   catch (Exception e){
            if(e instanceof UnauthorizedAccessException){
                throw new UnauthorizedAccessException(e.getMessage());
            }
            if(e instanceof IllegalArgumentException){
                throw new IllegalArgumentException(e.getMessage());
            }
            return null;
        }
    }

    public CashBankAccount createCashBankAccount(CreateCashBankAccountInput input, Long userId) {
        try{
            CashBankAccount bankAccount = bankAccountService.createCashBankAccount(userId ,input);
            return bankAccount;
        } catch (Exception e){
            if(e instanceof UnauthorizedAccessException){
                throw new UnauthorizedAccessException(e.getMessage());
            }
            if(e instanceof IllegalArgumentException){
                throw new IllegalArgumentException(e.getMessage());
            }
            return null;
        }
    }

    public FundBankAccount createFundBankAccount(CreateFundBankAccountInput input, Long userId) {
        try{
            FundBankAccount bankAccount = bankAccountService.createFundBankAccount(userId ,input);
            return bankAccount;
        } catch (Exception e){
            if(e instanceof UnauthorizedAccessException){
                throw new UnauthorizedAccessException(e.getMessage());
            }
            if(e instanceof IllegalArgumentException){
                throw new IllegalArgumentException(e.getMessage());
            }
            return null;
        }
    }

    public BankBankAccount updateBankBankAccount(UpdateBankBankAccountInput input, Long userId) {
        try{
            BankBankAccount bankAccount = bankAccountService.updateBankBankAccount(userId ,input);
            return bankAccount;
        } catch (Exception e){
            if(e instanceof UnauthorizedAccessException){
                throw new UnauthorizedAccessException(e.getMessage());
            }
            if(e instanceof IllegalArgumentException){
                throw new IllegalArgumentException(e.getMessage());
            }
            return null;
        }
    }

    public CashBankAccount updateCashBankAccount(UpdateCashBankAccountInput input, Long userId) {
        try{
            CashBankAccount bankAccount = bankAccountService.updateCashBankAccount(userId ,input);
            return bankAccount;
        } catch (Exception e){
            if(e instanceof UnauthorizedAccessException){
                throw new UnauthorizedAccessException(e.getMessage());
            }
            if(e instanceof IllegalArgumentException){
                throw new IllegalArgumentException(e.getMessage());
            }
            return null;
        }
    }

    public FundBankAccount updateFundBankAccount(UpdateFundBankAccountInput input, Long userId) {
        try{
            FundBankAccount bankAccount = bankAccountService.updateFundBankAccount(userId ,input);
            return bankAccount;
        } catch (Exception e){
            if(e instanceof UnauthorizedAccessException){
                throw new UnauthorizedAccessException(e.getMessage());
            }
            if(e instanceof IllegalArgumentException){
                throw new IllegalArgumentException(e.getMessage());
            }
            return null;
        }
    }

    public Boolean deleteBankAccount(Long id, Long userId, Long companyId) {
        try {
            bankAccountService.deleteBankAccount(id, userId, companyId);
            return true; // Indicate success
        } catch (Exception e) {
            // Log the exception, handle it as needed
            if (e instanceof IllegalStateException)
                throw new IllegalStateException(e.getMessage());
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return false;
        }
    }
}
