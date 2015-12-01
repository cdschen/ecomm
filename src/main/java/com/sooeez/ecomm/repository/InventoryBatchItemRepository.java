package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sooeez.ecomm.domain.InventoryBatchItem;

public interface InventoryBatchItemRepository extends JpaRepository<InventoryBatchItem, Long>, JpaSpecificationExecutor<InventoryBatchItem>  {

	@Modifying
	@Query("update InventoryBatchItem set batchType = ?1 where inventoryBatchId = ?2")
	void updateBatchTypeByBatchId(Integer batchType, Long batchId);
}
