package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Company;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.CompanyService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompanyQueryResolver implements GraphQLQueryResolver{
    private final CompanyService companyService;
    
    public List<Company> companies(Long userId){
        return companyService.findCompaniesByUserId(userId);
    }

    public Company companyById(Long id, Long userId){
        try{
            return companyService.getCompanyByIdIfUserAssociated(id, userId);
        } catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }
}
