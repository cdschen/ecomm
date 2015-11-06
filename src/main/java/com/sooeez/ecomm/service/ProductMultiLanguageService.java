package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.ProductMultiLanguage;
import com.sooeez.ecomm.repository.ProductMultiLanguageRepository;

@Service
public class ProductMultiLanguageService {

	/*
	 * Repository
	 */
	
	@Autowired 
	private ProductMultiLanguageRepository multiLanguageRepository;
	
	/*
	 * ProductMultiLanguage
	 */

	@Transactional
	public ProductMultiLanguage saveMultiLanguage(ProductMultiLanguage multiLanguage) {
		return multiLanguageRepository.save(multiLanguage);
	}

	@Transactional
	public void deleteMultiLanguage(Long id) {
		multiLanguageRepository.delete(id);
	}

	public ProductMultiLanguage getMultiLanguage(Long id) {
		return multiLanguageRepository.findOne(id);
	}

	public List<ProductMultiLanguage> getMultiLanguages() {
		return multiLanguageRepository.findAll();
	}

	public Page<ProductMultiLanguage> getPagedMultiLanguages(Pageable pageable) {
		return multiLanguageRepository.findAll(pageable);
	}
}
