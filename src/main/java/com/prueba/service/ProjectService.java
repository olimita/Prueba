package com.prueba.service;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.prueba.data.TaskRepository;
import com.prueba.exceptions.ResourceNotFoundException;
import com.prueba.model.Project;
import com.prueba.model.Task;
import com.prueba.utils.CustomUtils;

@Repository
public class ProjectService {
	
	@Autowired
	private CustomUtils customUtils;
	
	@Autowired
	private TaskRepository taskRepository;
	
	public String validate(Project project) {
		String validation = validateNulls(project);
		if(!validation.equals("0"))
			return validation;
		validation = validateDates(project);
		if(!validation.equals("0"))
			return validation;
		return "0";
	}
	
	public void sanitize(Project project) {
		project.setProjectName(project.getProjectName().trim());
		project.setProjectDescription(project.getProjectDescription().trim());
	}
	
	public String validateNewEndDate(Long projectId, Date newEndDate) {
		Page<Task> tasks = taskRepository.findByProjectId(projectId,null)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "project_id", projectId));
		for(Task task : tasks.getContent()) {
			if(task.getExecutionDate().compareTo(newEndDate) < 0)
				return "10";
		}
		return "0";
	}
	
	public Boolean validatePorjectCompletition(Long projectId) {
		Page<Task> tasks = taskRepository.findByProjectId(projectId,null)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "project_id", projectId));
		for(Task task : tasks.getContent()) {
			if(!task.getState().equals("Realizada"))
				return false;
		}
		return true;
	}
	
	public String validateNulls(Project project) {
		if(!customUtils.validateNull(project.getProjectName()))
			return "1";
		if(!customUtils.validateNull(project.getProjectDescription()))
			return "2";
		if(project.getStartDate() == null)
			return "3";
		if(project.getEndDate() == null)
			return "4";
		return "0";
	}
	
	public String validateDates(Project project) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long currentTime = cal.getTimeInMillis();
		cal.setTime(project.getStartDate());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long startTime = cal.getTimeInMillis();
		cal.setTime(project.getEndDate());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long endTime = cal.getTimeInMillis();
		cal.setTime(project.getStartDate());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, 1);
		long minimumEndTime = cal.getTimeInMillis();	
		if(startTime < currentTime)
			return "5";
		if(endTime < minimumEndTime)
			return "6";
		
		return "0";
	}
	
}
