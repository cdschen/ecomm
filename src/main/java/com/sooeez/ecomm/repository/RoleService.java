package com.sooeez.ecomm.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.Role;

@Service
public class RoleService {
	
	/*
	 * Repository
	 */

	@Autowired
	private RoleRepository roleRepository;
	
	/*
	 * Role
	 */
	
	@Transactional
	public Role saveRole(Role role) {
		return roleRepository.save(role);
	}
	
	@Transactional
	public void deleteRole(Long id) {
		roleRepository.delete(id);
	}
	
	public Role getRole(Long id) {
		return roleRepository.findOne(id);
	}
	
	public List<Role> getRoles() {
		return roleRepository.findAll();
	}
	
}
