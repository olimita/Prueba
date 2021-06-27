package com.prueba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.prueba.data.ProjectRepository;
import com.prueba.exceptions.ResourceNotFoundException;
import com.prueba.model.Project;
import com.prueba.service.ProjectService;
import com.prueba.utils.CustomUtils;

@RestController
@RequestMapping("/api")
public class ProjectController {
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private CustomUtils customUtils;
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/project")
	public Page<Project> getAllProjects(Pageable pageable) {
		return projectRepository.findAll(pageable);
	}
	
	@PreAuthorize("hasRole('OPERATOR')")
	@PostMapping("/project")
	public ResponseEntity<?> createProject(@RequestBody Project project) {
		String isValidProject = projectService.validate(project);
		if(!isValidProject.equals("0"))
			return ResponseEntity.badRequest().body("Invalid parameter: Error code " + isValidProject);	
		projectService.sanitize(project);
		project.setState("En Proceso");
		return ResponseEntity.ok().body(projectRepository.save(project));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/project/{id}")
	public Project getProjectById(@PathVariable(value = "id") Long projectId) {
		return projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
	}

	@PreAuthorize("hasRole('OPERATOR')")
	@PutMapping("/project/{id}")
	public ResponseEntity<?> updateProject(@PathVariable(value = "id") Long projectId, @RequestBody Project projectDetails) {

		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
		if(customUtils.validateNull(projectDetails.getProjectName()))
			project.setProjectName(projectDetails.getProjectName());
		if(customUtils.validateNull(projectDetails.getProjectDescription()))
			project.setProjectDescription(projectDetails.getProjectDescription());
		String isValidDate = projectService.validateNewEndDate(projectId,projectDetails.getEndDate());
		if(!isValidDate.equals("0"))
			return ResponseEntity.badRequest().body("Invalid parameter: Error code " + isValidDate);
		if(projectDetails.getEndDate() != null)
			project.setEndDate(projectDetails.getEndDate());
		Project updatedProject = projectRepository.save(project);
		return ResponseEntity.ok().body(updatedProject);
	}
	
	@PreAuthorize("hasRole('OPERATOR')")
	@RequestMapping(value = "/project/complete/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> completeProject(@PathVariable(value = "id") Long projectId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
		if(project.getState().equals("Finalizado"))
			return ResponseEntity.ok().body("Project already completed");
		if(!projectService.validatePorjectCompletition(projectId))
			return ResponseEntity.ok().body("Project does not fulfill the requirements to be completed");
		project.setState("Finalizado");
		Project updatedProject = projectRepository.save(project);
		return ResponseEntity.ok().body(updatedProject);
	}
	
	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
	@DeleteMapping("/project/{id}")
	public ResponseEntity<?> deleteProject(@PathVariable(value = "id") Long projectId, Pageable pageable) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
		projectRepository.delete(project);
		return ResponseEntity.ok().body(true);
	}
	
}
