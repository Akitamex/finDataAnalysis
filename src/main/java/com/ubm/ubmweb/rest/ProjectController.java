package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.graphql.dto.CreateProjectInput;
import com.ubm.ubmweb.graphql.dto.UpdateProjectInput;
import com.ubm.ubmweb.model.Project;
import com.ubm.ubmweb.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Project> createProject(
            @RequestParam UUID userId,
            @RequestBody CreateProjectInput input) {
        Project project = projectService.createProject(userId, input);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @GetMapping
    public ResponseEntity<List<Project>> findProjects(
            @RequestParam UUID userId,
            @RequestParam UUID companyId,
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<UUID> directionIds,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description) {
        List<Project> projects = projectService.findProjects(userId, companyId, types, statuses, directionIds, name, description);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(
            @RequestParam UUID userId,
            @RequestParam UUID companyId,
            @PathVariable UUID id) {
        Project project = projectService.getProjectByIdAndCompanyId(userId, companyId, id);
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
            @RequestParam UUID userId,
            @RequestBody UpdateProjectInput input) {
        Project updatedProject = projectService.updateProject(userId, input);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @RequestParam UUID userId,
            @RequestParam UUID companyId,
            @PathVariable UUID id) {
        projectService.deleteProject(userId, companyId, id);
        return ResponseEntity.noContent().build();
    }
}
