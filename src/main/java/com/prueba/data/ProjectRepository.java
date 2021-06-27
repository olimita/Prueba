package com.prueba.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prueba.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

}
