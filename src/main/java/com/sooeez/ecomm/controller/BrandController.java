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

import com.sooeez.ecomm.domain.Brand;
import com.sooeez.ecomm.service.BrandService;

@RestController
@RequestMapping("/api")
public class BrandController {
	
	/*
	 * Service
	 */

	@Autowired 
	private BrandService brandService;
	
	/*
	 * Brand
	 */
	
	@RequestMapping(value = "/brands/check-unique")
	public Boolean existsBrand(Brand brand) {
		return brandService.existsBrand(brand);
	}
	
	@RequestMapping(value = "/brands/{id}")
	public Brand getBrand(@PathVariable("id") Long id) {
		return brandService.getBrand(id);
	}
	
	@RequestMapping(value = "/brands")
	public Page<Brand> getPagedBrands(Brand brand, Pageable pageable) {
		return brandService.getPagedBrands(brand, pageable);
	}
	
	@RequestMapping(value = "/brands/get/all")
	public List<Brand> getBrands(Brand brand, Sort sort) {
		return brandService.getBrands(brand, sort);
	}
	
	@RequestMapping(value = "/brands", method = RequestMethod.POST)
	public Brand saveBrand(@RequestBody Brand brand) {
		return brandService.saveBrand(brand);
	}
	
	@RequestMapping(value = "/brands/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteBrand(@PathVariable("id") Long id) {
		brandService.deleteBrand(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
