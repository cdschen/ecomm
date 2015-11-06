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

import com.sooeez.ecomm.domain.Category;
import com.sooeez.ecomm.repository.CategoryRepository;

@Service
public class CategoryService {

	/*
	 * Repository
	 */

	@Autowired
	private CategoryRepository categoryRepository;

	/*
	 * Category
	 */

	@Transactional
	public Category saveCategory(Category category) {
		return categoryRepository.save(category);
	}

	@Transactional
	public void deleteCategory(Long id) {
		categoryRepository.delete(id);
	}
	
	public Boolean existsCategory(Category category) {
		return categoryRepository.count(getCategorySpecification(category)) > 0 ? true : false;
	}

	public Category getCategory(Long id) {
		return categoryRepository.findOne(id);
	}

	public List<Category> getCategories(Category category, Sort sort) {
		return categoryRepository.findAll(getCategorySpecification(category), sort);
	}

	public Page<Category> getPagedCategories(Category category, Pageable pageable) {
		return categoryRepository.findAll(getCategorySpecification(category), pageable);
	}
	
	private Specification<Category> getCategorySpecification(Category category) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (category.getId() != null) {
				if (category.getCheckUnique() != null && category.getCheckUnique().booleanValue() == true) {
					predicates.add(cb.notEqual(root.get("id"), category.getId()));
				} else {
					predicates.add(cb.equal(root.get("id"), category.getId()));
				}
			}
			if (StringUtils.hasText(category.getName())) {
				predicates.add(cb.equal(root.get("name"), category.getName()));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};

	}

}
