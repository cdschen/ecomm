package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.Inventory;
import com.sooeez.ecomm.domain.InventoryBatch;
import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.domain.WarehousePosition;
import com.sooeez.ecomm.dto.ProductDTO;
import com.sooeez.ecomm.repository.InventoryBatchRepository;
import com.sooeez.ecomm.repository.InventoryRepository;
import com.sooeez.ecomm.repository.WarehousePositionRepository;
import com.sooeez.ecomm.repository.WarehouseRepository;

@Service
public class InventoryService {

	@Autowired private WarehouseRepository warehouseRepository;

	@Autowired private WarehousePositionRepository warehousePositionRepository;

	@Autowired private InventoryRepository inventoryRepository;

	@Autowired private InventoryBatchRepository inventoryBatchRepository;

	/*
	 * Warehouse
	 */

	@Transactional
	public Warehouse saveWarehouse(Warehouse warehouse) {
		return this.warehouseRepository.save(warehouse);
	}

	@Transactional
	public void deleteWarehouse(Long id) {
		this.warehouseRepository.delete(id);
	}

	public Warehouse getWarehouse(Long id) {
		return this.warehouseRepository.findOne(id);
	}

	public List<Warehouse> getWarehouses(Warehouse warehouse, Sort sort) {
		return this.warehouseRepository.findAll(getWarehouseSpecification(warehouse), sort);
	}

	public Page<Warehouse> getPagedWarehouses(Warehouse warehouse, Pageable pageable) {
		return this.warehouseRepository.findAll(getWarehouseSpecification(warehouse), pageable);
	}
	
	private Specification<Warehouse> getWarehouseSpecification(Warehouse warehouse) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("deleted"), warehouse.getDeleted() != null && warehouse.getDeleted() == true ? true : false));
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	/*
	 * WarehousePosition
	 */

	@Transactional
	public WarehousePosition saveWarehousePosition(WarehousePosition warehousePosition) {
		return this.warehousePositionRepository.save(warehousePosition);
	}

	@Transactional
	public List<WarehousePosition> saveWarehousePositions(List<WarehousePosition> positions) {
		return this.warehousePositionRepository.save(positions);
	}

	@Transactional
	public void deleteWarehousePosition(Long id) {
		this.warehousePositionRepository.delete(id);
	}

	public WarehousePosition getWarehousePosition(Long id) {
		return this.warehousePositionRepository.findOne(id);
	}

	public List<WarehousePosition> getWarehousePositions() {
		return this.warehousePositionRepository.findAll();
	}

	public Page<WarehousePosition> getPagedWarehousePositions(Pageable pageable) {
		return this.warehousePositionRepository.findAll(pageable);
	}

	/*
	 * Inventory
	 */

	@Transactional
	public Inventory saveInventory(Inventory inventory) {
		return this.inventoryRepository.save(inventory);
	}

	@Transactional
	public void deleteInventory(Long id) {
		this.inventoryRepository.delete(id);
	}

	public Inventory getInventory(Long id) {
		return this.inventoryRepository.findOne(id);
	}

	public List<Inventory> getInventories(Inventory inventory, Sort sort) {
		return this.inventoryRepository.findAll(getInventorySpecification(inventory), sort);
	}
	
	//public List<Inventory> getInventories()

	public Page<Inventory> getPagedInventories(Pageable pageable) {
		return this.inventoryRepository.findAll(pageable);
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
			
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	/*
	 * InventoryBatch
	 */

	@Transactional
	public InventoryBatch saveInventoryBatch(InventoryBatch batch) {
		
		batch.setOperateTime(new Date(System.currentTimeMillis()));

		if (batch.getOperate() == 1) { // 正常入库
			
			this.inventoryBatchRepository.save(batch);

			batch.getItems().forEach(item -> {

				Inventory inventory = new Inventory();
				inventory.setProduct(item.getProduct());
				inventory.setWarehouseId(item.getWarehouse().getId());
				inventory.setPosition(item.getPosition());
				inventory.setInventoryBatchId(batch.getId());
				inventory.setQuantity(item.getChangedQuantity());
				inventory.setExpireDate(item.getExpireDate());

				this.inventoryRepository.save(inventory);
			});

		} else if (batch.getOperate() == 2) { // 正常出库
			
			batch.getItems().forEach(item -> {
				item.setChangedQuantity(-item.getChangedQuantity());
			});

			
//			
//			this.inventoryBatchRepository.save(batch);
//


		} else if (batch.getOperate() == 3) { // 调整入库

		} else if (batch.getOperate() == 4) { // 调整出库

		}

		return batch;
	}

	@Transactional
	public void deleteInventoryBatch(Long id) {
		this.inventoryBatchRepository.delete(id);
	}

	public InventoryBatch getInventoryBatch(Long id) {
		return this.inventoryBatchRepository.findOne(id);
	}

	public List<InventoryBatch> getInventoryBatches() {
		return this.inventoryBatchRepository.findAll();
	}

	public Page<InventoryBatch> getPagedInventoryBatches(Pageable pageable) {
		return this.inventoryBatchRepository.findAll(pageable);
	}
	
	public List<Product> refreshInventory(List<Inventory> inventories) {
		List<Product> products = new ArrayList<>();
		for (Inventory inventory: inventories) {
			boolean existInventory = false;
			for (Product product: products) {
				if (product.getSku().equals(inventory.getProduct().getSku())) {
					product.setTotal(product.getTotal().longValue() + inventory.getQuantity().longValue());
					break;
				}
			}
			if (!existInventory) {
				inventory.getProduct().setTotal(inventory.getQuantity().longValue());
				products.add(inventory.getProduct());
			}
		}
		return products;
	}
}
