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

import com.sooeez.ecomm.domain.Category;
import com.sooeez.ecomm.service.CategoryService;

@RestController
@RequestMapping("/api")
public class CategoryController {
	
	/*
	 * Service
	 */

	@Autowired 
	private CategoryService categoryService;
	
	/*
	 * Category
	 */
	
	@RequestMapping(value = "/categories/check-unique")
	public Boolean existsCategory(Category category) {
		return categoryService.existsCategory(category);
	}
	
	@RequestMapping(value = "/categories/{id}")
	public Category getCategory(@PathVariable("id") Long id) {
		return categoryService.getCategory(id);
	}
	
	@RequestMapping(value = "/categories")
	public Page<Category> getPagedCategories(Category category, Pageable pageable) {
		return categoryService.getPagedCategories(category, pageable);
	}
	
	@RequestMapping(value = "/categories/get/all")
	public List<Category> getCategories(Category category, Sort sort) {
		return categoryService.getCategories(category, sort);
	}
	
	@RequestMapping(value = "/categories", method = RequestMethod.POST)
	public Category saveCategory(@RequestBody Category category) {
		return categoryService.saveCategory(category);
	}
	
	@RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) {
		categoryService.deleteCategory(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
