package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sooeez.ecomm.domain.OrderBatch;

public interface OrderBatchRepository extends JpaRepository<OrderBatch, Long> {
	
	@Modifying
	@Query("delete from OrderBatch where batchId = ?1")
	void deleteOrderBatchByBatchId(Long batchId);
}
