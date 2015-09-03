package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.ProductMember;
import com.sooeez.ecomm.domain.ProductMultiLanguage;
import com.sooeez.ecomm.domain.ProductMultiCurrency;
import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.repository.ProductMemberRepository;
import com.sooeez.ecomm.repository.ProductMultiLanguageRepository;
import com.sooeez.ecomm.repository.ProductMultiCurrencyRepository;
import com.sooeez.ecomm.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired private ProductRepository productRepository;

	@Autowired private ProductMultiLanguageRepository productMultiLanguageRepository;
	
	@Autowired private ProductMultiCurrencyRepository productMultiCurrencyRepository;
	
	@Autowired private ProductMemberRepository productMemberRepository;

	/*
	 * Product
	 */

	@Transactional
	public Product saveProduct(Product product) {
		return this.productRepository.save(product);
	}

	@Transactional
	public void deleteProduct(Long id) {
		this.productRepository.delete(id);
	}

	public Product getProduct(Long id) {
		return this.productRepository.findOne(id);
	}

	public List<Product> getProducts(Product product) {
		return this.productRepository.findAll(getProductSpecification(product));
	}

	public Page<Product> getPagedProducts(Pageable pageable, Product product) {
		return this.productRepository.findAll(getProductSpecification(product), pageable);
	}
	
	private Specification<Product> getProductSpecification(Product product) {
		
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (product.getDeleted()) {
				predicates.add(cb.equal(root.get("deleted"), true));
			} else {
				predicates.add(cb.equal(root.get("deleted"), false));
			}
			if (StringUtils.hasText(product.getSku())) {
				predicates.add(cb.like(root.get("sku"), "%" + product.getSku() + "%"));
			}
			if (StringUtils.hasText(product.getName())) {
				predicates.add(cb.like(root.get("name"), "%" + product.getName() + "%"));
			}
			if (product.getProductType() != null) {
				predicates.add(cb.equal(root.get("productType"), product.getProductType()));
			}
			if (product.getStatus() != null) {
				Subquery<ObjectProcess> objectProcessSubquery = query.subquery(ObjectProcess.class);
				Root<ObjectProcess> objectProcessRoot = objectProcessSubquery.from(ObjectProcess.class);
				objectProcessSubquery.select(objectProcessRoot.get("objectId"));
				objectProcessSubquery.where(objectProcessRoot.get("stepId").in(product.getStatus()));
				predicates.add(cb.in(root.get("id")).value(objectProcessSubquery));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	/*
	 * ProductMultiLanguage
	 */

	@Transactional
	public ProductMultiLanguage saveProductMultiLanguage(ProductMultiLanguage productMultiLanguage) {
		return this.productMultiLanguageRepository.save(productMultiLanguage);
	}

	@Transactional
	public void deleteProductMultiLanguage(Long id) {
		this.productMultiLanguageRepository.delete(id);
	}

	public ProductMultiLanguage getProductMultiLanguage(Long id) {
		return this.productMultiLanguageRepository.findOne(id);
	}

	public List<ProductMultiLanguage> getProductMultiLanguages() {
		return this.productMultiLanguageRepository.findAll();
	}

	public Page<ProductMultiLanguage> getPagedProductMultiLanguages(Pageable pageable) {
		return this.productMultiLanguageRepository.findAll(pageable);
	}
	
	/*
	 * ProductMultiCurrency
	 */

	@Transactional
	public ProductMultiCurrency saveProductMultiCurrency(ProductMultiCurrency productMultiCurrency) {
		return this.productMultiCurrencyRepository.save(productMultiCurrency);
	}

	@Transactional
	public void deleteProductMultiCurrency(Long id) {
		this.productMultiCurrencyRepository.delete(id);
	}

	public ProductMultiCurrency getProductMultiCurrency(Long id) {
		return this.productMultiCurrencyRepository.findOne(id);
	}

	public List<ProductMultiCurrency> getProductMultiCurrencies() {
		return this.productMultiCurrencyRepository.findAll();
	}

	public Page<ProductMultiCurrency> getPagedProductMultiCurrencies(Pageable pageable) {
		return this.productMultiCurrencyRepository.findAll(pageable);
	}
	
	/*
	 * ProductMember
	 */

	@Transactional
	public ProductMember saveProductMember(ProductMember productMember) {
		return this.productMemberRepository.save(productMember);
	}

	@Transactional
	public void deleteProductMember(Long id) {
		this.productMemberRepository.delete(id);
	}

	public ProductMember getProductMember(Long id) {
		return this.productMemberRepository.findOne(id);
	}

	public List<ProductMember> getProductMembers() {
		return this.productMemberRepository.findAll();
	}

	public Page<ProductMember> getPagedProductMembers(Pageable pageable) {
		return this.productMemberRepository.findAll(pageable);
	}
}
