package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.Category;
import com.sooeez.ecomm.repository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired CategoryRepository categoryRepository;
	
	/*
	 * Category
	 */
	
	public Category saveCategory(Category category) {
		return this.categoryRepository.save(category);
	}
	
	public void deleteCategory(Long id) {
		this.categoryRepository.delete(id);
	}
	
	public Category getCategory(Long id) {
		return this.categoryRepository.findOne(id);
	}
	
	public List<Category> getCategories() {
		return this.categoryRepository.findAll();
	}

	public Page<Category> getPagedCategories(Pageable pageable) {
		return this.categoryRepository.findAll(pageable);
	}

}
