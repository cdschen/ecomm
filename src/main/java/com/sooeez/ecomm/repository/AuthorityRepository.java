package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sooeez.ecomm.domain.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, String> {

}
