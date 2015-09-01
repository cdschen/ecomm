package com.sooeez.ecomm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.Currency;
import com.sooeez.ecomm.domain.Currency;
import com.sooeez.ecomm.service.CurrencyService;

@RestController
@RequestMapping("/api")
public class CurrencyController {

	@Autowired private CurrencyService currencyService;
	
	/*
	 * Currency
	 */
	
	@RequestMapping(value = "/currencies/{id}")
	public Currency getCurrency(@PathVariable("id") Long id) {
		return this.currencyService.getCurrency(id);
	}
	
	@RequestMapping(value = "/currencies")
	public Page<Currency> getPagedCurrencies(Pageable pageable) {
		return this.currencyService.getPagedCurrencies(pageable);
	}
	
	@RequestMapping(value = "/currencies/get/all")
	public List<Currency> getCurrencies() {
		return this.currencyService.getCurrencies();
	}
	
	@RequestMapping(value = "/currencies", method = RequestMethod.POST)
	public Currency saveCurrency(@RequestBody Currency currency) {
		return this.currencyService.saveCurrency(currency);
	}
	
	@RequestMapping(value = "/currencies/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCurrency(@PathVariable("id") Long id) {
		this.currencyService.deleteCurrency(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
