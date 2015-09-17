package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sooeez.ecomm.domain.ProductMember;

public interface ProductMemberRepository extends JpaRepository<ProductMember, Long> {

}
