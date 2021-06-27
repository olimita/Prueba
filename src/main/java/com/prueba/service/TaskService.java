package com.prueba.service;

import java.util.Calendar;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.prueba.data.ProjectRepository;
import com.prueba.exceptions.ResourceNotFoundException;
import com.prueba.model.Project;
import com.prueba.model.Task;
import com.prueba.utils.CustomUtils;

@Repository
public class TaskService {
	
	@Autowired
	private CustomUtils customUtils;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	public String validate(Long projectId, Task task) {
		Project associatedProject = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "project_id", projectId));
		String validation = validateNulls(task);
		if(!validation.equals("0"))
			return validation;
		validation = validateDates(associatedProject, task);
		if(!validation.equals("0"))
			return validation;
		return "0";
	}
	
	public void sanitize(Task task) {
		task.setTaskName(task.getTaskName().trim());
		task.setTaskDescription(task.getTaskDescription().trim());
	}
	
	public String validateNulls(Task task) {
		if(!customUtils.validateNull(task.getTaskName()))
			return "7";
		if(!customUtils.validateNull(task.getTaskDescription()))
			return "8";
		if(task.getExecutionDate() == null)
			return "9";
		return "0";
	}
	
	public String validateDates(Project project, Task task) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(task.getExecutionDate());
		long taskTime = cal.getTimeInMillis();
		cal.setTime(project.getStartDate());
		long startTime = cal.getTimeInMillis();
		cal.setTime(project.getEndDate());
		long endTime = cal.getTimeInMillis();
		if(startTime > taskTime  || taskTime > endTime)
			return "9";
		return "0";
	}

}
