package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sooeez.ecomm.domain.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {

}
