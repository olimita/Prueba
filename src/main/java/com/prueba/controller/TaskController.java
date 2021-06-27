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
import com.prueba.data.TaskRepository;
import com.prueba.exceptions.ResourceNotFoundException;
import com.prueba.model.Project;
import com.prueba.model.Task;
import com.prueba.service.TaskService;
import com.prueba.utils.CustomUtils;

@RestController
@RequestMapping("/api")
public class TaskController {
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private CustomUtils customUtils;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/project/task")
	public Page<?> getAllTasks(Pageable pageable) {
		return taskRepository.findAll(pageable);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/project/{project_id}/task")
	public Page<Task> getCommentsOnProjectWithId(@PathVariable(value = "project_id") Long projectId, Pageable pageable) {
		return taskRepository.findByProjectId(projectId,pageable)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "project_id", projectId));
	}
	
	@PreAuthorize("hasRole('OPERATOR')")
	@PostMapping("/project/{project_id}/task")
	public ResponseEntity<?> createComment(@PathVariable(value = "project_id") Long projectId, @RequestBody Task task) {
		String isValidTask = taskService.validate(projectId, task);
		if(!isValidTask.equals("0"))
			return ResponseEntity.badRequest().body("Invalid parameter: Error code " + isValidTask);	
		taskService.sanitize(task);
		task.setState("Pendiente");
		return projectRepository.findById(projectId).map(project -> {
			task.setProject(project);
            return ResponseEntity.ok().body(taskRepository.save(task));
        }).orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/project/{project_id}/task/{task_id}")
	public Task getProjectById(@PathVariable(value = "project_id") Long projectId, @PathVariable(value = "task_id") Long taskId) {
		return taskRepository.findByIdAndProjectId(taskId, projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Task", "task_id", taskId));
	}
	
	@PreAuthorize("hasRole('OPERATOR')")
	@PutMapping("/project/{project_id}/task/{task_id}")
	public ResponseEntity<?> updateProject(@PathVariable(value = "project_id") Long projectId, @PathVariable(value = "task_id") Long taskId, @RequestBody Task taskDetails) {
		Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Task", "task_id", taskId));
		if(customUtils.validateNull(taskDetails.getTaskName()))
			task.setTaskName(taskDetails.getTaskName());
		if(customUtils.validateNull(taskDetails.getTaskDescription()))
			task.setTaskDescription(taskDetails.getTaskDescription());
		Project associatedProject = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "project_id", projectId));
		String isValidDate = taskService.validateDates(associatedProject, task);
		if(!isValidDate.equals("0"))
			return ResponseEntity.badRequest().body("Invalid parameter: Error code " + isValidDate);
		if(taskDetails.getExecutionDate() != null)
			task.setExecutionDate(taskDetails.getExecutionDate());
		return ResponseEntity.ok().body(taskRepository.save(task));
	}
	
	@PreAuthorize("hasRole('OPERATOR')")
	@RequestMapping(value = "/project/{project_id}/task/complete/{task_id}", method = RequestMethod.PUT)
	public ResponseEntity<?> completeTask(@PathVariable(value = "project_id") Long projectId, @PathVariable(value = "task_id") Long taskId) {
		if(!projectRepository.existsById(projectId))
				throw new ResourceNotFoundException("Project", "project_id", projectId);
		Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Task", "task_id", taskId));
		if(task.getState().equals("Realizada"))
			ResponseEntity.ok().body("Task already completed");
		task.setState("Realizada");
		return ResponseEntity.ok().body(taskRepository.save(task));
	}
	
	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
	@DeleteMapping("/project/{project_id}/task/{task_id}")
	public ResponseEntity<?> deleteTask(@PathVariable(value = "project_id") Long projectId, @PathVariable(value = "task_id") Long taskId) {
		return taskRepository.findByIdAndProjectId(taskId, projectId).map(task -> {
			taskRepository.delete(task);
            return ResponseEntity.ok().body(true);
        }).orElseThrow(() -> new ResourceNotFoundException("Task", "task_id", taskId));
	}

}
