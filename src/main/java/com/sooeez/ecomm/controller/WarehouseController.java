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
import com.sooeez.ecomm.service.WarehousePositionService;
import com.sooeez.ecomm.service.WarehouseService;

@RestController
@RequestMapping("/api")
public class WarehouseController {

	/*
	 * Service
	 */

	@Autowired
	private WarehouseService warehouseService;
	
	@Autowired
	private WarehousePositionService positionService;

	/*
	 * Warehouse
	 */
	
	@RequestMapping(value = "/warehouses/check-unique")
	public Boolean existsWarehouse(Warehouse warehouse) {
		return warehouseService.existsWarehouse(warehouse);
	}

	@RequestMapping(value = "/warehouses/{id}")
	public Warehouse getWarehouse(@PathVariable("id") Long id) {
		return warehouseService.getWarehouse(id);
	}

	@RequestMapping(value = "/warehouses")
	public Page<Warehouse> getPagedWarehouses(Warehouse warehouse, Pageable pageable) {
		return warehouseService.getPagedWarehouses(warehouse, pageable);
	}

	@RequestMapping(value = "/warehouses/get/all")
	public List<Warehouse> getWarehouse(Warehouse warehouse, Sort sort) {
		return warehouseService.getWarehouses(warehouse, sort);
	}

	@RequestMapping(value = "/warehouses", method = RequestMethod.POST)
	public Warehouse saveWarehouse(@RequestBody Warehouse warehouse) {
		return warehouseService.saveWarehouse(warehouse);
	}

	@RequestMapping(value = "/warehouses/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteWarehouse(@PathVariable("id") Long id) {
		warehouseService.deleteWarehouse(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/*
	 * WarehousePosition
	 */

	@RequestMapping(value = "/warehouse-positions/{id}")
	public WarehousePosition getPosition(@PathVariable("id") Long id) {
		return positionService.getPosition(id);
	}

	@RequestMapping(value = "/warehouse-positions")
	public Page<WarehousePosition> getPagedPositions(WarehousePosition position, Pageable pageable) {
		return positionService.getPagedPositions(pageable);
	}

	@RequestMapping(value = "/warehouse-positions/get/all")
	public List<WarehousePosition> getPositions() {
		return positionService.getPositions();
	}

	@RequestMapping(value = "/warehouse-positions", method = RequestMethod.POST)
	public WarehousePosition savePosition(@RequestBody WarehousePosition position) {
		return positionService.savePosition(position);
	}

	@RequestMapping(value = "/warehouse-positions/save/list", method = RequestMethod.POST)
	public List<WarehousePosition> savePositions(@RequestBody List<WarehousePosition> positions) {
		return positionService.savePositions(positions);
	}

	@RequestMapping(value = "/warehouse-positions/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePosition(@PathVariable("id") Long id) {
		positionService.deletePosition(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
