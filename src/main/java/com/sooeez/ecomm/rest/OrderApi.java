package com.sooeez.ecomm.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.dto.api.DTO_Order;
import com.sooeez.ecomm.dto.api.DTO_OrderItem;
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
				
				this.orderService.getAPIRespondOrder(shop, orderId, orderSn, dtoOrder);

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
				
				this.orderService.getAPIRespondOrders(shop, orders, page_context);
				
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
	
	@RequestMapping(value = "/v1/orders", method = RequestMethod.POST)
	public Map<String, Object> createOrders(
			@RequestHeader("authorization") String authorization,
			@RequestHeader("ecomm-shopId") Long shopId,
			@RequestBody( required = false ) DTO_Order dtoOrder)
	{
		
		Map<String, Object> map = new HashMap<>();
		
		Shop shop = shopService.getShop(shopId);
		
		/* 店铺验证必须通过 */
		if (shop != null && authorization.equals(shop.getToken()))
		{
			/* 必须传入订单信息 */
			if( dtoOrder != null )
			{
				/* 发货方式不能为空 */
				if( dtoOrder.getDelivery_method() != null && ! dtoOrder.getDelivery_method().trim().equals("") )
				{
					/* 订单详情不能为空 receive_name,receive_phone,receive_address */
					if( dtoOrder.getOrder_items() != null && dtoOrder.getOrder_items().size() > 0 )
					{
						/* 收件人名不能为空 */
						if( dtoOrder.getReceive_name() != null && ! dtoOrder.getReceive_name().trim().equals("") )
						{
							/* 收件人电话不能为空 */
							if( dtoOrder.getReceive_phone() != null && ! dtoOrder.getReceive_phone().trim().equals("") )
							{
								/* 收件人地址不能为空 */
								if( dtoOrder.getReceive_address() != null && ! dtoOrder.getReceive_address().trim().equals("") )
								{
									
									Boolean isItemSkuEmpty = false;
									Boolean isQtyOrderedEmpty = false;
									
									/* 检查所有 Item 看是否有为空的 sku 或 qty_ordered */
									for( DTO_OrderItem dtoOrderItem : dtoOrder.getOrder_items() )
									{
										if( dtoOrderItem.getSku() == null || dtoOrderItem.getSku().trim().equals("") )
										{
											isItemSkuEmpty = true;
										}
										if( dtoOrderItem.getQty_ordered() == null || dtoOrderItem.getQty_ordered().equals( 0 ) )
										{
											isQtyOrderedEmpty = true;
										}
									}
									
									/* 订单详情 sku 是否为空 */
									if( ! isItemSkuEmpty )
									{
										/* 订单详情 qty_ordered 是否为空 */
										if( ! isQtyOrderedEmpty )
										{
											map.put("code", "0");
											
											map.put("message", "success");
											
											this.orderService.createAPIRespondOrder(shop, dtoOrder, map);
										}
										else
										{
											map.put("message", "Item Quantity could not be empty.");
										}
									}
									else
									{
										map.put("message", "Item SKU could not be empty.");
									}
								}
								else
								{
									map.put("message", "Receive Address could not be empty.");
								}
							}
							else
							{
								map.put("message", "Receive Phone could not be empty.");
							}
						}
						else
						{
							map.put("message", "Receive Name could not be empty.");
						}
					}
					else
					{
						map.put("message", "Order Item could not be empty.");
					}
				}
				else
				{
					map.put("message", "Delivery Method could not be empty.");
				}
			}
			else
			{
				map.put("message", "JSON data could not be empty.");
			}
		}
		else
		{
			map.put("code", "401");
			map.put("message", "Token is invalid.");
		}
		
		return map;
	}
	
	@RequestMapping(value = "/v1/orders", method = RequestMethod.PUT)
	public Map<String, Object> updateOrders(
			@RequestHeader("authorization") String authorization,
			@RequestHeader("ecomm-shopId") Long shopId,
			@RequestParam( value = "id", required = false ) Long orderId,
			@RequestParam( value = "sn", required = false ) String orderSn,
			@RequestBody( required = false ) DTO_Order dtoOrder)
	{
		
		Map<String, Object> map = new HashMap<>();
		
		Shop shop = shopService.getShop(shopId);
		
		/* 店铺验证必须通过 */
		if (shop != null && authorization.equals(shop.getToken()))
		{
			/* 必须传入订单信息 */
			if( dtoOrder != null )
			{
				this.orderService.updateAPIRespondOrder(shop, dtoOrder, orderId, orderSn, map);
			}
			else
			{
				map.put("message", "JSON data could not be empty.");
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
			this.orderService.deleteAPIRespondOrder(shop, orderId, orderSn, map);
		}
		else
		{
			map.put("code", "401");
			map.put("message", "Token is invalid.");
		}
		
		return map;
	}
}
