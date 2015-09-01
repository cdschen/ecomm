package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.Supplier;
import com.sooeez.ecomm.domain.Supplier;
import com.sooeez.ecomm.repository.SupplierRepository;

@Service
public class SupplierService {

	@Autowired SupplierRepository supplierRepository;

	/*
	 * Supplier
	 */

	public Supplier saveSupplier(Supplier brand) {
		return this.supplierRepository.save(brand);
	}

	public void deleteSupplier(Long id) {
		this.supplierRepository.delete(id);
	}

	public Supplier getSupplier(Long id) {
		return this.supplierRepository.findOne(id);
	}

	public List<Supplier> getSuppliers(Supplier suipplier) {
		return this.supplierRepository.findAll(getSupplierSpecification(suipplier));
	}

	public Page<Supplier> getPagedSuppliers(Pageable pageable, Supplier suipplier) {
		return this.supplierRepository.findAll(getSupplierSpecification(suipplier), pageable);
	}
	
	private Specification<Supplier> getSupplierSpecification(Supplier suipplier) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("deleted"), suipplier.getDeleted()));
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
}
