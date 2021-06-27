package com.prueba.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.prueba.dao.RoleDao;
import com.prueba.dao.UserDao;
import com.prueba.exceptions.BlockedUserException;
import com.prueba.exceptions.InternalServerException;
import com.prueba.model.ChangePasswordDTO;
import com.prueba.model.ChangeUserStateDTO;
import com.prueba.model.DAOUser;
import com.prueba.model.Role;
import com.prueba.model.UserDTO;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RoleDao roleDao;

	@Autowired
	private PasswordEncoder bcryptEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, BlockedUserException {
		DAOUser user = userDao.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		} else if (!user.getIsActive()) {
			throw new BlockedUserException(username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				getAuthorities(user));
	}
	
	public DAOUser update(ChangePasswordDTO req) throws Exception {
		authenticate(req.getUsername(), req.getOldPassword());
		DAOUser user = userDao.findByUsername(req.getUsername());
		user.setPassword(bcryptEncoder.encode(req.getNewPassword()));
		return userDao.save(user);
	}
	
	public DAOUser save(UserDTO user) {
		DAOUser newUser = new DAOUser();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setIsActive(true);
		Role rol = roleDao.findById(Long.valueOf(2L))
				.orElseThrow(() -> new InternalServerException("12", "No user role configured"));
		Set<Role> roles = new HashSet<Role>();
		roles.add(rol);
		newUser.setRoles(roles);
		return userDao.save(newUser);
	}
	
	public void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
	
	private static Set<? extends GrantedAuthority> getAuthorities(DAOUser retrievedUser) {
		Set<Role> roles = retrievedUser.getRoles();
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" +  role.getName())));
		return authorities;
	}
	
	public DAOUser changeUserState(ChangeUserStateDTO changeReq) {
		DAOUser user = userDao.findByUsername(changeReq.getUsername());
		user.setIsActive(!changeReq.getIsBlocked());
		return userDao.save(user);
	}
	
}
