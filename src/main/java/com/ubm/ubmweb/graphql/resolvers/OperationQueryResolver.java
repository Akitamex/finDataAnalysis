// Assuming an existing setup for GraphQL with graphql-java
package com.ubm.ubmweb.graphql.resolvers;

import com.ubm.ubmweb.services.*;
import com.ubm.ubmweb.entities.*;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.DateRangeInput;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperationQueryResolver implements GraphQLQueryResolver {

    private final OperationsService operationsService;
    
    public List<Operation> operations(Long companyId, Long userId, DateRangeInput dateRange, List<Long> articleIds, 
                                      List<Long> articleGroupIds, List<Long> counterpartyIds, 
                                      List<Long> counterpartyGroupIds, List<Long> projectIds, 
                                      List<Long> projectDirectionIds, List<String> operationTypes, 
                                      List<Long> legalEntityIds, List<Long> bankAccountIds, String description) {
        try{
            return operationsService.findOperations(userId, companyId, dateRange, articleIds, 
                                                articleGroupIds, counterpartyIds, counterpartyGroupIds, 
                                                projectIds, projectDirectionIds, operationTypes, 
                                                legalEntityIds, bankAccountIds, description);
        }
        catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }
    
    public Operation operationById(Long id, Long userId, Long companyId){
        try{
            return operationsService.getOperationByIdAndCompanyId(userId, id, companyId);
        }
        catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }
}
