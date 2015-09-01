package com.sooeez.ecomm.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.Inventory;
import com.sooeez.ecomm.domain.InventoryBatch;
import com.sooeez.ecomm.domain.User;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.domain.WarehousePosition;
import com.sooeez.ecomm.service.InventoryService;

@RestController
@RequestMapping("/api")
public class InventoryController {

	@Autowired private InventoryService inventoryService;

	/*
	 * Warehouse
	 */
	
	@RequestMapping(value = "/warehouses/{id}")
	public Warehouse getWarehouse(@PathVariable("id") Long id) {
		return this.inventoryService.getWarehouse(id);
	}
	
	@RequestMapping(value = "/warehouses")
	public Page<Warehouse> getPagedWarehouses(Pageable pageable) {
		return this.inventoryService.getPagedWarehouses(pageable);
	}
	
	@RequestMapping(value = "/warehouses/get/all")
	public List<Warehouse> getWarehouse() {
		return this.inventoryService.getWarehouses();
	}
	
	@RequestMapping(value = "/warehouses", method = RequestMethod.POST)
	public Warehouse saveWarehouse(@RequestBody Warehouse warehouse) {
		return this.inventoryService.saveWarehouse(warehouse);
	}
	
	@RequestMapping(value = "/warehouses/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteWarehouse(@PathVariable("id") Long id) {
		this.inventoryService.deleteWarehouse(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * WarehousePosition
	 */
	
	@RequestMapping(value = "/warehousepositions/{id}")
	public WarehousePosition getWarehousePosition(@PathVariable("id") Long id) {
		return this.inventoryService.getWarehousePosition(id);
	}
	
	@RequestMapping(value = "/warehousepositions")
	public Page<WarehousePosition> getPagedWarehousePositions(Pageable pageable) {
		return this.inventoryService.getPagedWarehousePositions(pageable);
	}
	
	@RequestMapping(value = "/warehousepositions/get/all")
	public List<WarehousePosition> getWarehousePositions() {
		return this.inventoryService.getWarehousePositions();
	}
	
	@RequestMapping(value = "/warehousepositions", method = RequestMethod.POST)
	public WarehousePosition saveWarehousePosition(@RequestBody WarehousePosition position) {
		return this.inventoryService.saveWarehousePosition(position);
	}
	
	@RequestMapping(value = "/warehousepositions/save/list", method = RequestMethod.POST)
	public List<WarehousePosition> saveWarehousePositions(@RequestBody List<WarehousePosition> positions) {
		return this.inventoryService.saveWarehousePositions(positions);
	}
	
	@RequestMapping(value = "/warehousepositions/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteWarehousePosition(@PathVariable("id") Long id) {
		this.inventoryService.deleteWarehousePosition(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * Inventory
	 */
	
	@RequestMapping(value = "/inventories/{id}")
	public Inventory getInventory(@PathVariable("id") Long id) {
		return this.inventoryService.getInventory(id);
	}
	
	@RequestMapping(value = "/inventories")
	public Page<Inventory> getPagedInventories(Pageable pageable) {
		return this.inventoryService.getPagedInventories(pageable);
	}
	
	@RequestMapping(value = "/inventories/get/all")
	public List<Inventory> getInventories() {
		return this.inventoryService.getInventories();
	}
	
	@RequestMapping(value = "/inventories/get/all/{warehouseId}")
	public List<Inventory> getInventoriesByWarehouseId(@PathVariable("warehouseId") Long id) {
		return this.inventoryService.getInventoriesByWarehouseId(id);
	}
	
	@RequestMapping(value = "/inventories", method = RequestMethod.POST)
	public Inventory saveInventory(@RequestBody Inventory inventory) {
		return this.inventoryService.saveInventory(inventory);
	}
	
	@RequestMapping(value = "/inventories/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteInventory(@PathVariable("id") Long id) {
		this.inventoryService.deleteInventory(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * InventoryBatch
	 */
	
	@RequestMapping(value = "/inventorybatches/{id}")
	public InventoryBatch getInventoryBatch(@PathVariable("id") Long id) {
		return this.inventoryService.getInventoryBatch(id);
	}
	
	@RequestMapping(value = "/inventorybatches")
	public Page<InventoryBatch> getPagedInventoryBatches(Pageable pageable) {
		return this.inventoryService.getPagedInventoryBatches(pageable);
	}
	
	@RequestMapping(value = "/inventorybatches/get/all")
	public List<InventoryBatch> getInventoryBatches() {
		return this.inventoryService.getInventoryBatches();
	}
	
	@RequestMapping(value = "/inventorybatches", method = RequestMethod.POST)
	public InventoryBatch saveInventoryBatch(@RequestBody InventoryBatch inventoryBatch) {
		return this.inventoryService.saveInventoryBatch(inventoryBatch);
	}
	
	@RequestMapping(value = "/inventorybatches/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteInventoryBatch(@PathVariable("id") Long id) {
		this.inventoryService.deleteInventoryBatch(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
