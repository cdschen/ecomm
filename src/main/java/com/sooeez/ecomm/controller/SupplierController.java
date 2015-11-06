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

import com.sooeez.ecomm.domain.Supplier;
import com.sooeez.ecomm.service.SupplierService;

@RestController
@RequestMapping("/api")
public class SupplierController {

	/*
	 * Service
	 */

	@Autowired
	private SupplierService supplierService;

	/*
	 * Supplier
	 */

	@RequestMapping(value = "/suppliers/check-unique")
	public Boolean existSupplier(Supplier supplier) {
		return supplierService.existsSupplier(supplier);
	}

	@RequestMapping(value = "/suppliers/{id}")
	public Supplier getSupplier(@PathVariable("id") Long id) {
		return supplierService.getSupplier(id);
	}

	@RequestMapping(value = "/suppliers")
	public Page<Supplier> getPagedSuppliers(Supplier supplier, Pageable pageable) {
		return supplierService.getPagedSuppliers(supplier, pageable);
	}

	@RequestMapping(value = "/suppliers/get/all")
	public List<Supplier> getSuppliers(Supplier supplier, Sort sort) {
		return supplierService.getSuppliers(supplier, sort);
	}

	@RequestMapping(value = "/suppliers", method = RequestMethod.POST)
	public Supplier saveSupplier(@RequestBody Supplier supplier) {
		return supplierService.saveSupplier(supplier);
	}

	@RequestMapping(value = "/suppliers/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteSupplier(@PathVariable("id") Long id) {
		supplierService.deleteSupplier(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
