package com.prueba.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.prueba.model.Role;

@Repository
public interface RoleDao extends CrudRepository<Role, Integer> {
	
	Optional<Role> findById(Long Id);

}
