package com.prueba.data;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prueba.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
	
	Optional<Page<Task>> findByProjectId(Long projectId, Pageable pageable);
    Optional<Task> findByIdAndProjectId(Long id, Long projectId);
	
}
