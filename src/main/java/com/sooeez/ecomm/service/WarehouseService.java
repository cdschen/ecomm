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
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.repository.WarehouseRepository;

@Service
public class WarehouseService {

	/*
	 * Repository
	 */
	
	@Autowired
	private WarehouseRepository warehouseRepository;

	/*
	 * Warehouse
	 */

	@Transactional
	public Warehouse saveWarehouse(Warehouse warehouse) {
		return warehouseRepository.save(warehouse);
	}

	@Transactional
	public void deleteWarehouse(Long id) {
		warehouseRepository.delete(id);
	}
	
	public Boolean existsWarehouse(Warehouse warehouse) {
		return warehouseRepository.count(getWarehouseSpecification(warehouse)) > 0 ? true : false;
	}

	public Warehouse getWarehouse(Long id) {
		return warehouseRepository.findOne(id);
	}

	public List<Warehouse> getWarehouses(Warehouse warehouse, Sort sort) {
		return warehouseRepository.findAll(getWarehouseSpecification(warehouse), sort);
	}

	public Page<Warehouse> getPagedWarehouses(Warehouse warehouse, Pageable pageable) {
		return warehouseRepository.findAll(getWarehouseSpecification(warehouse), pageable);
	}

	private Specification<Warehouse> getWarehouseSpecification(Warehouse warehouse) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (warehouse.getId() != null) {
				if (warehouse.getCheckUnique() != null && warehouse.getCheckUnique().booleanValue() == true) {
					predicates.add(cb.notEqual(root.get("id"), warehouse.getId()));
				} else {
					predicates.add(cb.equal(root.get("id"), warehouse.getId()));
				}
			}
			if (StringUtils.hasText(warehouse.getName())) {
				predicates.add(cb.equal(root.get("name"), warehouse.getName()));
			}
			if (warehouse.getEnabled() != null) {
				predicates.add(cb.equal(root.get("enabled"), warehouse.getEnabled()));
			}
			if (warehouse.getWarehouseIds() != null && warehouse.getWarehouseIds().length > 0) {
				predicates.add(root.get("id").in((Object[]) warehouse.getWarehouseIds()));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
		
	}
	
}
