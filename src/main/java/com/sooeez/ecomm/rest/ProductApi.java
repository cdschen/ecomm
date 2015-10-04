package com.sooeez.ecomm.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.dto.ProductDTO;
import com.sooeez.ecomm.service.ProductService;
import com.sooeez.ecomm.service.ShopService;

@RestController
@RequestMapping("/oauth/api")
public class ProductApi {
	
	@Autowired ShopService shopService;
	@Autowired ProductService productService;
	
	@RequestMapping("/v1/products")
	public Map<String, Object> getProducts(
			@RequestHeader("authorization") String authorization,
			@RequestHeader("ecomm-shopId") Long shopId) {
		
		System.out.println("authorization:" + authorization);
		System.out.println("shopId:" + shopId);
		
		Map<String, Object> map = new HashMap<>();
		
		Shop shop = shopService.getShop(shopId);
		
		if (shop != null && authorization.equals(shop.getToken())) {
			List<ProductDTO> products = new ArrayList<>();
			
			Product productQuery = new Product();
			productQuery.setDeleted(false);
			
			productService.getProducts(productQuery, null).forEach(product -> {
				ProductDTO productDTO = new ProductDTO();
				productDTO.setSku(product.getSku());
				productDTO.setName(product.getName());
				products.add(productDTO);
			});
			
			map.put("code", "200");
			map.put("message", "ok");
			map.put("products", products);
		} else {
			map.put("code", "401");
			map.put("message", "Token is invalid.");
		}
		
		return map;
	}
}
