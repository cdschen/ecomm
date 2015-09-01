package com.sooeez.ecomm.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.Inventory;
import com.sooeez.ecomm.domain.InventoryBatch;
import com.sooeez.ecomm.domain.InventoryBatchItem;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.domain.WarehousePosition;
import com.sooeez.ecomm.repository.InventoryBatchRepository;
import com.sooeez.ecomm.repository.InventoryRepository;
import com.sooeez.ecomm.repository.WarehousePositionRepository;
import com.sooeez.ecomm.repository.WarehouseRepository;

@Service
public class InventoryService {

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private WarehousePositionRepository warehousePositionRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private InventoryBatchRepository inventoryBatchRepository;

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

	public List<Warehouse> getWarehouses() {
		return this.warehouseRepository.findAll();
	}

	public Page<Warehouse> getPagedWarehouses(Pageable pageable) {
		return this.warehouseRepository.findAll(pageable);
	}

	/*
	 * WarehousePosition
	 */

	@Transactional
	public WarehousePosition saveWarehousePosition(
			WarehousePosition warehousePosition) {
		return this.warehousePositionRepository.save(warehousePosition);
	}

	@Transactional
	public List<WarehousePosition> saveWarehousePositions(
			List<WarehousePosition> positions) {
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

	public List<Inventory> getInventories() {
		return this.inventoryRepository.findAll();
	}

	public List<Inventory> getInventoriesByWarehouseId(Long id) {
		List<Inventory> inventories = this.inventoryRepository.findAllByWarehouseId(id);
		return inventories;
	}

	public Page<Inventory> getPagedInventories(Pageable pageable) {
		return this.inventoryRepository.findAll(pageable);
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
				inventory.setInventoryBatchId(batch.getId());

				Product prodcut = new Product();
				prodcut.setId(item.getProduct().getId());

				inventory.setProduct(prodcut);

				inventory.setWarehouseId(batch.getWarehouseId());

				if (item.getPosition() != null) {
					WarehousePosition position = new WarehousePosition();
					position.setId(item.getPosition().getId());
					inventory.setPosition(position);
				}

				inventory.setQuantity(item.getChangedQuantity());
				inventory.setExpireDate(item.getExpireDate());

				this.inventoryRepository.save(inventory);
			});

		} else if (batch.getOperate() == 2) { // 正常出库
			
			batch.getItems().forEach(item -> {
				item.setChangedQuantity(-item.getChangedQuantity());
			});
			
			this.inventoryBatchRepository.save(batch);

			batch.getItems().forEach(item -> {
				
				Inventory inventory = null;
				
				if (item.getPosition() != null) {
					if (item.getOutBatch() != null) {
						inventory = this.inventoryRepository.findFirstByWarehousePositionIdAndInventoryBatchId(item.getPosition().getId(), item.getOutBatch().getId());
						inventory.setQuantity(inventory.getQuantity() + item.getChangedQuantity());
					} else {
						inventory = this.inventoryRepository.findFirstByWarehousePositionId(item.getPosition().getId());
						inventory.setQuantity(inventory.getQuantity() + item.getChangedQuantity());
					}
				} else {
					if (item.getOutBatch() != null) {
						inventory = this.inventoryRepository.findFirstByInventoryBatchId(item.getOutBatch().getId());
						inventory.setQuantity(inventory.getQuantity() + item.getChangedQuantity());
					} else {
						inventory = this.inventoryRepository.findFirstByWarehouseIdAndProductId(item.getWarehouseId(), item.getProduct().getId());
						inventory.setQuantity(inventory.getQuantity() + item.getChangedQuantity());
					}
				}
				
				if (inventory != null) {
					this.inventoryRepository.save(inventory);
				}
			});

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
}
