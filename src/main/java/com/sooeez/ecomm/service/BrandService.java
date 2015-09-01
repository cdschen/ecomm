package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.Brand;
import com.sooeez.ecomm.repository.BrandRepository;

@Service
public class BrandService {

	@Autowired BrandRepository brandRepository;
	
	/*
	 * Brand
	 */
	
	public Brand saveBrand(Brand brand) {
		return this.brandRepository.save(brand);
	}
	
	public void deleteBrand(Long id) {
		this.brandRepository.delete(id);
	}
	
	public Brand getBrand(Long id) {
		return this.brandRepository.findOne(id);
	}
	
	public List<Brand> getBrands() {
		return this.brandRepository.findAll();
	}

	public Page<Brand> getPagedBrands(Pageable pageable) {
		return this.brandRepository.findAll(pageable);
	}
}
