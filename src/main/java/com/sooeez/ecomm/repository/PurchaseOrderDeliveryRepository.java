package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sooeez.ecomm.domain.PurchaseOrderDelivery;

public interface PurchaseOrderDeliveryRepository extends JpaRepository<PurchaseOrderDelivery, Long>, JpaSpecificationExecutor<PurchaseOrderDelivery> {

	@Query( "UPDATE PurchaseOrderDelivery SET status = ?1 WHERE id = ?2" )
	@Modifying
	void updateStatus( Integer status, Long id );
	
}
