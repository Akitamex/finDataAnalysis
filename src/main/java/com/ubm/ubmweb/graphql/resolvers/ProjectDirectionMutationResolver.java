package com.ubm.ubmweb.graphql.resolvers;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ubm.ubmweb.entities.ProjectDirection;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.services.ProjectDirectionService;

@Component
@RequiredArgsConstructor
public class ProjectDirectionMutationResolver implements GraphQLMutationResolver{
    private final ProjectDirectionService projectDirectionService;

    public ProjectDirection createProjectDirection(String name, Long userId, Long companyId){
        try{
            ProjectDirection projectDirection = projectDirectionService.createProjectDirection(userId, companyId, name);
            return projectDirection;
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
    public ProjectDirection updateProjectDirection(Long id, Long userId, String name, Long companyId){
        try{
            ProjectDirection projectDirection = projectDirectionService.updateProjectDirection(userId, companyId, id, name);
            return projectDirection;
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
    public Boolean deleteProjectDirection(Long id, Long userId, Long companyId) {
        try {
            projectDirectionService.deleteProjectDirection(userId, companyId, id);
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
