package com.ubm.ubmweb.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.entities.Company;
import com.ubm.ubmweb.entities.Project;
import com.ubm.ubmweb.entities.ProjectDirection;
import com.ubm.ubmweb.repository.CompanyRepository;
import com.ubm.ubmweb.repository.ProjectDirectionRepository;
import com.ubm.ubmweb.repository.ProjectRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectDirectionService {
    
    private final ProjectDirectionRepository projectDirectionRepository;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;
    private final CompanyRepository companyRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectDirection createProjectDirection(Long userId, Long companyId, String name){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Company not found for the given id: " + companyId));

        ProjectDirection projectDirection = new ProjectDirection();
        
        projectDirection.setName(name);
        projectDirection.setCompany(company);

        company.addProjectDirection(projectDirection);

        return projectDirectionRepository.save(projectDirection);
    }

    @Transactional(readOnly = true)
    public List<ProjectDirection> findProjectDirections(Long userId, Long companyId, String name) {
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }

        Specification<ProjectDirection> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Mandatory filter for companyId
            predicates.add(cb.equal(root.get("company").get("id"), companyId));
            
            // Optional filter for name
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return projectDirectionRepository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public ProjectDirection getProjectDirectionByIdAndCompanyId(Long userId, Long companyId, Long id){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        ProjectDirection projectDirection = projectDirectionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ProjectDirection not found for the given id: " + id));
        if (!projectDirection.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested ProjectDirection does not belong to the provided company.");
        }
        return projectDirection;
    }

    @Transactional
    public ProjectDirection updateProjectDirection(Long userId, Long companyId, Long id, String name){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        ProjectDirection projectDirection = projectDirectionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ProjectDirection not found for the given id: " + id));
        if (!projectDirection.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested ProjectDirection does not belong to the provided company.");
        }
        projectDirection.setName(name);
        return projectDirectionRepository.save(projectDirection);
    }

    @Transactional
    public void deleteProjectDirection(Long userId, Long companyId, Long id){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated){
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        ProjectDirection projectDirection = projectDirectionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ProjectDirection not found for the given id: " + id));
        if (!projectDirection.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedAccessException("Requested ProjectDirection does not belong to the provided company.");
        }

        List<Project> projects = projectRepository.findByProjectDirectionId(id);
        if(!projects.isEmpty()){
            throw new IllegalStateException("Cannot delete ProjectDirection as it has associated projects.");
        }

        projectDirectionRepository.deleteById(id);
    }

}
