package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.Inventory;
import com.sooeez.ecomm.repository.InventoryRepository;

@Service
public class InventoryService {

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
			if (inventory.getWarehouseId() != null) {
				predicates.add(cb.equal(root.get("warehouseId"), inventory.getWarehouseId()));
			}
			if (inventory.getWarehouseIds() != null && inventory.getWarehouseIds().size() > 0) {
				predicates.add(cb.in(root.get("warehouseId")).value(inventory.getWarehouseIds()));
			}
			
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
	
}
