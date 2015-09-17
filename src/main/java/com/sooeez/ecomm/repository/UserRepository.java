package com.sooeez.ecomm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sooeez.ecomm.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("select count(*) from User u where u.username = ?1")
	int existsByUsername(String username);
	
	@Query("select count(*) from User u where u.username = ?1 and u.id <> ?2")
	int existsNotSelfByUsername(String username, Long id);
	
	List<User> findAllByUsername(String username);
	
	User findOneByUsername(String username);
	
	User findOneByEmail(String email);
	
}
