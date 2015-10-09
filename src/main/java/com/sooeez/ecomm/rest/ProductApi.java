package com.sooeez.ecomm.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.dto.api.DTO_Product_Partner;
import com.sooeez.ecomm.dto.api.DTO_Product_Self;
import com.sooeez.ecomm.dto.api.general.DTO_Pagination;
import com.sooeez.ecomm.service.ProductService;
import com.sooeez.ecomm.service.ShopService;

@RestController
@RequestMapping("/oauth/api")
public class ProductApi {
	
	@Autowired ShopService shopService;
	@Autowired ProductService productService;
	
	@RequestMapping(value = "/v1/products", method = RequestMethod.GET)
	public Map<String, Object> getProducts(
			@RequestHeader("authorization") String authorization,
			@RequestHeader("ecomm-shopId") Long shopId,
			@RequestParam( value = "page", required = false ) Long currentPage,
			@RequestParam( value = "per_page", required = false ) Long perPage,
			@RequestParam( value = "id", required = false ) Long productId,
			@RequestParam( value = "sku", required = false ) String productSku)
	{
		
		Map<String, Object> map = new HashMap<>();
		
		Shop shop = shopService.getShop(shopId);
		
		if (shop != null && authorization.equals(shop.getToken()))
		{
			map.put("code", "0");
			map.put("message", "success");
			
			/* 是否获取单个商品 */
			if( productId!=null || productSku!=null )
			{
				DTO_Product_Self productSelf = new DTO_Product_Self();
				DTO_Product_Partner productPartner = new DTO_Product_Partner();
				
				this.productService.setAPIRespondProduct(shop, productId, productSku, productSelf, productPartner);

				if( shop.getType() == 0 )
				{
					map.put("product", productSelf.getId() != null ? productSelf : null);
				}
				else
				{
					map.put("product", productPartner.getId() != null ? productPartner : null);
				}
			}
			else
			{
				/* 初始化分页参数 */
				DTO_Pagination page_context = new DTO_Pagination();
				page_context.setPage( currentPage != null ? currentPage : 1 );
				page_context.setPer_page( perPage != null ? perPage : 30 );
				
				/* 否则获取多个商品 */
				List<DTO_Product_Self> productsSelf = new ArrayList<DTO_Product_Self>();
				List<DTO_Product_Partner> productsPartner = new ArrayList<DTO_Product_Partner>();
				
				this.productService.setAPIRespondProducts(shop, productsSelf, productsPartner, page_context);
				
				if( shop.getType() == 0 )
				{
					map.put("products", productsSelf.size() > 0 ? productsSelf : null);
				}
				else
				{
					map.put("products", productsPartner.size() > 0 ? productsPartner : null);
				}
				
				map.put("page_context", page_context);
			}
		}
		else
		{
			map.put("code", "401");
			map.put("message", "Token is invalid.");
		}
		
		return map;
	}
}
