package com.sooeez.ecomm.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.Authority;
import com.sooeez.ecomm.domain.Role;
import com.sooeez.ecomm.domain.User;
import com.sooeez.ecomm.repository.AuthorityRepository;
import com.sooeez.ecomm.repository.RoleRepository;
import com.sooeez.ecomm.repository.UserRepository;
import com.sooeez.ecomm.security.SecurityUtils;
import com.sooeez.ecomm.service.UserService;

@Service
public class UserService {
	
	private final Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Autowired private PasswordEncoder passwordEncoder;
	
	@Autowired private UserRepository userRepository;
	
	@Autowired private RoleRepository roleRepository;
	
	@Autowired private AuthorityRepository authorityRepository;
	
	/*
	 * Session
	 */
	
	@Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        User user = userRepository.findOneByUsername(SecurityUtils.getCurrentLogin());
        user.getRoles().forEach(role -> role.getAuthorities());
		log.debug("Get Current Login user {}", user);
        return user;
    }
	
	/*
	 * User
	 */
	
	@Transactional
	public User saveUser(User user) {
		if (user.getId() == null)
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		return this.userRepository.save(user);
	}
	
	@Transactional
	public void deleteUser(Long id) {
		this.userRepository.delete(id);
	}
	
	public boolean existsUser(String username) {
		int count = this.userRepository.existsByUsername(username);
		return count > 0 ? true : false;
	}
	
	public boolean existsNotSelfUser(String username, Long id) {
		int count = this.userRepository.existsNotSelfByUsername(username, id);
		return count > 0 ? true : false;
	}
	
	public User getUser(Long id) {
		return this.userRepository.findOne(id);
	}
	
	public List<User> getUsers() {
		return this.userRepository.findAll();
	}
	
	public Page<User> getPagedUsers(Pageable pageable) {
		return this.userRepository.findAll(pageable);
	}
	
	/*
	 * Role
	 */
	
	@Transactional
	public Role saveRole(Role role) {
		return this.roleRepository.save(role);
	}
	
	@Transactional
	public void deleteRole(Long id) {
		this.roleRepository.delete(id);
	}
	
	public Role getRole(Long id) {
		return this.roleRepository.findOne(id);
	}
	
	public List<Role> getRoles() {
		return this.roleRepository.findAll();
	}
	
	/*
	 * Authority
	 */
	
	public List<Authority> getAuthorities() {
		return this.authorityRepository.findAll();
	}
}
