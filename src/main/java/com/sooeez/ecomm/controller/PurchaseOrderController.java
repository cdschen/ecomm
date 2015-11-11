package com.sooeez.ecomm.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.PurchaseOrder;
import com.sooeez.ecomm.domain.PurchaseOrderItem;
import com.sooeez.ecomm.domain.SupplierProductCodeMap;
import com.sooeez.ecomm.service.PurchaseOrderService;

@RestController
@RequestMapping("/api")
public class PurchaseOrderController {
	
	@Autowired private PurchaseOrderService purchaseOrderService;
	
	/*
	 * PurchaseOrder
	 */
	
	@RequestMapping(value = "/purchaseorders/{id}")
	public PurchaseOrder getPurchaseOrder(@PathVariable("id") Long id) {
		return this.purchaseOrderService.getPurchaseOrder(id);
	}
	
	@RequestMapping(value = "/purchaseorders")
	public Page<PurchaseOrder> getPagedPurchaseOrders(PurchaseOrder purchaseOrder, Pageable pageable) {
		return this.purchaseOrderService.getPagedPurchaseOrders(purchaseOrder, pageable);
	}
	
//	@RequestMapping(value = "/purchaseOrders/confirm/shipment")
//	public OperationReviewDTO confirmOrderWhenGenerateShipment(@RequestBody OperationReviewDTO review) {
//		return this.orderService.confirmOrderWhenGenerateShipment(review);
//	}
	
	@RequestMapping(value = "/purchaseorders/get/all")
	public List<PurchaseOrder> getPurchaseOrders(PurchaseOrder purchaseOrder, Sort sort) {
		return this.purchaseOrderService.getPurchaseOrders(purchaseOrder, sort);
	}
	
	@RequestMapping(value = "/purchaseorders", method = RequestMethod.POST)
	public PurchaseOrder savePurchaseOrder(@RequestBody PurchaseOrder purchaseOrder, @RequestParam String action, HttpServletRequest request) {
		return this.purchaseOrderService.savePurchaseOrder(purchaseOrder);
	}
	
	@RequestMapping(value = "/savePurchaseOrders", method = RequestMethod.POST)
	public List<PurchaseOrder> savePurchaseOrders(@RequestBody PurchaseOrder purchaseOrders) {
		return purchaseOrderService.savePurchaseOrders( purchaseOrders );
	}
	
	@RequestMapping(value = "/purchaseorders/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePurchaseOrder(@PathVariable("id") Long id) {
		this.purchaseOrderService.deletePurchaseOrder(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	/*
	 * PurchaseOrderItem
	 */
	
	@RequestMapping(value = "/purchaseorderitems/{id}")
	public PurchaseOrderItem getPurchaseOrderItem(@PathVariable("id") Long id) {
		return this.purchaseOrderService.getPurchaseOrderItem(id);
	}
	
	@RequestMapping(value = "/purchaseorderitems")
	public Page<PurchaseOrderItem> getPagedPurchaseOrderItems(Pageable pageable) {
		return this.purchaseOrderService.getPagedPurchaseOrderItems(pageable);
	}
	
	@RequestMapping(value = "/purchaseorderitems/get/all")
	public List<PurchaseOrderItem> getPurchaseOrderItems(Sort sort) {
		return this.purchaseOrderService.getPurchaseOrderItems(sort);
	}
	
	@RequestMapping(value = "/purchaseorderitems", method = RequestMethod.POST)
	public PurchaseOrderItem savePurchaseOrderItem(@RequestBody PurchaseOrderItem purchaseOrderItem) {
		return this.purchaseOrderService.savePurchaseOrderItem(purchaseOrderItem);
	}
	
	@RequestMapping(value = "/purchaseorderitems/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePurchaseOrderItem(@PathVariable("id") Long id) {
		this.purchaseOrderService.deletePurchaseOrderItem(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	/*
	 * SupplierProductCodeMap
	 */
	
	@RequestMapping(value = "/supplierproductcodemaps/{id}")
	public SupplierProductCodeMap getSupplierProductCodeMap(@PathVariable("id") Long id) {
		return this.purchaseOrderService.getSupplierProductCodeMap(id);
	}
	
	@RequestMapping(value = "/supplierproductcodemaps")
	public Page<SupplierProductCodeMap> getPagedSupplierProductCodeMaps(Pageable pageable) {
		return this.purchaseOrderService.getPagedSupplierProductCodeMaps(pageable);
	}
	
	@RequestMapping(value = "/supplierproductcodemaps/get/all")
	public List<SupplierProductCodeMap> getSupplierProductCodeMaps(Sort sort) {
		return this.purchaseOrderService.getSupplierProductCodeMaps(sort);
	}
	
	@RequestMapping(value = "/supplierproductcodemaps", method = RequestMethod.POST)
	public SupplierProductCodeMap saveSupplierProductCodeMap(@RequestBody SupplierProductCodeMap supplierProductCodeMap) {
		return this.purchaseOrderService.saveSupplierProductCodeMap(supplierProductCodeMap);
	}
	
	@RequestMapping(value = "/supplierproductcodemaps/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteSupplierProductCodeMap(@PathVariable("id") Long id) {
		this.purchaseOrderService.deleteSupplierProductCodeMap(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/supplierproductcodemaps/get/supplierProductCode/{supplierProductCode}")
	public SupplierProductCodeMap getSupplierProductCodeMap(@PathVariable("supplierProductCode") String supplierProductCode) {
		return this.purchaseOrderService.getSupplierProductCodeMap(supplierProductCode);
	}
	
	@RequestMapping(value = "/supplierproductcodemaps/get/productId/{productId}")
	public SupplierProductCodeMap getSupplierProductCodeMapByProductId(@PathVariable("productId") Long productId) {
		return this.purchaseOrderService.getSupplierProductCodeMapByProductId(productId);
	}
	
}
