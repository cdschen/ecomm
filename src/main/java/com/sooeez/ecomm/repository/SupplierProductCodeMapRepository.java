package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sooeez.ecomm.domain.SupplierProductCodeMap;

public interface SupplierProductCodeMapRepository extends JpaRepository<SupplierProductCodeMap, Long>, JpaSpecificationExecutor<SupplierProductCodeMap> {

}
