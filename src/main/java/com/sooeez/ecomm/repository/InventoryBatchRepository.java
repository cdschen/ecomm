package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sooeez.ecomm.domain.InventoryBatch;

public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long>, JpaSpecificationExecutor<InventoryBatch> {

	@Modifying
	@Query("update InventoryBatch set type = ?1 where id = ?2")
	void updateType(Integer type, Long id);
}
