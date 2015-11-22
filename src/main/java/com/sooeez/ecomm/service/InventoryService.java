package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.Inventory;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.repository.InventoryRepository;

@Service
public class InventoryService {
	
	@PersistenceContext
	private EntityManager em;

	/*
	 * Repository
	 */
	
	@Autowired 
	private InventoryRepository inventoryRepository;

	/*
	 * Inventory
	 */

	@Transactional
	public Inventory saveInventory(Inventory inventory) {
		return inventoryRepository.save(inventory);
	}

	@Transactional
	public void deleteInventory(Long id) {
		inventoryRepository.delete(id);
	}

	public Inventory getInventory(Long id) {
		return inventoryRepository.findOne(id);
	}

	public List<Inventory> getInventories(Inventory inventory, Sort sort) {
		return inventoryRepository.findAll(getInventorySpecification(inventory), sort);
	}

	public Page<Inventory> getPagedInventories(Inventory inventory, Pageable pageable) {
		return inventoryRepository.findAll(getInventorySpecification(inventory), pageable);
	}
	
	private Specification<Inventory> getInventorySpecification(Inventory inventory) {
		
		return (root, query, cb) -> {
			
			List<Predicate> predicates = new ArrayList<>();
			
			if (inventory.getId() != null) {
				predicates.add(cb.equal(root.get("id"), inventory.getId()));
			}
			if (inventory.getProductId() != null) {
				predicates.add(cb.equal(root.get("productId"), inventory.getProductId()));
			}
			if (inventory.getWarehouseId() != null) {
				predicates.add(cb.equal(root.get("warehouseId"), inventory.getWarehouseId()));
			}
			if (inventory.getWarehouseIds() != null && inventory.getWarehouseIds().size() > 0) {
				predicates.add(cb.in(root.get("warehouseId")).value(inventory.getWarehouseIds()));
			}
			if (inventory.getProductIds() != null && inventory.getProductIds().size() > 0) {
				predicates.add(cb.in(root.get("productId")).value(inventory.getProductIds()));
			} 
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
	
	/*
	 * 把库存，变成仓库的集合,当库存只来自一个商品
	 */
	
	public List<Warehouse> refreshInventoryFormOneProduct(List<Inventory> inventories){
		System.out.println("refreshInventoryFormOneProduct(): " + inventories.size());
		
		List<Warehouse> warehouses = new ArrayList<>();
		
		// 循环当前商品库存集合
		for (Inventory inventory: inventories) {
			boolean existWarehouse = false;
			
			// 循环仓库集合
			for (Warehouse warehouse: warehouses) {
				// 如何某一个仓库等于库存条目的仓库
				if (warehouse.getId().longValue() == inventory.getWarehouseId().longValue()) {
					existWarehouse = true;
					
					// 添加当前库存到当前仓库的库存
					warehouse.setTotal(warehouse.getTotal().longValue() + inventory.getQuantity().longValue());
					
					// 把所有库存条目的保质期都列在仓库的这个属性上
					if (inventory.getExpireDate() != null) {
						warehouse.getExpireDates().add(inventory.getExpireDate());
					}
					
					break;
				}
			}
			
			// 如何某仓库不在仓库集合中
			if (!existWarehouse) {
				
				// 创建一个新的仓库
				Warehouse warehouse = new Warehouse();
				BeanUtils.copyProperties(inventory.getWarehouse(), warehouse);
				
				// 设置新仓库的库存
				warehouse.setTotal(inventory.getQuantity().longValue());
				
				// 把所有库存条目的保质期都列在仓库的这个属性上
				warehouse.setExpireDates(new ArrayList<>());
				if (inventory.getExpireDate() != null) {
					warehouse.getExpireDates().add(inventory.getExpireDate());
				}
				
				// 把新仓库添加进仓库集合
				warehouses.add(warehouse);
			}
		}
		return warehouses;
	}
	
	public Long countAsInventory(Pageable pageable) {
		
		Long count = inventoryRepository.countAsInventory();
		
		System.out.println("countAsInventory(): " + inventoryRepository.countAsInventory());
		
		return count;
	}
	
}
