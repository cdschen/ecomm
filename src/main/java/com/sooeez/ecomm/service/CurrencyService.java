package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.Currency;
import com.sooeez.ecomm.repository.CurrencyRepository;

@Service
public class CurrencyService {

	@Autowired private CurrencyRepository currencyRepository;
	
	/*
	 * Currency
	 */
	
	public Currency saveCurrency(Currency currency) {
		return this.currencyRepository.save(currency);
	}
	
	public void deleteCurrency(Long id) {
		this.currencyRepository.delete(id);
	}
	
	public Currency getCurrency(Long id) {
		return this.currencyRepository.findOne(id);
	}

	public List<Currency> getCurrencies() {
		return this.currencyRepository.findAll();
	}
	
	public Page<Currency> getPagedCurrencies(Pageable pageable) {
		return this.currencyRepository.findAll(pageable);
	}

}
