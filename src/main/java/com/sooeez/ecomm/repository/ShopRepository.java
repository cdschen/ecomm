package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sooeez.ecomm.domain.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {

}
