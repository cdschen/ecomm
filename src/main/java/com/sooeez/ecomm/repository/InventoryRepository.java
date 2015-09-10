package com.sooeez.ecomm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sooeez.ecomm.domain.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {
	
	List<Inventory> findAllByWarehouseId(Long id);
//	
//	Inventory findFirstByWarehousePositionIdAndInventoryBatchId(Long warehousePositionId, Long inventoryBatchId);
//	
//	Inventory findFirstByWarehousePositionId(Long warehousePositionId); 
//	
//	Inventory findFirstByInventoryBatchId(Long inventoryBatchId);
//	
//	Inventory findFirstByWarehouseIdAndProductId(Long warehouseId, Long productId); 

}
