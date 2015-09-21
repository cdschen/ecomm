package com.sooeez.ecomm.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.Courier;
import com.sooeez.ecomm.domain.Inventory;
import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.domain.Order;
import com.sooeez.ecomm.domain.OrderBatch;
import com.sooeez.ecomm.domain.OrderItem;
import com.sooeez.ecomm.domain.OrderItem;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.ProductShopTunnel;
import com.sooeez.ecomm.domain.Shipment;
import com.sooeez.ecomm.domain.ShipmentItem;
import com.sooeez.ecomm.domain.ShopTunnel;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.repository.OrderBatchRepository;
import com.sooeez.ecomm.repository.OrderItemRepository;
import com.sooeez.ecomm.repository.OrderRepository;
import com.sooeez.ecomm.repository.ShipmentRepository;

@Service
public class OrderService {
	
	// Repository
	
	@Autowired private ShipmentRepository shipmentRepository;

	@Autowired private OrderRepository orderRepository;
	
	@Autowired private OrderItemRepository orderItemRepository;
	
	@Autowired private OrderBatchRepository orderBatchRepository;
	
	// Service
	
	@Autowired private InventoryService inventoryService;
	
	@Autowired private ShopService shopService;
	
	@PersistenceContext private EntityManager em;

	/*
	 * Order
	 */
	
