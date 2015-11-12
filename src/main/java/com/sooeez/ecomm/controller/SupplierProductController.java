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

import com.sooeez.ecomm.domain.SupplierProduct;
import com.sooeez.ecomm.service.SupplierProductService;

@RestController
@RequestMapping("/api")
public class SupplierProductController {

	/*
	 * Service
	 */

	@Autowired
	private SupplierProductService supplierProductService;

	/*
	 * SupplierProduct
	 */

	@RequestMapping(value = "/supplierproducts/{id}")
	public SupplierProduct getSupplierProduct(@PathVariable("id") Long id) {
		return supplierProductService.getSupplierProduct(id);
	}

	@RequestMapping(value = "/supplierproducts")
	public Page<SupplierProduct> getPagedSuppliers(SupplierProduct supplierProduct, Pageable pageable) {
		return supplierProductService.getPagedSupplierProducts( supplierProduct, pageable );
	}

	@RequestMapping(value = "/supplierproducts/get/all")
	public List<SupplierProduct> getSupplierProducts(SupplierProduct supplierProduct, Sort sort) {
		return supplierProductService.getSupplierProducts( supplierProduct, sort );
	}

	@RequestMapping(value = "/supplierproducts", method = RequestMethod.POST)
	public SupplierProduct saveSupplier(@RequestBody SupplierProduct supplierProduct) {
		return supplierProductService.saveSupplierProduct( supplierProduct );
	}

	@RequestMapping(value = "/supplierproducts/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteSupplierProduct(@PathVariable("id") Long id) {
		supplierProductService.deleteSupplierProduct(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
