package com.ubm.ubmweb.graphql.resolvers;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Company;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CompanyInput;
import com.ubm.ubmweb.services.CompanyService;


@Component
@RequiredArgsConstructor
public class CompanyMutationResolver implements GraphQLMutationResolver{
    private final CompanyService companyService;
    
    public Company createCompany(CompanyInput data, Long userId){
		System.out.println("Inside createCompany");
        Company company = companyService.createCompany(userId, data);
        return company;
    }
    public Company updateCompany(Long id, Long userId, CompanyInput data){
        try{
            Company company = companyService.updateCompany(id, userId, data);
            return company;
        }
        catch (Exception e){
            if(e instanceof UnauthorizedAccessException){
                throw new UnauthorizedAccessException(e.getMessage());
            }
            if(e instanceof IllegalArgumentException){
                throw new IllegalArgumentException(e.getMessage());
            }
            return null;
        }
    }
    public Boolean deleteCompany(Long id, Long userId) {
        try {
            companyService.deleteCompany(id, userId);
            return true;
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
