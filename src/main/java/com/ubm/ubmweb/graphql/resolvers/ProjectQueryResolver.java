package com.ubm.ubmweb.graphql.resolvers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Project;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.ProjectService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectQueryResolver implements GraphQLQueryResolver{
    private final ProjectService projectService;

    public List<Project> projects(Long companyId, Long userId, List<String> types, List<String> statuses, List<Long> directionIds, String name, String description){
        try{
            return projectService.findProjects(userId, companyId, types, statuses, directionIds, name, description);
        }
        catch(Exception e){
            if(e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if(e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }

    public Project projectById(Long id, Long userId, Long companyId){
        try{
            return projectService.getProjectByIdAndCompanyId(userId, companyId, id);
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
