package com.sooeez.ecomm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.Courier;
import com.sooeez.ecomm.domain.Inventory;
import com.sooeez.ecomm.domain.InventoryBatch;
import com.sooeez.ecomm.domain.Shipment;
import com.sooeez.ecomm.domain.ShipmentItem;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.domain.WarehousePosition;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.service.CourierService;
import com.sooeez.ecomm.service.InventoryService;
import com.sooeez.ecomm.service.ShipmentService;

@RestController
@RequestMapping("/api")
public class InventoryController {

	@Autowired private InventoryService inventoryService;
	@Autowired private CourierService courierService;
	@Autowired private ShipmentService shipmentService;

	/*
	 * Warehouse
	 */
	
	@RequestMapping(value = "/warehouses/{id}")
	public Warehouse getWarehouse(@PathVariable("id") Long id) {
		return this.inventoryService.getWarehouse(id);
	}
	
	@RequestMapping(value = "/warehouses")
	public Page<Warehouse> getPagedWarehouses(Warehouse warehouse, Pageable pageable) {
		return this.inventoryService.getPagedWarehouses(warehouse, pageable);
	}
	
	@RequestMapping(value = "/warehouses/get/all")
	public List<Warehouse> getWarehouse(Warehouse warehouse, Sort sort) {
		return this.inventoryService.getWarehouses(warehouse, sort);
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
	public List<Inventory> getInventories(Inventory inventory, Sort sort) {
		return this.inventoryService.getInventories(inventory, sort);
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
	
	@RequestMapping(value = "/inventories/outinventorysheet/create", method = RequestMethod.POST)
	public OperationReviewDTO createOutInventorySheet(@RequestBody OperationReviewDTO review) {
		return this.inventoryService.createOutInventorySheet(review);
	}
	
	/*
	 * InventoryBatch
	 */
	
	@RequestMapping(value = "/inventory-batches/{id}")
	public InventoryBatch getInventoryBatch(@PathVariable("id") Long id) {
		return this.inventoryService.getInventoryBatch(id);
	}
	
	@RequestMapping(value = "/inventory-batches")
	public Page<InventoryBatch> getPagedInventoryBatches(InventoryBatch batch, Pageable pageable) {
		return this.inventoryService.getPagedInventoryBatches(batch, pageable);
	}
	
	@RequestMapping(value = "/inventory-batches/get/all")
	public List<InventoryBatch> getInventoryBatches(InventoryBatch batch, Sort sort) {
		return this.inventoryService.getInventoryBatches(batch, sort);
	}
	
	@RequestMapping(value = "/inventory-batches", method = RequestMethod.POST)
	public InventoryBatch saveInventoryBatch(@RequestBody InventoryBatch inventoryBatch) {
		return this.inventoryService.saveInventoryBatch(inventoryBatch);
	}
	
	@RequestMapping(value = "/inventory-batches/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteInventoryBatch(@PathVariable("id") Long id) {
		this.inventoryService.deleteInventoryBatch(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * Courier
	 */
	
	@RequestMapping(value = "/couriers/{id}")
	public Courier getCouerirs(@PathVariable("id") Long id) {
		return this.courierService.getCourier(id);
	}
	
	@RequestMapping(value = "/couriers")
	public Page<Courier> getPagedCouriers(Pageable pageable) {
		return this.courierService.getPagedCouriers(pageable);
	}
	
	@RequestMapping(value = "/couriers/get/all")
	public List<Courier> getCouriers() {
		return this.courierService.getCouriers();
	}
	
	@RequestMapping(value = "/couriers", method = RequestMethod.POST)
	public Courier saveCourier(@RequestBody Courier courier) {
		return this.courierService.saveCourier(courier);
	}
	
	@RequestMapping(value = "/couriers/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCourier(@PathVariable("id") Long id) {
		this.courierService.deleteCourier(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * Shipment
	 */
	
	@RequestMapping(value = "/shipments/{id}")
	public Shipment getShipments(@PathVariable("id") Long id) {
		return this.shipmentService.getShipment(id);
	}
	
	@RequestMapping(value = "/shipments")
	public Page<Shipment> getPagedShipments(Shipment shipment, Pageable pageable) {
		return this.shipmentService.getPagedShipments(shipment, pageable);
	}
	
	@RequestMapping(value = "/shipments/get/all")
	public List<Shipment> getShipments() {
		return this.shipmentService.getShipments();
	}
	
	@RequestMapping(value = "/shipments", method = RequestMethod.POST)
	public Shipment saveShipment(@RequestBody Shipment shipment) {
		return this.shipmentService.saveShipment(shipment);
	}
	
	@RequestMapping(value = "/shipments/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteShipment(@PathVariable("id") Long id) {
		this.shipmentService.deleteShipment(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * ShipmentItem
	 */
	
	@RequestMapping(value = "/shipmentitems/{id}")
	public ShipmentItem getShipmentItems(@PathVariable("id") Long id) {
		return this.shipmentService.getShipmentItem(id);
	}
	
	@RequestMapping(value = "/shipmentitems")
	public Page<ShipmentItem> getPagedShipmentItems(Pageable pageable) {
		return this.shipmentService.getPagedShipmentItems(pageable);
	}
	
	@RequestMapping(value = "/shipmentitems/get/all")
	public List<ShipmentItem> getShipmentItems() {
		return this.shipmentService.getShipmentItems();
	}
	
	@RequestMapping(value = "/shipmentitems", method = RequestMethod.POST)
	public ShipmentItem saveShipmentItem(@RequestBody ShipmentItem shipmentItem) {
		return this.shipmentService.saveShipmentItem(shipmentItem);
	}
	
	@RequestMapping(value = "/shipmentitems/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteShipmentItem(@PathVariable("id") Long id) {
		this.shipmentService.deleteShipmentItem(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
