package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.ProductMultiCurrency;
import com.sooeez.ecomm.repository.ProductMultiCurrencyRepository;

@Service
public class ProductMultiCurrencyService {

	/*
	 * Repository
	 */

	@Autowired
	private ProductMultiCurrencyRepository multiCurrencyRepository;

	/*
	 * ProductMultiCurrency
	 */

	@Transactional
	public ProductMultiCurrency saveMultiCurrency(ProductMultiCurrency multiCurrency) {
		return multiCurrencyRepository.save(multiCurrency);
	}

	@Transactional
	public void deleteMultiCurrency(Long id) {
		multiCurrencyRepository.delete(id);
	}

	public ProductMultiCurrency getMultiCurrency(Long id) {
		return multiCurrencyRepository.findOne(id);
	}

	public List<ProductMultiCurrency> getMultiCurrencies() {
		return multiCurrencyRepository.findAll();
	}

	public Page<ProductMultiCurrency> getPagedMultiCurrencies(Pageable pageable) {
		return multiCurrencyRepository.findAll(pageable);
	}

}
