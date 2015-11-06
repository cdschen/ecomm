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

import com.sooeez.ecomm.domain.Supplier;
import com.sooeez.ecomm.repository.SupplierRepository;

@Service
public class SupplierService {
	
	/*
	 * Repository
	 */

	@Autowired 
	private SupplierRepository supplierRepository;

	/*
	 * Supplier
	 */

	@Transactional
	public Supplier saveSupplier(Supplier supplier) {
		return supplierRepository.save(supplier);
	}

	@Transactional
	public void deleteSupplier(Long id) {
		supplierRepository.delete(id);
	}
	
	public Boolean existsSupplier(Supplier supplier) {
		return supplierRepository.count(getSupplierSpecification(supplier)) > 0 ? true : false;
	}

	public Supplier getSupplier(Long id) {
		return supplierRepository.findOne(id);
	}

	public List<Supplier> getSuppliers(Supplier supplier, Sort sort) {
		return supplierRepository.findAll(getSupplierSpecification(supplier), sort);
	}

	public Page<Supplier> getPagedSuppliers(Supplier supplier, Pageable pageable) {
		return supplierRepository.findAll(getSupplierSpecification(supplier), pageable);
	}
	
	private Specification<Supplier> getSupplierSpecification(Supplier supplier) {
		
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (supplier.getId() != null) {
				if (supplier.getCheckUnique() != null && supplier.getCheckUnique().booleanValue() == true) {
					predicates.add(cb.notEqual(root.get("id"), supplier.getId()));
				} else {
					predicates.add(cb.equal(root.get("id"), supplier.getId()));
				}
			}
			if (StringUtils.hasText(supplier.getName())) {
				predicates.add(cb.equal(root.get("name"), supplier.getName()));
			}
			if (supplier.getEnabled() != null) {
				predicates.add(cb.equal(root.get("enabled"), supplier.getEnabled()));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
		
	}
}
