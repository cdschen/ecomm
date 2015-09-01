package com.sooeez.ecomm.controller;

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

import com.sooeez.ecomm.domain.Supplier;
import com.sooeez.ecomm.service.SupplierService;

@RestController
@RequestMapping("/api")
public class SupplierController {

	@Autowired SupplierService supplierService;
	
	/*
	 * Supplier
	 */
	
	@RequestMapping(value = "/suppliers/{id}")
	public Supplier getSupplier(@PathVariable("id") Long id) {
		return this.supplierService.getSupplier(id);
	}
	
	@RequestMapping(value = "/suppliers")
	public Page<Supplier> getPagedSuppliers(Pageable pageable, Supplier supplier) {
		return this.supplierService.getPagedSuppliers(pageable, supplier);
	}
	
	@RequestMapping(value = "/suppliers/get/all")
	public List<Supplier> getSuppliers(Supplier supplier) {
		return this.supplierService.getSuppliers(supplier);
	}
	
	@RequestMapping(value = "/suppliers", method = RequestMethod.POST)
	public Supplier saveSupplier(@RequestBody Supplier supplier) {
		return this.supplierService.saveSupplier(supplier);
	}
	
	@RequestMapping(value = "/suppliers/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteSupplier(@PathVariable("id") Long id) {
		this.supplierService.deleteSupplier(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
