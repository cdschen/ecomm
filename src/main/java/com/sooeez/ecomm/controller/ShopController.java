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

import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.service.ShopService;

@RestController
@RequestMapping("/api")
public class ShopController {

	/*
	 * Service
	 */

	@Autowired
	private ShopService shopService;

	/*
	 * Shop
	 */

	@RequestMapping(value = "/shops/check-unique")
	public Boolean existsShop(Shop shop) {
		return shopService.existsShop(shop);
	}

	@RequestMapping(value = "/shops/{id}")
	public Shop getShop(@PathVariable("id") Long id) {
		return shopService.getShop(id);
	}

	@RequestMapping(value = "/shops")
	public Page<Shop> getPagedShops(Shop shop, Pageable pageable) {
		return shopService.getPagedShops(shop, pageable);
	}

	@RequestMapping(value = "/shops/get/all")
	public List<Shop> getShops(Shop shop, Sort sort) {
		return shopService.getShops(shop, sort);
	}

	@RequestMapping(value = "/shops", method = RequestMethod.POST)
	public Shop saveShop(@RequestBody Shop shop) {
		return shopService.saveShop(shop);
	}

	@RequestMapping(value = "/shops/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteShop(@PathVariable("id") Long id) {
		shopService.deleteShop(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
