package com.sooeez.ecomm.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.PurchaseOrder;
import com.sooeez.ecomm.domain.PurchaseOrderDelivery;
import com.sooeez.ecomm.domain.PurchaseOrderDeliveryItem;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.dto.PageDTO;
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
	public PageDTO<PurchaseOrderDelivery> getPagedPurchaseOrderDeliverys(PurchaseOrderDelivery purchaseOrderDelivery, Pageable pageable) {
		Page<PurchaseOrderDelivery> page = this.purchaseOrderDeliveryService.getPagedPurchaseOrderDeliverys( purchaseOrderDelivery, pageable );
		PageDTO<PurchaseOrderDelivery> pageDTO = new PageDTO<>();
		BeanUtils.copyProperties( page, pageDTO, "content", "sort" );
		List<PurchaseOrderDelivery> fpods = new ArrayList<>();
		page.getContent().forEach( opod ->
		{
			PurchaseOrderDelivery fpod = new PurchaseOrderDelivery();
			BeanUtils.copyProperties( opod, fpod, "batches", "items" );
			fpods.add( fpod );
//			Product productDTO = new Product();
//			productDTO.setId(p.getId());
//			productDTO.setSku(p.getSku());
//			productDTO.setName(p.getName());
//			productDTO.setProcesses(p.getProcesses());
//			productDTO.setWarehouses(p.getWarehouses());
//			
//			productDTO.setPriceL1(p.getPriceL1());
//			productDTO.setPriceL2(p.getPriceL2());
//			productDTO.setPriceL3(p.getPriceL3());
//			productDTO.setPriceL4(p.getPriceL4());
//			productDTO.setPriceL5(p.getPriceL5());
//			productDTO.setPriceL6(p.getPriceL6());
//			productDTO.setPriceL7(p.getPriceL7());
//			productDTO.setPriceL8(p.getPriceL8());
//			productDTO.setPriceL9(p.getPriceL9());
//			productDTO.setPriceL10(p.getPriceL10());
//			productDTO.setWeight(p.getWeight());
			
//			fpos.add( productDTO );
		});
		pageDTO.setContent( fpods );
		return pageDTO;
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
