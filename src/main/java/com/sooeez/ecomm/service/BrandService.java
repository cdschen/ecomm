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

import com.sooeez.ecomm.domain.Brand;
import com.sooeez.ecomm.repository.BrandRepository;

@Service
public class BrandService {

	/*
	 * Repository
	 */

	@Autowired
	private BrandRepository brandRepository;

	/*
	 * Brand
	 */

	@Transactional
	public Brand saveBrand(Brand brand) {
		return brandRepository.save(brand);
	}

	@Transactional
	public void deleteBrand(Long id) {
		brandRepository.delete(id);
	}

	public Boolean existsBrand(Brand brand) {
		return brandRepository.count(getBrandSpecification(brand)) > 0 ? true : false;
	}

	public Brand getBrand(Long id) {
		return brandRepository.findOne(id);
	}

	public List<Brand> getBrands(Brand brand, Sort sort) {
		return brandRepository.findAll(getBrandSpecification(brand), sort);
	}

	public Page<Brand> getPagedBrands(Brand brand, Pageable pageable) {
		return brandRepository.findAll(getBrandSpecification(brand), pageable);
	}

	private Specification<Brand> getBrandSpecification(Brand brand) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (brand.getId() != null) {
				if (brand.getCheckUnique() != null && brand.getCheckUnique().booleanValue() == true) {
					predicates.add(cb.notEqual(root.get("id"), brand.getId()));
				} else {
					predicates.add(cb.equal(root.get("id"), brand.getId()));
				}
			}
			if (StringUtils.hasText(brand.getName())) {
				predicates.add(cb.equal(root.get("name"), brand.getName()));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};

	}

}
