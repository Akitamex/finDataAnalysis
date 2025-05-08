package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.ProjectDirection;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.ProjectDirectionService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectDirectionQueryResolver implements GraphQLQueryResolver{
    private final ProjectDirectionService projectDirectionService;

    public List<ProjectDirection> projectDirections(Long companyId, Long userId, String name){
        try{
            return projectDirectionService.findProjectDirections(userId, companyId, name);
        }
        catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public ProjectDirection projectDirectionById(Long id, Long userId, Long companyId){
        try{
            return projectDirectionService.getProjectDirectionByIdAndCompanyId(userId, companyId, id);
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
