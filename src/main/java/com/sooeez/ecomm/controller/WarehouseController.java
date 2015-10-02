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

import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.domain.WarehousePosition;
import com.sooeez.ecomm.service.WarehouseService;

@RestController
@RequestMapping("/api")
public class WarehouseController {
	
	@Autowired private WarehouseService warehouseService;

	/*
	 * Warehouse
	 */
	
	@RequestMapping(value = "/warehouses/{id}")
	public Warehouse getWarehouse(@PathVariable("id") Long id) {
		return this.warehouseService.getWarehouse(id);
	}
	
	@RequestMapping(value = "/warehouses")
	public Page<Warehouse> getPagedWarehouses(Warehouse warehouse, Pageable pageable) {
		return this.warehouseService.getPagedWarehouses(warehouse, pageable);
	}
	
	@RequestMapping(value = "/warehouses/get/all")
	public List<Warehouse> getWarehouse(Warehouse warehouse, Sort sort) {
		return this.warehouseService.getWarehouses(warehouse, sort);
	}
	
	@RequestMapping(value = "/warehouses", method = RequestMethod.POST)
	public Warehouse saveWarehouse(@RequestBody Warehouse warehouse) {
		return this.warehouseService.saveWarehouse(warehouse);
	}
	
	@RequestMapping(value = "/warehouses/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteWarehouse(@PathVariable("id") Long id) {
		this.warehouseService.deleteWarehouse(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * WarehousePosition
	 */
	
	@RequestMapping(value = "/warehousepositions/{id}")
	public WarehousePosition getWarehousePosition(@PathVariable("id") Long id) {
		return this.warehouseService.getWarehousePosition(id);
	}
	
	@RequestMapping(value = "/warehousepositions")
	public Page<WarehousePosition> getPagedWarehousePositions(Pageable pageable) {
		return this.warehouseService.getPagedWarehousePositions(pageable);
	}
	
	@RequestMapping(value = "/warehousepositions/get/all")
	public List<WarehousePosition> getWarehousePositions() {
		return this.warehouseService.getWarehousePositions();
	}
	
	@RequestMapping(value = "/warehousepositions", method = RequestMethod.POST)
	public WarehousePosition saveWarehousePosition(@RequestBody WarehousePosition position) {
		return this.warehouseService.saveWarehousePosition(position);
	}
	
	@RequestMapping(value = "/warehousepositions/save/list", method = RequestMethod.POST)
	public List<WarehousePosition> saveWarehousePositions(@RequestBody List<WarehousePosition> positions) {
		return this.warehouseService.saveWarehousePositions(positions);
	}
	
	@RequestMapping(value = "/warehousepositions/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteWarehousePosition(@PathVariable("id") Long id) {
		this.warehouseService.deleteWarehousePosition(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
