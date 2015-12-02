package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.Role;
import com.sooeez.ecomm.domain.User;
import com.sooeez.ecomm.repository.RoleRepository;

@Service
public class RoleService {
	
	@PersistenceContext
	private EntityManager em;
	
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
	
	public List<Role> getRoles(Role role) {
		return roleRepository.findAll(getRoleSpecification(role));
	}
	
	private Specification<Role> getRoleSpecification(Role role) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			
			
			
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
		
	}
	
}
