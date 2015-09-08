package com.sooeez.ecomm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sooeez.ecomm.domain.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	
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
