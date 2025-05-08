package com.ubm.ubmweb.graphql.resolvers;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.Project;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.graphql.dto.CreateProjectInput;
import com.ubm.ubmweb.graphql.dto.UpdateProjectInput;
import com.ubm.ubmweb.services.ProjectService;

@Component
@RequiredArgsConstructor
public class ProjectMutationResolver implements GraphQLMutationResolver{
    private final ProjectService projectService;

    public Project createProject(CreateProjectInput input, Long userId){
        try{
            Project project = projectService.createProject(userId, input);
            return project;
        } catch (Exception e) {
            // Log the exception, handle it as needed
            if (e instanceof UnauthorizedAccessException)
                throw new UnauthorizedAccessException(e.getMessage());
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            return null;
        }
    }
    public Project updateProject(UpdateProjectInput input, Long userId){
        try{
            Project project = projectService.updateProject(userId, input);
            return project;
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
    public Boolean deleteProject(Long id, Long userId, Long companyId) {
        try {
            projectService.deleteProject(userId, companyId, id);
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
