package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.BankAccount;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.BankAccountService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BankAccountQueryResolver implements GraphQLQueryResolver{
    private final BankAccountService bankAccountService;
    
    public List<BankAccount> bankAccounts(Long companyId, Long userId, String name, String currency, String type, String bank, String BIC, String correspondentAccount, String accountNumber, List<Long> legalEntityIds, List<Long> ids){
        try{
            return bankAccountService.findBankAccounts(userId, companyId, name, currency, type, bank, BIC, correspondentAccount, accountNumber, legalEntityIds, ids);
        } catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public BankAccount bankAccountById(Long id, Long userId, Long companyId){
        try{
            return bankAccountService.getBankAccountByIdAndCompanyId(userId, companyId, id);
        } catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }
}
