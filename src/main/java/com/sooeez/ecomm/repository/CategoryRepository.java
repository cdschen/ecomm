package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sooeez.ecomm.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
