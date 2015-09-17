package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sooeez.ecomm.domain.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
