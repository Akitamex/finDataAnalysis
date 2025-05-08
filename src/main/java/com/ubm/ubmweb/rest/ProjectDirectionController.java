package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.model.ProjectDirection;
import com.ubm.ubmweb.service.ProjectDirectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/project-directions")
@RequiredArgsConstructor
public class ProjectDirectionController {

    private final ProjectDirectionService projectDirectionService;

    @PostMapping
    public ResponseEntity<ProjectDirection> createProjectDirection(
            @RequestParam UUID userId,
            @RequestParam UUID companyId,
            @RequestParam String name) {
        ProjectDirection projectDirection = projectDirectionService.createProjectDirection(userId, companyId, name);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectDirection);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDirection>> findProjectDirections(
            @RequestParam UUID userId,
            @RequestParam UUID companyId,
            @RequestParam(required = false) String name) {
        List<ProjectDirection> projectDirections = projectDirectionService.findProjectDirections(userId, companyId, name);
        return ResponseEntity.ok(projectDirections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDirection> getProjectDirectionById(
            @RequestParam UUID userId,
            @RequestParam UUID companyId,
            @PathVariable UUID id) {
        ProjectDirection projectDirection = projectDirectionService.getProjectDirectionByIdAndCompanyId(userId, companyId, id);
        return ResponseEntity.ok(projectDirection);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDirection> updateProjectDirection(
            @RequestParam UUID userId,
            @RequestParam UUID companyId,
            @PathVariable UUID id,
            @RequestParam String name) {
        ProjectDirection updatedProjectDirection = projectDirectionService.updateProjectDirection(userId, companyId, id, name);
        return ResponseEntity.ok(updatedProjectDirection);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectDirection(
            @RequestParam UUID userId,
            @RequestParam UUID companyId,
            @PathVariable UUID id) {
        projectDirectionService.deleteProjectDirection(userId, companyId, id);
        return ResponseEntity.noContent().build();
    }
}
