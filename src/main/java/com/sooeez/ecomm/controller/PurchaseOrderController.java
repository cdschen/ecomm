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
import com.sooeez.ecomm.domain.PurchaseOrderItem;
import com.sooeez.ecomm.domain.SupplierProduct;
import com.sooeez.ecomm.dto.PageDTO;
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
	public PageDTO<PurchaseOrder> getPagedPurchaseOrders(PurchaseOrder purchaseOrder, Pageable pageable) {
		Page<PurchaseOrder> page = this.purchaseOrderService.getPagedPurchaseOrders( purchaseOrder, pageable );
		PageDTO<PurchaseOrder> pageDTO = new PageDTO<>();
		BeanUtils.copyProperties( page, pageDTO, "content", "sort" );
		List<PurchaseOrder> fpos = new ArrayList<>();
		page.getContent().forEach( opo ->
		{
			PurchaseOrder fpo = new PurchaseOrder();
			BeanUtils.copyProperties( opo, fpo, "purchaseOrderDeliveries", "items" );
			fpos.add( fpo );
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
		pageDTO.setContent( fpos );
		return pageDTO;
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
	public SupplierProduct getSupplierProduct(@PathVariable("id") Long id) {
		return this.purchaseOrderService.getSupplierProduct(id);
	}
	
	@RequestMapping(value = "/supplierproductcodemaps")
	public Page<SupplierProduct> getPagedSupplierProducts(Pageable pageable) {
		return this.purchaseOrderService.getPagedSupplierProducts(pageable);
	}
	
	@RequestMapping(value = "/supplierproductcodemaps/get/all")
	public List<SupplierProduct> getSupplierProducts(Sort sort) {
		return this.purchaseOrderService.getSupplierProducts(sort);
	}
	
	@RequestMapping(value = "/supplierproductcodemaps", method = RequestMethod.POST)
	public SupplierProduct saveSupplierProduct(@RequestBody SupplierProduct supplierProduct) {
		return this.purchaseOrderService.saveSupplierProduct(supplierProduct);
	}
	
	@RequestMapping(value = "/supplierproductcodemaps/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteSupplierProduct(@PathVariable("id") Long id) {
		this.purchaseOrderService.deleteSupplierProduct(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
