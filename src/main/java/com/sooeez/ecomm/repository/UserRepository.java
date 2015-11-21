package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sooeez.ecomm.domain.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
	
	User findOneByUsernameAndEnabled(String username, Boolean enabled);

	@Modifying
	@Query("update User set password = ?1 where id = ?2")
	void updatePassword(String password, Long id);
	
}
