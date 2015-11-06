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
import com.sooeez.ecomm.domain.InventoryBatchItem;
import com.sooeez.ecomm.domain.Shipment;
import com.sooeez.ecomm.domain.ShipmentItem;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.domain.WarehousePosition;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.dto.OperationReviewShipmentDTO;
import com.sooeez.ecomm.service.CourierService;
import com.sooeez.ecomm.service.InventoryBatchItemService;
import com.sooeez.ecomm.service.InventoryBatchService;
import com.sooeez.ecomm.service.InventoryService;
import com.sooeez.ecomm.service.ShipmentService;
import com.sooeez.ecomm.service.WarehouseService;

@RestController
@RequestMapping("/api")
public class InventoryController {
	
	/*
	 * Service
	 */

	@Autowired 
	private InventoryService inventoryService;
	
	@Autowired 
	private InventoryBatchService batchService;
	
	@Autowired 
	private InventoryBatchItemService batchItemService;
	
	@Autowired 
	private CourierService courierService;
	
	@Autowired 
	private ShipmentService shipmentService;
	
	@Autowired 
	private WarehouseService warehouseService;
	
	/*
	 * Inventory
	 */
	
	@RequestMapping(value = "/inventories/{id}")
	public Inventory getInventory(@PathVariable("id") Long id) {
		return this.inventoryService.getInventory(id);
	}
	
	@RequestMapping(value = "/inventories")
	public Page<Inventory> getPagedInventories(Inventory inventory, Pageable pageable) {
		return this.inventoryService.getPagedInventories(inventory, pageable);
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
		return this.batchService.createOutInventorySheet(review);
	}
	
	/*
	 * InventoryBatch
	 */
	
	@RequestMapping(value = "/inventory-batches/{id}")
	public InventoryBatch getBatch(@PathVariable("id") Long id) {
		return this.batchService.getBatch(id);
	}
	
	@RequestMapping(value = "/inventory-batches")
	public Page<InventoryBatch> getPagedBatches(InventoryBatch batch, Pageable pageable) {
		return this.batchService.getPagedBatches(batch, pageable);
	}
	
	@RequestMapping(value = "/inventory-batches/get/all")
	public List<InventoryBatch> getBatches(InventoryBatch batch, Sort sort) {
		return this.batchService.getBatches(batch, sort);
	}
	
	@RequestMapping(value = "/inventory-batches", method = RequestMethod.POST)
	public InventoryBatch saveBatch(@RequestBody InventoryBatch inventoryBatch) {
		return this.batchService.saveInventoryBatch(inventoryBatch);
	}
	
	@RequestMapping(value = "/inventory-batches/save/list", method = RequestMethod.POST)
	public List<InventoryBatch> saveBatches(@RequestBody List<InventoryBatch> inventoryBatches) {
		return this.batchService.saveInventoryBatches(inventoryBatches);
	}
	
	@RequestMapping(value = "/inventory-batches/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteBatch(@PathVariable("id") Long id) {
		this.batchService.deleteBatch(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * InventoryBatchItem
	 */
	
	@RequestMapping(value = "/inventory-batch-items/{id}")
	public InventoryBatchItem getBatchItem(@PathVariable("id") Long id) {
		return this.batchItemService.getBatchItem(id);
	}
	
	@RequestMapping(value = "/inventory-batch-items")
	public Page<InventoryBatchItem> getPagedInventoryBatchItems(InventoryBatchItem inventoryBatchItem, Pageable pageable) {
		return this.batchItemService.getPagedBatchItems(inventoryBatchItem, pageable);
	}
	
	@RequestMapping(value = "/inventory-batch-items/get/all")
	public List<InventoryBatchItem> getInventoryBatchItems(InventoryBatchItem inventoryBatchItem, Sort sort) {
		return this.batchItemService.getBatchItems(inventoryBatchItem, sort);
	}
	
	@RequestMapping(value = "/inventory-batch-items", method = RequestMethod.POST)
	public InventoryBatchItem saveInventoryBatchItem(@RequestBody InventoryBatchItem inventoryBatchItem) {
		return this.batchItemService.saveBatchItem(inventoryBatchItem);
	}
	
	@RequestMapping(value = "/inventory-batch-items/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteInventoryBatchItem(@PathVariable("id") Long id) {
		this.batchItemService.deleteBatchItem(id);
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
	
	@RequestMapping(value = "/saveShipments", method = RequestMethod.POST)
	public List<Shipment> saveShipments(@RequestBody Shipment shipments) {
		return this.shipmentService.saveShipments( shipments );
	}
	
	@RequestMapping(value = "/shipments/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteShipment(@PathVariable("id") Long id) {
		this.shipmentService.deleteShipment(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/shipments/confirm/complete/operation-review")
	public OperationReviewShipmentDTO confirmOrderWhenGenerateShipment(@RequestBody OperationReviewShipmentDTO review) {
		return this.shipmentService.confirmOperationReviewWhenCompleteShipment(review);
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
