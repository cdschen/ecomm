package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sooeez.ecomm.domain.SupplierProduct;

public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long>, JpaSpecificationExecutor<SupplierProduct> {

}
