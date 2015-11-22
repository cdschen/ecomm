package com.sooeez.ecomm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sooeez.ecomm.domain.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {

	@Query(value = "select count(*) from (select product_id from t_inventory GROUP BY product_id order by product_id) res", nativeQuery = true)
	Long countAsInventory();

	List<Inventory> findAllByWarehouseId(Long id);

	@Modifying
	@Query("update Inventory set quantity = quantity + ?1 where productId = ?2 and warehouseId = ?3 and warehousePositionId = ?4 and inventoryBatchId = ?5")
	void updateInventoryByProductIdAndWarehouseIdAndPositionIdAndBatchId(
			Long quantity, Long productId, Long warehouseId,
			Long warehousePositionId, Long inventoryBatchId);

	@Modifying
	@Query("delete from Inventory where quantity = 0")
	void deleteByQuantity();
}
