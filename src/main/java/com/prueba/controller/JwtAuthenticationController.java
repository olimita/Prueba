package com.prueba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.UserDetails;
import com.prueba.config.JwtTokenUtil;
import com.prueba.model.ChangePasswordDTO;
import com.prueba.model.ChangeUserStateDTO;
import com.prueba.model.JwtRequest;
import com.prueba.model.JwtResponse;
import com.prueba.model.UserDTO;
import com.prueba.service.JwtUserDetailsService;


@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;
	
	@RequestMapping(value = "/api/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

		userDetailsService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(token));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/api/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
		return ResponseEntity.ok(userDetailsService.save(user));
	}
	
	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
	@RequestMapping(value = "/api/changePassword", method = RequestMethod.POST)
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO req) throws Exception {
		return ResponseEntity.ok(userDetailsService.update(req));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/api/changeUserState", method = RequestMethod.POST)
	public ResponseEntity<?> changeUserState(@RequestBody ChangeUserStateDTO user) throws Exception {
		
		return ResponseEntity.ok(userDetailsService.changeUserState(user));
	}

}
