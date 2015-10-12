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
import com.sooeez.ecomm.dto.api.DTO_Order;
import com.sooeez.ecomm.dto.api.general.DTO_Pagination;
import com.sooeez.ecomm.service.OrderService;
import com.sooeez.ecomm.service.ShopService;

@RestController
@RequestMapping("/oauth/api")
public class OrderApi {
	
	@Autowired ShopService shopService;
	@Autowired OrderService orderService;
	
	@RequestMapping(value = "/v1/orders", method = RequestMethod.GET)
	public Map<String, Object> getOrders(
			@RequestHeader("authorization") String authorization,
			@RequestHeader("ecomm-shopId") Long shopId,
			@RequestParam( value = "page", required = false ) Long currentPage,
			@RequestParam( value = "per_page", required = false ) Long perPage,
			@RequestParam( value = "id", required = false ) Long orderId,
			@RequestParam( value = "sn", required = false ) String orderSn)
	{
		
		Map<String, Object> map = new HashMap<>();
		
		Shop shop = shopService.getShop(shopId);
		
		if (shop != null && authorization.equals(shop.getToken()))
		{
			map.put("code", "0");
			map.put("message", "success");
			
			/* 是否获取单个订单 */
			if( orderId!=null || orderSn!=null )
			{
				DTO_Order dtoOrder = new DTO_Order();
				
				this.orderService.setAPIRespondOrder(shop, orderId, orderSn, dtoOrder);

				map.put("order", dtoOrder.getId() != null ? dtoOrder : null);
			}
			else
			{
				/* 初始化分页参数 */
				DTO_Pagination page_context = new DTO_Pagination();
				page_context.setPage( currentPage != null ? currentPage : 1 );
				page_context.setPer_page( perPage != null ? perPage : 30 );
				
				/* 否则获取多个订单 */
				List<DTO_Order> orders = new ArrayList<DTO_Order>();
				
				this.orderService.setAPIRespondOrders(shop, orders, page_context);
				
				map.put("orders", orders.size() > 0 ? orders : null);
				
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
	
	@RequestMapping(value = "/v1/orders", method = RequestMethod.DELETE)
	public Map<String, Object> deleteOrders(
			@RequestHeader("authorization") String authorization,
			@RequestHeader("ecomm-shopId") Long shopId,
			@RequestParam( value = "id", required = false ) Long orderId,
			@RequestParam( value = "sn", required = false ) String orderSn)
	{
		
		Map<String, Object> map = new HashMap<>();
		
		Shop shop = shopService.getShop(shopId);
		
		if (shop != null && authorization.equals(shop.getToken()))
		{
			map.put("code", "0");
			
			map.put("message", "success");

			this.orderService.deleteAPIRespondOrders(shop, orderId, orderSn, map);
			
		}
		else
		{
			map.put("code", "401");
			map.put("message", "Token is invalid.");
		}
		
		return map;
	}
}
