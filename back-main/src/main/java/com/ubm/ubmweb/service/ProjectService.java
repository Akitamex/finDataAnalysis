package com.ubm.ubmweb.service;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.graphql.dto.CreateProjectInput;
import com.ubm.ubmweb.graphql.dto.UpdateProjectInput;
import com.ubm.ubmweb.model.Company;
import com.ubm.ubmweb.model.Operation;
import com.ubm.ubmweb.model.Project;
import com.ubm.ubmweb.model.ProjectDirection;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.OperationsRepository;
import com.ubm.ubmweb.repository.ProjectDirectionRepository;
import com.ubm.ubmweb.repository.ProjectRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
// import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectDirectionRepository projectDirectionRepository;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final CompanyRepository companyRepository;
    private final OperationsRepository operationsRepository;

    @Transactional
    public Project createProject(UUID userId, CreateProjectInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Company company = companyRepository.findById(input.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + input.getCompanyId()));

        ProjectDirection projectDirection = null;
        if(input.getDirectionId() != null){
            projectDirection = projectDirectionRepository.findById(input.getDirectionId())
                .orElseThrow(() -> new IllegalArgumentException("ProjectDirection not found for the given id: " + input.getDirectionId()));
            if (!projectDirection.getCompany().getId().equals(input.getCompanyId())) {
                throw new UnauthorizedAccessException("Requested ProjectDirection does not belong to the provided company.");
            }
        }

        if(!(input.getType().equals("PROJECT") || input.getType().equals("DEAL"))){
            throw new IllegalArgumentException("project type must equal either PROJECT or DEAL");
        }

        Project project = new Project();
        project.setCompany(company);
        project.setType(input.getType());
        //Status is set to IN_PROGRESS by default (at least it should be, see:...entities/Project.java)
        project.setName(input.getName());
        project.setDescription(input.getDescription());
        project.setProjectDirection(projectDirection);
        project.setCompany(company);

        if(projectDirection != null)
            projectDirection.addProject(project);
        company.addProject(project);
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public List<Project> findProjects(UUID userId, UUID companyId, List<String> types, List<String> statuses, List<UUID> directionIds, String name, String description) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        if (directionIds != null && !directionIds.isEmpty()) {
            List<ProjectDirection> validDirections = projectDirectionRepository.findByIdInAndCompanyId(directionIds, companyId);
            if (validDirections.size() != directionIds.size()) {
                throw new IllegalArgumentException("One or more ProjectDirections do not belong to the specified company.");
            }
        }
        Specification<Project> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Mandatory filter for companyId
            predicates.add(cb.equal(root.get("company").get("id"), companyId));
            
            // Optional filters
            if (types != null && !types.isEmpty()) {
                predicates.add(root.get("type").in(types));
            }
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }
            if (directionIds != null && !directionIds.isEmpty()) {
                predicates.add(root.get("direction").get("id").in(directionIds));
            }
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (description != null && !description.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return projectRepository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public Project getProjectByIdAndCompanyId(UUID userId, UUID companyId, UUID id){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found for the given id: " + id));
        if (!project.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested Project does not belong to the provided company.");
        }
        return project;
    }

    @Transactional
    public Project updateProject(UUID userId, UpdateProjectInput input){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(input.getCompanyId(), userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Project project = projectRepository.findById(input.getId())
            .orElseThrow(() -> new IllegalArgumentException("Project not found for the given id: " + input.getId()));
        if (!project.getCompany().getId().equals(input.getCompanyId())) {
            throw new UnauthorizedAccessException("Requested Project does not belong to the provided company.");
        }
        ProjectDirection projectDirection = null;
        if(input.getDirectionId() != null){
            projectDirection = projectDirectionRepository.findById(input.getDirectionId())
                .orElseThrow(() -> new IllegalArgumentException("ProjectDirection not found for the given id: " + input.getDirectionId()));
            if (!projectDirection.getCompany().getId().equals(input.getCompanyId())) {
                throw new UnauthorizedAccessException("Requested ProjectDirection does not belong to the provided company.");
            }
        }
        ProjectDirection oldProjectDirection = project.getProjectDirection();
        if(oldProjectDirection != null)
            oldProjectDirection.removeProject(project);

        if(!(input.getType().equals("PROJECT") || input.getType().equals("DEAL"))){
            throw new IllegalArgumentException("project type must equal either PROJECT or DEAL");
        }

        project.setType(input.getType());
        project.setStatus(input.getStatus());
        project.setProjectDirection(projectDirection);
        project.setName(input.getName());
        project.setDescription(input.getDescription());

        if(projectDirection != null)
            projectDirection.addProject(project);
        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(UUID userId, UUID companyId, UUID id){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found for the given id: " + id));
        if (!project.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested Project does not belong to the provided company.");
        }
        List<Operation> operations = operationsRepository.findByProjectId(id);
        
        if (!operations.isEmpty()) {
            // Throw an exception to prevent deletion
            throw new IllegalStateException("Cannot delete project as it has associated operations.");
        }

        projectRepository.deleteById(id);
    }

}
