package com.sooeez.ecomm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.Role;
import com.sooeez.ecomm.domain.User;
import com.sooeez.ecomm.service.RoleService;
import com.sooeez.ecomm.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

	/*
	 * Service
	 */

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	/*
	 * User
	 */
	
	@RequestMapping(value = "/account")
    public ResponseEntity<User> getAccount() {
    	return new ResponseEntity<User>(userService.getUserWithAuthorities(), HttpStatus.OK);
    }
	
	@RequestMapping(value = "/users/check-unique")
	public Boolean existsUser(User user) {
		return userService.existsUser(user);
	}

	@RequestMapping(value = "/users/{id}")
	public User getUser(@PathVariable("id") Long id) {
		return userService.getUser(id);
	}

	@RequestMapping(value = "/users")
	public Page<User> getPagedUsers(User user, Pageable pageable) {
		return userService.getPagedUsers(user, pageable);
	}

	@RequestMapping(value = "/users/get/all")
	public List<User> getUsers(User user, Sort sort) {
		return userService.getUsers(user, sort);
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public User saveUser(@RequestBody User user) {
		return userService.saveUser(user);
	}

	@RequestMapping(value = "/users/update/password", method = RequestMethod.POST)
	public ResponseEntity<?> updatePassword(@RequestBody User user) {
		userService.updatePassword(user);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
		userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/*
	 * Role
	 */

	@RequestMapping(value = "/roles/{id}")
	public Role getRole(@PathVariable("id") Long id) {
		return roleService.getRole(id);
	}

	@RequestMapping(value = "/roles")
	public List<Role> getRoles(Role role) {
		return roleService.getRoles(role);
	}

	@RequestMapping(value = "/roles", method = RequestMethod.POST)
	public Role saveRole(@RequestBody Role role) {
		return roleService.saveRole(role);
	}

	@RequestMapping(value = "/roles/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteRole(@PathVariable("id") Long id) {
		roleService.deleteRole(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
