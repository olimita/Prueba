package com.prueba.utils;

import org.springframework.stereotype.Repository;

@Repository
public class CustomUtils {
	
	public Boolean validateNull(String string) {
		if(string == null || string.trim().equals(""))
			return false;
		return true;
	}
	
}
