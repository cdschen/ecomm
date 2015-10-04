package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.domain.WarehousePosition;
import com.sooeez.ecomm.repository.WarehousePositionRepository;
import com.sooeez.ecomm.repository.WarehouseRepository;

@Service
public class WarehouseService {

	// Repository
	@Autowired private WarehouseRepository warehouseRepository;
	@Autowired private WarehousePositionRepository warehousePositionRepository;

	/*
	 * Warehouse
	 */

	@Transactional
	public Warehouse saveWarehouse(Warehouse warehouse) {
		return this.warehouseRepository.save(warehouse);
	}

	@Transactional
	public void deleteWarehouse(Long id) {
		this.warehouseRepository.delete(id);
	}

	public Warehouse getWarehouse(Long id) {
		return this.warehouseRepository.findOne(id);
	}

	public List<Warehouse> getWarehouses(Warehouse warehouse, Sort sort) {
		return this.warehouseRepository.findAll(getWarehouseSpecification(warehouse), sort);
	}

	public Page<Warehouse> getPagedWarehouses(Warehouse warehouse, Pageable pageable) {
		return this.warehouseRepository.findAll(getWarehouseSpecification(warehouse), pageable);
	}

	private Specification<Warehouse> getWarehouseSpecification(Warehouse warehouse) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("deleted"), warehouse.getDeleted() != null && warehouse.getDeleted() == true ? true : false));
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	/*
	 * WarehousePosition
	 */

	@Transactional
	public WarehousePosition saveWarehousePosition(WarehousePosition warehousePosition) {
		return this.warehousePositionRepository.save(warehousePosition);
	}

	@Transactional
	public List<WarehousePosition> saveWarehousePositions(List<WarehousePosition> positions) {
		return this.warehousePositionRepository.save(positions);
	}

	@Transactional
	public void deleteWarehousePosition(Long id) {
		this.warehousePositionRepository.delete(id);
	}

	public WarehousePosition getWarehousePosition(Long id) {
		return this.warehousePositionRepository.findOne(id);
	}

	public List<WarehousePosition> getWarehousePositions() {
		return this.warehousePositionRepository.findAll();
	}

	public Page<WarehousePosition> getPagedWarehousePositions(Pageable pageable) {
		return this.warehousePositionRepository.findAll(pageable);
	}
}
