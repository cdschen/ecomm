package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.User;
import com.sooeez.ecomm.repository.UserRepository;
import com.sooeez.ecomm.security.SecurityUtils;
import com.sooeez.ecomm.service.UserService;

@Service
public class UserService {
	
	private final Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Autowired 
	private PasswordEncoder passwordEncoder;
	
	/*
	 * Repository
	 */
	
	@Autowired
	private UserRepository userRepository;
	
	/*
	 * Session
	 */
	
	@Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        User user = userRepository.findOneByUsernameAndEnabled(SecurityUtils.getCurrentLogin(), true);
		log.debug("Get Current Login user {}", user);
        return user;
    }
	
	/*
	 * User
	 */
	
	@Transactional
	public User saveUser(User user) {
		if (user.getId() == null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		return userRepository.save(user);
	}
	
	@Transactional
	public void deleteUser(Long id) {
		userRepository.delete(id);
	}
	
	@Transactional
	public void updatePassword(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.updatePassword(user.getPassword(), user.getId());
	}
	
	public Boolean existsUser(User user) {
		return userRepository.count(getUserSpecification(user)) > 0 ? true : false;
	}
	
	public User getUser(Long id) {
		return userRepository.findOne(id);
	}
	
	public List<User> getUsers(User user, Sort sort) {
		return userRepository.findAll(getUserSpecification(user), sort);
	}
	
	public Page<User> getPagedUsers(User user, Pageable pageable) {
		return userRepository.findAll(getUserSpecification(user), pageable);
	}
	
	private Specification<User> getUserSpecification(User user) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (user.getId() != null) {
				if (user.getCheckUnique() != null && user.getCheckUnique().booleanValue() == true) {
					predicates.add(cb.notEqual(root.get("id"), user.getId()));
				} else {
					predicates.add(cb.equal(root.get("id"), user.getId()));
				}
			}
			if (StringUtils.hasText(user.getUsername())) {
				predicates.add(cb.equal(root.get("username"), user.getUsername()));
			}
			if (user.getEnabled() != null) {
				predicates.add(cb.equal(root.get("enabled"), user.getEnabled()));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
		
	}
	
}
