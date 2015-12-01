package com.sooeez.ecomm.controller;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.BeanUtils;
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
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.dto.OperationReviewShipmentDTO;
import com.sooeez.ecomm.dto.PageDTO;
import com.sooeez.ecomm.dto.inventory.InventoryBatchDTO;
import com.sooeez.ecomm.dto.inventory.InventoryDTO;
import com.sooeez.ecomm.dto.inventory.InventoryPositionDTO;
import com.sooeez.ecomm.dto.inventory.InventoryProductDTO;
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
		return inventoryService.getInventory(id);
	}
	
	@RequestMapping(value = "/inventories")
	public PageDTO<InventoryDTO> getPagedInventories(Inventory inventory, Pageable pageable) {
		PageDTO<InventoryDTO> page = new PageDTO<>();
		page.setNumber(pageable.getPageNumber());
		page.setSize(pageable.getPageSize());
		page.setTotalElements(inventoryService.countAsInventory(inventory, pageable));
		List<InventoryDTO> inventoriesDTO = new ArrayList<>();
		inventoryService.getPagedInventoriesReturnList(inventory, pageable).forEach(inve -> {
			InventoryDTO inventoryDTO = new InventoryDTO();
			
			InventoryProductDTO productDTO = new InventoryProductDTO();
			productDTO.setId(inve.getProduct().getId());
			productDTO.setSku(inve.getProduct().getSku());
			productDTO.setName(inve.getProduct().getName());
			inventoryDTO.setProduct(productDTO);
			
			InventoryPositionDTO positionDTO = new InventoryPositionDTO();
			positionDTO.setId(inve.getPosition().getId());
			positionDTO.setName(inve.getPosition().getName());
			inventoryDTO.setPosition(positionDTO);
			
			InventoryBatchDTO batchDTO = new InventoryBatchDTO();
			batchDTO.setId(inve.getBatch().getId());
			batchDTO.setPurchaseOrderId(inve.getBatch().getPurchaseOrderId());
			batchDTO.setReceiveId(inve.getBatch().getReceiveId());
			inventoryDTO.setBatch(batchDTO);
			
			inventoryDTO.setId(inve.getId());
			inventoryDTO.setInventoryBatchId(inve.getInventoryBatchId());
			inventoryDTO.setQuantity(inve.getQuantity());
			inventoryDTO.setExpireDate(inve.getExpireDate());
			
			inventoriesDTO.add(inventoryDTO);
		});
		page.setContent(inventoriesDTO);
		return page;
	}
	
	@RequestMapping(value = "/inventories/get/all")
	public List<InventoryDTO> getInventories(Inventory inventory, Sort sort) {
		
		List<InventoryDTO> inventoriesDTO = new ArrayList<>();
		inventoryService.getInventories(inventory, sort).forEach(inve -> {
			InventoryDTO inventoryDTO = new InventoryDTO();
			
			InventoryProductDTO productDTO = new InventoryProductDTO();
			productDTO.setId(inve.getProduct().getId());
			productDTO.setSku(inve.getProduct().getSku());
			productDTO.setName(inve.getProduct().getName());
			inventoryDTO.setProduct(productDTO);
			
			InventoryPositionDTO positionDTO = new InventoryPositionDTO();
			positionDTO.setId(inve.getPosition().getId());
			positionDTO.setName(inve.getPosition().getName());
			inventoryDTO.setPosition(positionDTO);
			
			InventoryBatchDTO batchDTO = new InventoryBatchDTO();
			batchDTO.setId(inve.getBatch().getId());
			batchDTO.setPurchaseOrderId(inve.getBatch().getPurchaseOrderId());
			batchDTO.setReceiveId(inve.getBatch().getReceiveId());
			inventoryDTO.setBatch(batchDTO);
			
			inventoryDTO.setId(inve.getId());
			inventoryDTO.setInventoryBatchId(inve.getInventoryBatchId());
			inventoryDTO.setQuantity(inve.getQuantity());
			inventoryDTO.setExpireDate(inve.getExpireDate());
			
			inventoriesDTO.add(inventoryDTO);
		});
		
		return inventoriesDTO;
	}
	
	@RequestMapping(value = "/inventories", method = RequestMethod.POST)
	public Inventory saveInventory(@RequestBody Inventory inventory) {
		return inventoryService.saveInventory(inventory);
	}
	
	@RequestMapping(value = "/inventories/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteInventory(@PathVariable("id") Long id) {
		inventoryService.deleteInventory(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/inventories/outinventorysheet/create", method = RequestMethod.POST)
	public OperationReviewDTO createOutInventorySheet(@RequestBody OperationReviewDTO review) {
		return batchService.createOutInventorySheet(review);
	}
	
	/*
	 * InventoryBatch
	 */
	
	@RequestMapping(value = "/inventory-batches/{id}")
	public InventoryBatch getBatch(@PathVariable("id") Long id) {
		InventoryBatch batch = batchService.getBatch(id);
		return batch;
	}
	
	@RequestMapping(value = "/inventory-batches")
	public PageDTO<InventoryBatch> getPagedBatches(InventoryBatch batch, Pageable pageable) {
		Page<InventoryBatch> page = batchService.getPagedBatches(batch, pageable);
		PageDTO<InventoryBatch> pageDTO = new PageDTO<>();
		BeanUtils.copyProperties(page, pageDTO, "content", "sort");
		List<InventoryBatch> bList = new ArrayList<>();
		page.forEach(b -> {
			InventoryBatch batchDTO = new InventoryBatch();
//			batchDTO.setId(b.getId().longValue());
//			batchDTO.setType(b.getType().intValue());
//			batchDTO.setOperate(b.getOperate().intValue());
//			batchDTO.setUserId(b.getUserId().longValue());
//			batchDTO.setExecuteOperatorId(b.getExecuteOperatorId().longValue());
//			batchDTO.setOperateTime(b.getOperateTime());
//			batchDTO.setOutInventoryTime(b.getOutInventoryTime());
			BeanUtils.copyProperties(b, batchDTO, "items", "orderBatches");
			bList.add(batchDTO);
		});
		pageDTO.setContent(bList);
		return pageDTO;
	}
	
	@RequestMapping(value = "/inventory-batches/get/all")
	public List<InventoryBatch> getBatches(InventoryBatch batch, Sort sort) {
		return batchService.getBatches(batch, sort);
	}
	
	@RequestMapping(value = "/inventory-batches", method = RequestMethod.POST)
	public InventoryBatch saveBatch(@RequestBody InventoryBatch batch) {
		return batchService.saveInventoryBatch(batch);
	}
	
	@RequestMapping(value = "/inventory-batches/save/list", method = RequestMethod.POST)
	public List<InventoryBatch> saveBatches(@RequestBody List<InventoryBatch> batches) {
		return batchService.saveInventoryBatches(batches);
	}
	
	@RequestMapping(value = "/inventory-batches/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteBatch(@PathVariable("id") Long id) {
		batchService.deleteBatch(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/inventory-batches/trash", method = RequestMethod.POST)
	public ResponseEntity<?> trashBatch(@RequestBody InventoryBatch batch) {
		batchService.trashBatch(batch);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * InventoryBatchItem
	 */
	
	@RequestMapping(value = "/inventory-batch-items/{id}")
	public InventoryBatchItem getBatchItem(@PathVariable("id") Long id) {
		return batchItemService.getBatchItem(id);
	}
	
	@RequestMapping(value = "/inventory-batch-items")
	public Page<InventoryBatchItem> getPagedInventoryBatchItems(InventoryBatchItem inventoryBatchItem, Pageable pageable) {
		return batchItemService.getPagedBatchItems(inventoryBatchItem, pageable);
	}
	
	@RequestMapping(value = "/inventory-batch-items/get/all")
	public List<InventoryBatchItem> getInventoryBatchItems(InventoryBatchItem inventoryBatchItem, Sort sort) {
		return batchItemService.getBatchItems(inventoryBatchItem, sort);
	}
	
	@RequestMapping(value = "/inventory-batch-items", method = RequestMethod.POST)
	public InventoryBatchItem saveInventoryBatchItem(@RequestBody InventoryBatchItem inventoryBatchItem) {
		return batchItemService.saveBatchItem(inventoryBatchItem);
	}
	
	@RequestMapping(value = "/inventory-batch-items/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteInventoryBatchItem(@PathVariable("id") Long id) {
		batchItemService.deleteBatchItem(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * Courier
	 */
	
	@RequestMapping(value = "/couriers/{id}")
	public Courier getCouerirs(@PathVariable("id") Long id) {
		return courierService.getCourier(id);
	}
	
	@RequestMapping(value = "/couriers")
	public Page<Courier> getPagedCouriers(Pageable pageable) {
		return courierService.getPagedCouriers(pageable);
	}
	
	@RequestMapping(value = "/couriers/get/all")
	public List<Courier> getCouriers() {
		return courierService.getCouriers();
	}
	
	@RequestMapping(value = "/couriers", method = RequestMethod.POST)
	public Courier saveCourier(@RequestBody Courier courier) {
		return courierService.saveCourier(courier);
	}
	
	@RequestMapping(value = "/couriers/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCourier(@PathVariable("id") Long id) {
		courierService.deleteCourier(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * Shipment
	 */
	
	@RequestMapping(value = "/shipments/{id}")
	public Shipment getShipments(@PathVariable("id") Long id) {
		return shipmentService.getShipment(id);
	}
	
	@RequestMapping(value = "/shipments")
	public Page<Shipment> getPagedShipments(Shipment shipment, Pageable pageable) {
		return shipmentService.getPagedShipments(shipment, pageable);
	}
	
	@RequestMapping(value = "/shipments/get/all")
	public List<Shipment> getShipments() {
		return shipmentService.getShipments();
	}
	
	@RequestMapping(value = "/shipments", method = RequestMethod.POST)
	public Shipment saveShipment(@RequestBody Shipment shipment) {
		return shipmentService.saveShipment(shipment);
	}
	
	@RequestMapping(value = "/saveShipments", method = RequestMethod.POST)
	public List<Shipment> saveShipments(@RequestBody Shipment shipments) {
		return shipmentService.saveShipments( shipments );
	}
	
	@RequestMapping(value = "/shipments/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteShipment(@PathVariable("id") Long id) {
		shipmentService.deleteShipment(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/shipment/confirm/complete/operation-review")
	public OperationReviewShipmentDTO confirmOrderWhenGenerateShipment(@RequestBody OperationReviewShipmentDTO review) {
		return shipmentService.confirmOperationReviewWhenCompleteShipment(review);
	}
	
	@RequestMapping(value = "/shipments/confirm/complete/operation-review")
	public OperationReviewShipmentDTO confirmOrderWhenGenerateShipments(@RequestBody OperationReviewShipmentDTO review) {
		return shipmentService.confirmOperationReviewWhenCompleteShipments(review);
	}
	
	@RequestMapping(value = "/shipments/confirm/import/operation-review")
	public OperationReviewShipmentDTO confirmOrderWhenImportShipments( @RequestBody OperationReviewShipmentDTO review )
	{
		return shipmentService.confirmOperationReviewWhenImportShipments( review );
	}
	
	/*
	 * ShipmentItem
	 */
	
	@RequestMapping(value = "/shipmentitems/{id}")
	public ShipmentItem getShipmentItems(@PathVariable("id") Long id) {
		return shipmentService.getShipmentItem(id);
	}
	
	@RequestMapping(value = "/shipmentitems")
	public Page<ShipmentItem> getPagedShipmentItems(Pageable pageable) {
		return shipmentService.getPagedShipmentItems(pageable);
	}
	
	@RequestMapping(value = "/shipmentitems/get/all")
	public List<ShipmentItem> getShipmentItems() {
		return shipmentService.getShipmentItems();
	}
	
	@RequestMapping(value = "/shipmentitems", method = RequestMethod.POST)
	public ShipmentItem saveShipmentItem(@RequestBody ShipmentItem shipmentItem) {
		return shipmentService.saveShipmentItem(shipmentItem);
	}
	
	@RequestMapping(value = "/shipmentitems/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteShipmentItem(@PathVariable("id") Long id) {
		shipmentService.deleteShipmentItem(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