	@Transactional
	public Order saveOrder(Order order) {
		/* If id not null then is edit action */
		if (order.getId() == null) {
			order.setInternalCreateTime(new Date());
		}
		/* execute no matter create or update */
		order.setLastUpdateTime(new Date());

		Integer qtyTotalItemOrdered = 0;
		Integer weight = 0;
		BigDecimal grandTotal = new BigDecimal(0);
		BigDecimal subtotal = new BigDecimal(0);
		BigDecimal tax = new BigDecimal(0);

		/* If order items is not empty then handle some operation */
		if (order.getItems() != null && order.getItems().size() > 0) {

			for (OrderItem orderItem : order.getItems()) {
				if (orderItem.getQtyOrdered() != null && orderItem.getUnitWeight() != null) {
					/* Accumulate total weight */
					weight += orderItem.getQtyOrdered() * orderItem.getUnitWeight();
				}
				if (orderItem.getQtyOrdered() != null) {
					/* Accumulate total items ordered */
					qtyTotalItemOrdered += orderItem.getQtyOrdered();
				}
				if (orderItem.getQtyOrdered() != null && orderItem.getUnitPrice() != null) {
					/* Accumulate grand total */
					grandTotal = grandTotal.add(orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getQtyOrdered())));
					subtotal = subtotal.add(orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getQtyOrdered())));
				}
			}

			if (order.getShippingFee() != null) {
				grandTotal = grandTotal.add(order.getShippingFee());
			}
			if (order.getSubtotal() != null) {
				tax = order.getSubtotal().multiply(new BigDecimal(0.15));
			}
		}

		/* Handled completed */
		order.setWeight(weight);
		order.setQtyTotalItemOrdered(qtyTotalItemOrdered);
		order.setGrandTotal(grandTotal);
		order.setSubtotal(subtotal);
		order.setTax(tax);

		return this.orderRepository.save(order);
	}

	@Transactional
	public void deleteOrder(Long id) {
		this.orderRepository.delete(id);
	}

	public Order getOrder(Long id) {
		return this.orderRepository.findOne(id);
	}

	public List<Order> getOrders(Order order, Sort sort) {
		return this.orderRepository.findAll(getOrderSpecification(order), sort);
	}

	public Page<Order> getPagedOrders(Order order, Pageable pageable) {
		return this.orderRepository.findAll(getOrderSpecification(order), pageable);
	}
	
	public Page<Order> getPagedOrdersForOrderDeploy(Order order, Pageable pageable) {

		long assginWarehouseId = order.getWarehouseId() != null ? order.getWarehouseId().longValue() : 0;
		
		findAvailableDeployOrder(order);
		
		Page<Order> page = this.orderRepository.findAll(getOrderSpecification(order), pageable);
		if (page != null && page.getContent() != null && page.getContent().size() > 0 && assginWarehouseId > 0) {
			filterItemsForOrder(page.getContent(), assginWarehouseId);
		}
		return page;
	}
	
	public void findAvailableDeployOrder(Order order) {
		// 查询出可配货的订单的  id
		String sqlString = "select distinct(`order`.id) from t_order as `order`, "
				+ "t_order_item as orderItem, "
				+ "t_object_process as process, "
				+ "t_shop as shop "
				+ "where `order`.id = orderItem.order_id "
				+ "and `order`.id = process.object_id "
				+ "and process.object_type = 1 "
				+ "and `order`.shop_id = shop.id "
				+ "and shop.deploy_process_step_id = process.step_id "
				+ "and `order`.deleted = 0 ";
		if (order.getInternalCreateTimeStart() != null && order.getInternalCreateTimeEnd() != null) {
			sqlString += "and `order`.internal_create_time between '" + order.getInternalCreateTimeStart() + "' "
					+ "and '" + order.getInternalCreateTimeEnd() + "'";
			order.setInternalCreateTimeStart(null);
			order.setInternalCreateTimeEnd(null);
		} else if (order.getInternalCreateTimeStart() != null) {
			sqlString += " and `order`.internal_create_time >= '" + order.getInternalCreateTimeStart() + "'";
			order.setInternalCreateTimeStart(null);
		} else if (order.getInternalCreateTimeEnd() != null) {
			sqlString += " and `order`.internal_create_time <= '" + order.getInternalCreateTimeEnd() + "'";
			order.setInternalCreateTimeEnd(null);
		}
		if (order.getShopId() != null) {
			sqlString += " and `order`.shop_id = " + order.getShopId();
			order.setShopId(null);;
		}
		if (StringUtils.hasText(order.getExternalSn())) {
			sqlString += " and `order`.external_sn like '%" + order.getExternalSn() + "%'";
			order.setExternalSn(null);;
		}
		if (StringUtils.hasText(order.getReceiveName())) {
			sqlString += " and `order`.receive_name like '%" + order.getReceiveName() + "%'";
			order.setReceiveName(null);;
		}
		if (order.getWarehouseId() != null) {
			sqlString += " and(orderItem.warehouse_id = " + order.getWarehouseId()
					+ " or("
					+ "orderItem.warehouse_id is null"
					+ " and(exists(select 1"
					+ " from t_product_shop_tunnel as productShopTunnel, "
					+ "t_shop_tunnel as shopTunnel1 "
					+ "where shopTunnel1.default_warehouse_id = " + order.getWarehouseId()
					+ " and shopTunnel1.id = productShopTunnel.tunnel_id "
					+ "and productShopTunnel.product_id = orderItem.product_id) "
					+ "or "
					+ "exists(select 1 from t_shop_tunnel as shopTunnel2 "
					+ "where shopTunnel2.shop_id = shop.id "
					+ "and shopTunnel2.default_option = 1 "
					+ "and shopTunnel2.default_warehouse_id = " + order.getWarehouseId() + ")"
					+ ")))";
			order.setWarehouseId(null);
		}
		
		order.setOrderIds(new ArrayList<>());
		em.createNativeQuery(sqlString).getResultList().forEach(orderId -> {
			order.getOrderIds().add(Long.parseLong(orderId.toString()));
		});
		
		if (order.getOrderIds().size() == 0) {
			order.getOrderIds().add(0L);
		}
	}
	
	public void filterItemsForOrder(List<Order> orders, long assginWarehouseId) {
		for (Order _order: orders) {
			System.out.println("order: " + _order.getId());
			
			List<OrderItem> deletedItems = new ArrayList<>();
			for (int i = 0, len = _order.getItems().size(); i < len; i++) {
				OrderItem item = _order.getItems().get(i);
				System.out.println("item: " + item.getId());
				
				// 1.判断item有没有设置指定的warehouseid, 并且是否等于查询的warehouseId
				if (item.getWarehouseId() != null && item.getWarehouseId().longValue() == assginWarehouseId) {
					continue;
				} else {
					// 2.判断item关联的产品有没有配置产品店铺通道,
					boolean exit = false;
					if (item.getProduct().getShopTunnels() != null && item.getProduct().getShopTunnels().size() > 0) {
						// 如果有，找到配置的产品店铺通道
						boolean match = false;
						for (ProductShopTunnel productShopTunnel: item.getProduct().getShopTunnels()) {
							if (productShopTunnel.getShopId().longValue() == _order.getShopId().longValue()) {
								match = true;
								// 匹配到店铺
								// 循环店铺通道
								for (ShopTunnel shopTunnel: _order.getShop().getTunnels()) {
									// 匹配到通道
									if (shopTunnel.getId().longValue() == productShopTunnel.getTunnelId().longValue()) {
										if (shopTunnel.getDefaultWarehouseId().longValue() == assginWarehouseId) {
											break;
										} else {
											deletedItems.add(item);
											exit = true;
											break;
										}
									}
								}
							}
							if (exit) {
								break;
							}
						}
						
						if (!match) {
							// 3.判断订单店铺下的默认通道的默认仓库是否和查询的warehouseId相等
							for (ShopTunnel shopTunnel: _order.getShop().getTunnels()) {
								if (shopTunnel.getDefaultOption()) {
									if (shopTunnel.getDefaultWarehouseId().longValue() == assginWarehouseId) {
										break;
									} else {
										deletedItems.add(item);
										break;
									}
								}
							}
						}
					} else {
						// 3.判断订单店铺下的默认通道的默认仓库是否和查询的warehouseId相等
						for (ShopTunnel shopTunnel: _order.getShop().getTunnels()) {
							if (shopTunnel.getDefaultOption()) {
								if (shopTunnel.getDefaultWarehouseId().longValue() == assginWarehouseId) {
									break;
								} else {
									deletedItems.add(item);
									break;
								}
							}
						}
					}
				}
			}
			
			if (deletedItems.size() > 0) {
				_order.getItems().removeAll(deletedItems);
			}
		}
	}
	
	/* 检查order下的item,是否有指定的默认仓库，如果没有则赋值 */
	public void checkItemProductShopTunnel(Order order) {
		System.out.println("checkItemProductShopTunnel===========");
		// 循环items
		for (OrderItem item: order.getItems()) {
			// 判断当前item上是否有指定仓库
			if (item.getWarehouseId() != null) {
				boolean exitShopTunnels = false;
				// 循环当前订单所属店铺的所有通道
				for (ShopTunnel tunnel: order.getShop().getTunnels()) {
					// 循环每个通道下的仓库
					for (Warehouse warehouse: tunnel.getWarehouses()) {
						// 判断当前仓库id是否和item上指定的仓库id一样
						if (warehouse.getId().longValue() == item.getWarehouseId().longValue()) {
							// 设置指定通道，和设置指定通道的默认仓库
							ShopTunnel assignTunnel = new ShopTunnel();
							BeanUtils.copyProperties(tunnel, assignTunnel);
							item.setAssignTunnel(assignTunnel);
							item.getAssignTunnel().setDefaultWarehouse(warehouse);
							System.out.println("item assign tunnel, orderid:" + order.getId() + ", itemid:" + item.getId() + ", tunnelid:" + tunnel.getId() + ", warehouseId:" + warehouse.getId());
							exitShopTunnels = true;
							break;
						}
					}
					if (exitShopTunnels) {
						break;
					}
				}
				
			} else {
				// 如果item上没有指定仓库，检查item关联的商品上是否有设置商品通道
				if (item.getProduct().getShopTunnels().size() > 0) {
					boolean match = false;
					// 循环item关联商品的设置的通道
					for (ProductShopTunnel productShopTunnel: item.getProduct().getShopTunnels()) {
						// 判断当前商品通道的店铺是不是订单的店铺
						if (productShopTunnel.getShopId().longValue() == order.getShop().getId().longValue()) {
							match = true;
							// 循环店铺通道
							for (ShopTunnel tunnel: order.getShop().getTunnels()) {
								// 判断店铺通过的id是不是和商品指定通道的id相等
								if (tunnel.getId().longValue() == productShopTunnel.getTunnelId()) {
									item.setAssignTunnel(tunnel);
									for (Warehouse warehouse: tunnel.getWarehouses()) {
										// 判断当前仓库id是否和item上指定的仓库id一样
										if (warehouse.getId().longValue() == item.getAssignTunnel().getDefaultWarehouseId().longValue()) {
											item.getAssignTunnel().setDefaultWarehouse(warehouse);
											break;
										}
									}
									break;
								}
							}
							break;
						}
					}
					
					// 如果商品指定的通道没有一个和当前订单店铺的通道吻合
					if (!match) {
						item.setAssignTunnel(order.getShop().getDefaultTunnel());
					}
				} else { // item没有设置商品通道
					item.setAssignTunnel(order.getShop().getDefaultTunnel());
				}
			}
		}
	}
	
	/* 先收集出，需要出库的item都来自那些仓库  */
	public Inventory collectWarehouseIds(List<Order> orders) {
		Inventory inventoryQuery = new Inventory();
		List<Long> warehouseIds = new ArrayList<>();
		// 先收集出，需要出库的item都来自那些仓库
		for (Order order: orders) {
			if (!order.getIgnoreCheck()) {
				for (OrderItem item: order.getItems()) {
					boolean exist = false;
					if (item.getAssignTunnel() != null) {
						for (Long warehouseId: warehouseIds){
							if (warehouseId.longValue() == item.getAssignTunnel().getDefaultWarehouse().getId().longValue()) {
								exist = true;
								break;
							} else {
								exist = false;
							}
						}
						if (!exist) {
							warehouseIds.add(item.getAssignTunnel().getDefaultWarehouse().getId());
						}
					}
				}
			}
		}
		System.out.println("confirmDifferentWarehouse; warehouseIds:");
		warehouseIds.forEach(warehouseId -> {
			System.out.println(warehouseId);
		});
		inventoryQuery.setWarehouseIds(warehouseIds);
		return inventoryQuery;
	}
	
	/* 验证订单是否都在同一个仓库 */
	public void confirmDifferentWarehouse(OperationReviewDTO review) {
		
		List<Long> sameWarehouseIds = new ArrayList<>();
		boolean differentWarehouseError = false;
		List<Order> orders = review.getOrders();
		// 循环 order
		for (Order order: orders) {
			// 当order没有被移出的时候才进行判断
			if (!order.getIgnoreCheck()) {
				// 循环 order item
				for (OrderItem item: order.getItems()) {
					if (item.getAssignTunnel() != null) {
						sameWarehouseIds.add(item.getAssignTunnel().getDefaultWarehouse().getId());
						for (Long warehouseId : sameWarehouseIds) {
							if (warehouseId.longValue() != item.getAssignTunnel().getDefaultWarehouse().getId().longValue()) {
								System.out.println(item.getId() + ":" + item.getAssignTunnel().getDefaultWarehouse().getId());
								differentWarehouseError = true;
								break;
							}
						}
					}
					if (differentWarehouseError) {
						break;
					}
				}
				if (differentWarehouseError) {
					break;
				}
			}
			
		}
		
		if (differentWarehouseError) {
			for (Order order: orders) {
				order.getCheckMap().put("differentWarehouseError", true);
			}
			review.getCheckMap().put("differentWarehouseError", true);
		} else {
			for (Order order: orders) {
				order.getCheckMap().put("differentWarehouseError", false);
			}
			review.getCheckMap().put("differentWarehouseError", false);
		}
	}

	/* 全部订单商品细目必须有库存 */
	public void confirmProductInventoryNotEnough(OperationReviewDTO review) {
		
		List<Order> orders = review.getOrders();
		Inventory inventoryQuery = this.collectWarehouseIds(review.getOrders());
		
		// 查询出这些仓库的每个商品的库存
		List<Inventory> inventories = inventoryService.getInventories(inventoryQuery, null);
		List<Product> products = inventoryService.refreshInventory(inventories);
		// 循环出每个商品下在每个仓库中的库存
		products.forEach(product -> {
			System.out.println("product: " + product.getName());
			product.getWarehouses().forEach(warehouse -> {
				System.out.println("WarehouseId: " + warehouse.getId() + ", " + warehouse.getTotal());
			});
		});
		
		List<OrderItem> issueItems = new ArrayList<>();
		
		// 重置review的productInventoryNotEnoughError为false
		review.getCheckMap().put("productInventoryNotEnoughError", false);
		
		for (Order order: orders) {
			
			if (!order.getIgnoreCheck()) {
				// 判断前先把每一个order的productInventoryNotEnoughError设置为false
				order.getCheckMap().put("productInventoryNotEnoughError", false);
				// 循环item
				for (OrderItem item: order.getItems()) {
					// 循环products
					boolean exitProductsEach = false;
					boolean matchItemInventory = false;
					for (Product product: products) {
						// 判断当前的item是否就是当前的产品
						if (item.getProduct().getSku().equals(product.getSku())) {
							
							// 循环当前产品的仓库
							for (Warehouse warehouse: product.getWarehouses()) {
								// 判断当前item指定的仓库是否在产品所在的仓库中
								if (item.getAssignTunnel().getDefaultWarehouse().getId().longValue() == warehouse.getId().longValue()) {
									matchItemInventory = true;
									warehouse.setTotal(warehouse.getTotal().longValue() - item.getQtyOrdered().longValue());
									System.out.println("orderid:" + order.getId() + ", itemid:" + item.getId() + "," + item.getProduct().getName() + "," + item.getAssignTunnel().getDefaultWarehouse().getName() + "," + warehouse.getTotal());
									if (warehouse.getTotal().longValue() < 0) { 
										issueItems.add(item);
									}
									exitProductsEach = true;
									break;
								}
							}
						} 
						if (exitProductsEach) {
							break;
						}
					}
					// 如果没有匹配到item的库存信息，也是一个问题item
					if (!matchItemInventory) {
						issueItems.add(item);
					}
				}
			}
			
		}
		
		for (OrderItem issueItem: issueItems) {
			for (Order order: orders) {
				if (!order.getIgnoreCheck()) {
					for (OrderItem item: order.getItems()) {
						if (item.getProduct().getSku().equals(issueItem.getProduct().getSku())
								&& item.getAssignTunnel().getDefaultWarehouse().getId().longValue() == issueItem.getAssignTunnel().getDefaultWarehouse().getId().longValue()) {
							order.getCheckMap().put("productInventoryNotEnoughError", true);
							review.getCheckMap().put("productInventoryNotEnoughError", true);
							break;
						}
					}
				}
				
			}
		}
	}
	
	/* 订单在同一个仓库只能有一张出库单 */
	public void confirmOrderExistOutInventorySheet(OperationReviewDTO review) {
		
		List<Order> orders = review.getOrders();
		
		review.getCheckMap().put("orderExistOutInventorySheetError", false);
		
		// 循环 orders
		for (Order order: orders) {
			if (!order.getIgnoreCheck()) {
				order.getCheckMap().put("orderExistOutInventorySheetError", false);
				boolean exitItemsEach = false;
				for (OrderItem item: order.getItems()) {
					if (item.getAssignTunnel() != null) {
						for (OrderBatch orderBatch: order.getBatches()) {
							if (orderBatch.getWarehouseId().longValue() == item.getAssignTunnel().getDefaultWarehouse().getId().longValue()) {
								order.getCheckMap().put("orderExistOutInventorySheetError", true);
								review.getCheckMap().put("orderExistOutInventorySheetError", true);
								exitItemsEach = true;
								break;
							}
						}
					}
					if (exitItemsEach) {
						break;
					}
				}
			}
		}
	}
	
	/* 通过orderids重新查询一遍所选的orders */
	public void refreshOrdersBySelectedOrderIds(OperationReviewDTO review) {
		// 再次查询选中的orders
		Order order = new Order();
		List<Long> moveOutOrderIds = new ArrayList<>();
		review.getOrders().forEach(o -> {
			order.getOrderIds().add(o.getId());
			if (o.getIgnoreCheck()) {
				moveOutOrderIds.add(o.getId());
			}
		});
		List<Order> newOrders = this.getOrders(order, new Sort(Sort.Direction.DESC, "internalCreateTime"));
		newOrders.forEach(o -> {
			// 重新设置移出的order
			for (Long moveOutOrderId: moveOutOrderIds) {
				if (moveOutOrderId.longValue() == o.getId()) {
					o.setIgnoreCheck(true);
					break;
				}
			}
			this.shopService.initShopDefaultTunnel(o.getShop());
			this.checkItemProductShopTunnel(o);
		});
		
		if (review.getAssignWarehouseId() != null) {
			this.filterItemsForOrder(newOrders, review.getAssignWarehouseId());
		}
		
		review.setOrders(newOrders);
	}
	
	public OperationReviewDTO confirmOrderWhenGenerateOutInventory(OperationReviewDTO review) {
		
		this.refreshOrdersBySelectedOrderIds(review);
		
		this.confirmDifferentWarehouse(review);
		
		if (!review.getIgnoredMap().get("productInventoryNotEnough")) {
			this.confirmProductInventoryNotEnough(review);
		}
		
		if (!review.getIgnoredMap().get("orderExistOutInventorySheet")) {
			this.confirmOrderExistOutInventorySheet(review);
		}
		
		return review;
	}

	/* 验证 2 ： 全部订单的发货方式是否相同 */
	public void confirmOrderDeliveryMethodSame(OperationReviewDTO review)
	{
		review.getCheckMap().put("differentDeliveryMethodError", false);
		List<Order> orders = review.getOrders();
		
		Order lastOrder = review.getOrders().get( review.getOrders().size() - 1 );
		for(Order order : orders)
		{
			/* 没有被移出 */
			if( ! order.getIgnoreCheck() )
			{
				if( order.getDeliveryMethod() == null || ! order.getDeliveryMethod().equals(lastOrder.getDeliveryMethod())  )
				{
					/* 不通过 */
					review.getCheckMap().put("differentDeliveryMethodError", true);
					break;
				}
			}
		}
	}

	/* 验证 3 ： 是否指定快递公司和起始快递单号 */
	public void confirmSpecifyCourierAndStartShipNumber(OperationReviewDTO review)
	{
		Courier selectedCourier = (Courier) review.getSelectedCourier();
		String startShipNumber = (String) review.getDataMap().get("startShipNumber");
		if(selectedCourier != null &&
		  (startShipNumber != null && ! startShipNumber.equals("")))
		{
			review.getCheckMap().put("emptyCourierAndShipNumberError", false);
		}
		else
		{
			/* 不通过 */
			review.getCheckMap().put("emptyCourierAndShipNumberError", true);
		}
	}

	/* 验证 4 ： 订单在同一仓库下是否已存在发货单 */
	public void confirmWarehouseExistOrderShipment(OperationReviewDTO review)
	{
		boolean isWarehouseExistSomeOrderShipment = false;
		for( Order order : review.getOrders() )
		{
			boolean isWarehouseExistOrderShipment = false;

			/* 没有被移出 */
			if( ! order.getIgnoreCheck() )
			{
				/* 订单有发货单，则订单在同一仓库下存在发货单的条件才有可能会成立 */
				if( order.getShipments() != null && order.getShipments().size() > 0 )
				{
					for( Shipment shipment : order.getShipments() )
					{
						for( OrderItem item : order.getItems() )
						{
							if( shipment.getShipWarehouseId().equals( item.getAssignTunnel().getDefaultWarehouse().getId() ) )
							{
								isWarehouseExistOrderShipment = true;
							}
						}
					}
				}

				/* 订单在同一仓库下存在发货单 */
				if( isWarehouseExistOrderShipment )
				{
					order.getCheckMap().put("warehouseExistOrderShipmentError", true);
				}
				else
				{
					order.getCheckMap().put("warehouseExistOrderShipmentError", false);
				}
			}
			
			/* 如果有一个订单在同一仓库下存在发货单，则有某一个订单在同一仓库下存在发货单成立 */
			if( isWarehouseExistOrderShipment )
			{
				isWarehouseExistSomeOrderShipment = isWarehouseExistOrderShipment;
			}
		}
		
		/* 某订单在同一仓库下存在发货单，并且不取消该验证 */
		if( isWarehouseExistSomeOrderShipment )
		{
			review.getCheckMap().put("warehouseExistOrderShipmentError", true);
		}
		else
		{
			/* 不通过 */
			review.getCheckMap().put("warehouseExistOrderShipmentError", false);
		}
	}

	/* 验证 5 ： 订单的收货地址是否为空 */
	public void confirmEmptyReceiveAddress(OperationReviewDTO review)
	{
		List<Order> orders = review.getOrders();
		
		boolean isReceiveAddressEmpty = false;
		for(Order order : orders)
		{
			/* 没有被移出 */
			if( ! order.getIgnoreCheck() )
			{
				if( order.getReceiveAddress() == null || order.getReceiveAddress().trim().equals("")  )
				{
					/* 当前订单不通过 */
					isReceiveAddressEmpty = true;
					order.getCheckMap().put("emptyReceiveAddressError", true);
				}
				else
				{
					/* 当前订单通过 */
					order.getCheckMap().put("emptyReceiveAddressError", false);
				}
			}
		}
		review.getCheckMap().put("emptyReceiveAddressError", isReceiveAddressEmpty);
	}

	/* 设置是否通过 */
	public void setConfirmable(OperationReviewDTO review)
	{
		/* 如果验证全都通过 */
		if( ! review.getCheckMap().get("differentWarehouseError") &&
			! review.getCheckMap().get("differentDeliveryMethodError") &&
			! review.getCheckMap().get("emptyCourierAndShipNumberError") &&
			! review.getCheckMap().get("warehouseExistOrderShipmentError") &&
			! review.getCheckMap().get("emptyReceiveAddressError") )
		{
			review.setConfirmable( true );
		}
		else
		{
			/* 否则有不通过的，则再看看有没有不通过但是已取消的验证 */
			boolean isDifferentWarehouseError = false;
			boolean isDifferentDeliveryMethodError = false;
			boolean isEmptyCourierAndShipNumberError = false;
			boolean isWarehouseExistOrderShipmentError = false;
			boolean isEmptyReceiveAddressError = false;
			
			/* 不在同一仓库，并且没有取消验证 */
			if( review.getCheckMap().get("differentWarehouseError") && ! review.getIgnoredMap().get("differentWarehouseError") )
			{
				isDifferentWarehouseError = true;
			}
			/* 发货方式不同，并且没有取消验证 */
			if( review.getCheckMap().get("differentDeliveryMethodError")  && ! review.getIgnoredMap().get("differentDeliveryMethodError") )
			{
				isDifferentDeliveryMethodError = true;
			}/* 没有指定快递公司或填写起始快递单号，并且没有取消验证 */
			if( review.getCheckMap().get("emptyCourierAndShipNumberError") && ! review.getIgnoredMap().get("emptyCourierAndShipNumberError") )
			{
				isEmptyCourierAndShipNumberError = true;
			}
			/* 订单在同一仓库下存在发货单，并且没有取消验证 */
			if( review.getCheckMap().get("warehouseExistOrderShipmentError") && ! review.getIgnoredMap().get("warehouseExistOrderShipmentError") )
			{
				isWarehouseExistOrderShipmentError = true;
			}
			/* 订单没有填写收件地址，并且没有取消验证 */
			if( review.getCheckMap().get("emptyReceiveAddressError") && ! review.getIgnoredMap().get("emptyReceiveAddressError") )
			{
				isEmptyReceiveAddressError = true;
			}
			
			/* 如果有一个验证不通过 */
			if( isDifferentWarehouseError ||
				isDifferentDeliveryMethodError ||
				isEmptyCourierAndShipNumberError ||
				isWarehouseExistOrderShipmentError ||
				isEmptyReceiveAddressError )
			{
				review.setConfirmable( false );
			}
			else
			{
				/* 否则全都通过 */
				review.setConfirmable( true );
			}
			
		}
	}

	/* 如果验证全都通过，并且操作类型是 CONFIRM 则执行创建操作 */
	public void executeShipmentGeneration(OperationReviewDTO review)
	{
		/* 假设有可生成发货单的订单 */
		review.getResultMap().put("isEmptyFinalOrders", false);
		
		List<Order> orders = review.getOrders();
		List<Order> shipmentGenerableOrders = new ArrayList<Order>();
		for(Order order : orders)
		{
			/* 如果订单没有被移出，则添加到最终订单列表中 */
			if( ! order.getIgnoreCheck() )
			{
				shipmentGenerableOrders.add(order);
			}
		}
		
		/* 如果有可生成发货单的订单 */
		if( shipmentGenerableOrders.size() > 0 )
		{
			/* 取得操作员信息 */
			Number operatorId = (Number) review.getDataMap().get("operatorId");
			Long finalOperatorId = operatorId.longValue();
			
			/* 取得快递公司及起始快递单号 */
			Long courierId = review.getSelectedCourier() != null ? review.getSelectedCourier().getId() : null;
			String startShipNumber = (String) review.getDataMap().get("startShipNumber");
			

			Integer initStartShipmentNumber = 0;
			if( startShipNumber != null && ! startShipNumber.trim().equals("") )
			{
				initStartShipmentNumber = Integer.valueOf( startShipNumber );
			}
			
			
			for( Order order : shipmentGenerableOrders )
			{
				/* 给发货单初始化数据 */
				Shipment shipment = new Shipment();

				/* 给发货单初始化创建人 */
				shipment.setOperatorId( finalOperatorId );
				
				/* 给发货单初始化快递公司编号及起始快递单号 */
				shipment.setCourierId( courierId );
				
				if( startShipNumber != null && ! startShipNumber.trim().equals("") )
				{
					shipment.setShipNumber( initStartShipmentNumber.toString() );
					initStartShipmentNumber ++ ;
				}
				

				/* 给发货单初始化余下数据 */
				shipment.setOrderId( order.getId() );
				/* 1 : 待取件 */
				shipment.setShipStatus( 1 );
				/* 初始化发件人数据 */
				shipment.setSenderName( order.getSenderName() );
				shipment.setSenderAddress( order.getSenderAddress() );
				shipment.setSenderPhone( order.getSenderPhone() );
				shipment.setSenderEmail( order.getSenderEmail() );
				shipment.setSenderPost( order.getSenderPost() );
				/* 初始化收件人数据 */
				shipment.setReceiveName( order.getReceiveName() );
				shipment.setReceivePhone( order.getReceivePhone() );
				shipment.setReceiveEmail( order.getReceiveEmail() );
				shipment.setReceiveCountry( order.getReceiveCountry() );
				shipment.setReceiveProvince( order.getReceiveProvince() );
				shipment.setReceiveCity( order.getReceiveCity() );
				shipment.setReceiveAddress( order.getReceiveAddress() );
				shipment.setReceivePost( order.getReceivePost() );

				/* 已发货商品总件数 */
				/* 商品相关数据初始化 */
				Integer qtyTotalItemShipped = 0;
				BigDecimal bigZero = new BigDecimal( 0 );
				BigDecimal shipFeeCost = new BigDecimal( 0 );
				Integer totalWeight = 0;
				/* 初始化发货商品数据 */
				List<ShipmentItem> shipmentItems = new ArrayList<ShipmentItem>();
				for( OrderItem orderItem : order.getItems() )
				{
					/* 叠加每件商品的数量，成本，重量 */
					qtyTotalItemShipped += orderItem.getQtyOrdered();
					shipFeeCost = shipFeeCost.add( orderItem.getUnitCost() != null ? orderItem.getUnitCost() : bigZero );
					totalWeight += orderItem.getQtyOrdered() * orderItem.getUnitWeight();
					
					ShipmentItem shipmentItem = new ShipmentItem();
					shipmentItem.setOrderItemId( orderItem.getId() );
					shipmentItem.setQtyShipped( orderItem.getQtyOrdered() );
					
					/* 关联发货单和发货详情 */
					shipmentItems.add( shipmentItem );
				}
				shipment.setShipmentItems( shipmentItems );
				shipment.setQtyTotalItemShipped( qtyTotalItemShipped );
				shipment.setShipfeeCost( shipFeeCost );
				shipment.setTotalWeight( totalWeight );
				
				/* 获取仓库编号 */
				Long warehouseId = order.getItems().get(0).getAssignTunnel().getDefaultWarehouse().getId();
				shipment.setShipWarehouseId( warehouseId );
				
				/* 所有数据已准备就绪，初始化［创建时间］和［最后更新时间］并生成发货单 */
				shipment.setCreateTime( new Date() );
				shipment.setLastUpdate( new Date() );
				this.shipmentRepository.save(shipment);
			}
			review.getResultMap().put("generatedShipmentCount", shipmentGenerableOrders.size());
		}
		else
		{
			/* 如果没有最终订单 */
			review.getResultMap().put("isEmptyFinalOrders", true);
		}
	}

	public OperationReviewDTO confirmOrderWhenGenerateShipment(OperationReviewDTO review)
	{
		this.refreshOrdersBySelectedOrderIds(review);
		
		/* 验证 1 ： 是否在同一仓库 */
		this.confirmDifferentWarehouse(review);

		
		/* 验证 2 ： 全部订单的发货方式是否相同 */
		this.confirmOrderDeliveryMethodSame(review);
		
		
		/* 验证 3 ： 是否指定快递公司和起始快递单号 */
		this.confirmSpecifyCourierAndStartShipNumber(review);
		
		
		/* 验证 4 ： 订单在同一仓库下是否已存在发货单 */
		this.confirmWarehouseExistOrderShipment(review);
		
		
		/* 验证 5 ： 订单的收货地址是否为空 */
		this.confirmEmptyReceiveAddress(review);
		
		
		/* 设置可确认性 */
		this.setConfirmable(review);
		
		
		/* 如果验证全都通过，并且操作类型是 CONFIRM 则执行创建操作 */
		if( review.isConfirmable() &&
			review.getAction().equals(OperationReviewDTO.CONFIRM) )
		{
			/* 执行生成发货单操作 */
			this.executeShipmentGeneration( review );
		}
		
		return review;
		
	}
	
	private Specification<Order> getOrderSpecification(Order order) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("deleted"), order.getDeleted() != null && order.getDeleted() ? true : false));
			if (order.getOrderId() != null) {
				predicates.add(cb.equal(root.get("id"), order.getOrderId()));
			}
			if (order.getShopId() != null) {
				predicates.add(cb.equal(root.get("shopId"), order.getShopId()));
			}
			if (StringUtils.hasText(order.getReceiveName())) {
				predicates.add(cb.like(root.get("receiveName"), "%" + order.getReceiveName() + "%"));
			}
			if (order.getInternalCreateTimeStart() != null && order.getInternalCreateTimeEnd() != null) {
				try {
					predicates.add(cb.between(root.get("internalCreateTime"),
							new SimpleDateFormat("yyyy-MM-dd").parse(order.getInternalCreateTimeStart()),
							new SimpleDateFormat("yyyy-MM-dd").parse(order.getInternalCreateTimeEnd())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (order.getInternalCreateTimeStart() != null) {
				try {
					predicates.add(cb.greaterThanOrEqualTo(root.get("internalCreateTime"), 
							new SimpleDateFormat("yyyy-MM-dd").parse(order.getInternalCreateTimeStart())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (order.getInternalCreateTimeEnd() != null) {
				try {
					predicates.add(cb.lessThanOrEqualTo(root.get("internalCreateTime"), 
							new SimpleDateFormat("yyyy-MM-dd").parse(order.getInternalCreateTimeEnd())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (order.getStatusIds() != null) {
				Subquery<ObjectProcess> objectProcessSubquery = query.subquery(ObjectProcess.class);
				Root<ObjectProcess> objectProcessRoot = objectProcessSubquery.from(ObjectProcess.class);
				objectProcessSubquery.select(objectProcessRoot.get("objectId"));
				objectProcessSubquery.where(objectProcessRoot.get("stepId").in(order.getStatusIds()));
				predicates.add(cb.in(root.get("id")).value(objectProcessSubquery));
			}
			if (order.getOrderIds() != null && order.getOrderIds().size() > 0) {
				predicates.add(cb.in(root.get("id")).value(order.getOrderIds()));
			}
			
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
	
	
	/*
	 * OrderItem
	 */
	
	public OrderItem saveOrderItem(OrderItem orderItem) {
		return this.orderItemRepository.save(orderItem);
	}

	public void deleteOrderItem(Long id) {
		this.orderItemRepository.delete(id);
	}

	public OrderItem getOrderItem(Long id) {
		return this.orderItemRepository.findOne(id);
	}

	public List<OrderItem> getOrderItems(Sort sort) {
		return this.orderItemRepository.findAll(sort);
	}

	public Page<OrderItem> getPagedOrderItems(Pageable pageable) {
		return this.orderItemRepository.findAll(pageable);
	}
	
	/*
	 * OrderBatch
	 */
	
	public OrderBatch saveOrderBatch(OrderBatch orderBatch) {
		return this.orderBatchRepository.save(orderBatch);
	}

	public void deleteOrderBatch(Long id) {
		this.orderBatchRepository.delete(id);
	}

	public OrderBatch getOrderBatch(Long id) {
		return this.orderBatchRepository.findOne(id);
	}

	public List<OrderBatch> getOrderBatches(Sort sort) {
		return this.orderBatchRepository.findAll(sort);
	}

	public Page<OrderBatch> getPagedOrderBatches(Pageable pageable) {
		return this.orderBatchRepository.findAll(pageable);
	}
}
