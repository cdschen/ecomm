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

import com.sooeez.ecomm.domain.PurchaseOrderDelivery;
import com.sooeez.ecomm.domain.PurchaseOrderDeliveryItem;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.service.PurchaseOrderDeliveryService;

@RestController
@RequestMapping("/api")
public class PurchaseOrderDeliveryController {
	
	@Autowired private PurchaseOrderDeliveryService purchaseOrderDeliveryService;
	
	/*
	 * PurchaseOrderDelivery
	 */
	
	@RequestMapping(value = "/purchaseorderdeliveries/{id}")
	public PurchaseOrderDelivery getPurchaseOrderDelivery(@PathVariable("id") Long id) {
		return this.purchaseOrderDeliveryService.getPurchaseOrderDelivery(id);
	}
	
	@RequestMapping(value = "/purchaseorderdeliveries")
	public Page<PurchaseOrderDelivery> getPagedPurchaseOrderDeliverys(PurchaseOrderDelivery purchaseOrderDelivery, Pageable pageable) {
		return this.purchaseOrderDeliveryService.getPagedPurchaseOrderDeliverys(purchaseOrderDelivery, pageable);
	}
	
	@RequestMapping(value = "/purchaseorderdeliveries/confirm/complete/operation-review")
	public OperationReviewDTO confirmOperationReviewWhenCompletePurchaseOrderDelivery(@RequestBody OperationReviewDTO review) {
		return this.purchaseOrderDeliveryService.confirmOrderWhenGeneratePurchaseOrderDelivery(review);
	}
	
	@RequestMapping(value = "/purchaseorderdeliveries/get/all")
	public List<PurchaseOrderDelivery> getPurchaseOrderDeliverys(PurchaseOrderDelivery purchaseOrderDelivery, Sort sort) {
		return this.purchaseOrderDeliveryService.getPurchaseOrderDeliverys(purchaseOrderDelivery, sort);
	}
	
	@RequestMapping(value = "/purchaseorderdeliveries", method = RequestMethod.POST)
	public PurchaseOrderDelivery savePurchaseOrderDelivery(@RequestBody PurchaseOrderDelivery purchaseOrderDelivery, @RequestParam String action, HttpServletRequest request) {
		return this.purchaseOrderDeliveryService.savePurchaseOrderDelivery(purchaseOrderDelivery);
	}
	
	@RequestMapping(value = "/purchaseorderdeliveries/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePurchaseOrderDelivery(@PathVariable("id") Long id) {
		this.purchaseOrderDeliveryService.deletePurchaseOrderDelivery(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * PurchaseOrderDeliveryItem
	 */
	
	@RequestMapping(value = "/purchaseorderdeliveryitems/{id}")
	public PurchaseOrderDeliveryItem getPurchaseOrderDeliveryItem(@PathVariable("id") Long id) {
		return this.purchaseOrderDeliveryService.getPurchaseOrderDeliveryItem(id);
	}
	
	@RequestMapping(value = "/purchaseorderdeliveryitems")
	public Page<PurchaseOrderDeliveryItem> getPagedPurchaseOrderDeliveryItems(Pageable pageable) {
		return this.purchaseOrderDeliveryService.getPagedPurchaseOrderDeliveryItems(pageable);
	}
	
	@RequestMapping(value = "/purchaseorderdeliveryitems/get/all")
	public List<PurchaseOrderDeliveryItem> getPurchaseOrderDeliveryItems(Sort sort) {
		return this.purchaseOrderDeliveryService.getPurchaseOrderDeliveryItems(sort);
	}
	
	@RequestMapping(value = "/purchaseorderdeliveryitems", method = RequestMethod.POST)
	public PurchaseOrderDeliveryItem savePurchaseOrderDeliveryItem(@RequestBody PurchaseOrderDeliveryItem purchaseOrderDeliveryItem) {
		return this.purchaseOrderDeliveryService.savePurchaseOrderDeliveryItem(purchaseOrderDeliveryItem);
	}
	
	@RequestMapping(value = "/purchaseorderdeliveryitems/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePurchaseOrderDeliveryItem(@PathVariable("id") Long id) {
		this.purchaseOrderDeliveryService.deletePurchaseOrderDeliveryItem(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
