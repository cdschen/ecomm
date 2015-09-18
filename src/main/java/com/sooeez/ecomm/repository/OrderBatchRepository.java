package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sooeez.ecomm.domain.OrderBatch;

public interface OrderBatchRepository extends JpaRepository<OrderBatch, Long> {
	
}
