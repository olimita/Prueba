package com.prueba.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class BlockedUserException extends RuntimeException {
	
	private static final long serialVersionUID = 5344320715123995240L;
	private String username;
	
	public BlockedUserException(String username) {
		super(String.format("We are sorry the user '%s' has been blocked", username));
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
