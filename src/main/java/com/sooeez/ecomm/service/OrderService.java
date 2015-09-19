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
import com.sooeez.ecomm.domain.ShopTunnel;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.repository.OrderBatchRepository;
import com.sooeez.ecomm.repository.OrderItemRepository;
import com.sooeez.ecomm.repository.OrderRepository;

@Service
public class OrderService {
	
	// Repository

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
							item.setAssignTunnel(tunnel);
							item.getAssignTunnel().setDefaultWarehouse(warehouse);
							exitShopTunnels = true;
							break;
						}
					}
				}
				if (!exitShopTunnels) {
					break;
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
			review.setReviewPass(false);
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
		Inventory inventoryQuery = new Inventory();
		List<Order> orders = review.getOrders();
		List<Long> warehouseIds = new ArrayList<>();
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
		warehouseIds.forEach(warehouseId ->{
			System.out.println(warehouseId);
		});
		inventoryQuery.setWarehouseIds(warehouseIds);
		
		List<Inventory> inventories = inventoryService.getInventories(inventoryQuery, null);
		List<Product> products = inventoryService.refreshInventory(inventories);
		products.forEach(product -> {
			System.out.println("product: " + product.getName());
			product.getWarehouses().forEach(warehouse -> {
				System.out.println("Warehouse: " + warehouse.getName() + ", " + warehouse.getTotal());
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
					for (Product product: products) {
						// 判断当前的item是否就是当前的产品
						if (item.getProduct().getSku().equals(product.getSku())) {
							// 循环当前产品的仓库
							for (Warehouse warehouse: product.getWarehouses()) {
								// 判断当前item指定的仓库是否在产品所在的仓库中
								if (item.getAssignTunnel().getDefaultWarehouse().getId().longValue() == warehouse.getId().longValue()) {
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
		
		review.getCheckMap().put("orderExistOutInventorySheet", false);
		
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
		
		this.confirmProductInventoryNotEnough(review);
		
		this.confirmOrderExistOutInventorySheet(review);
		
		return review;
	}
	
	public void confirmWarehouseExistOrderShipment(OperationReviewDTO review)
	{
		boolean isWarehouseExistSomeOrderShipment = false;
		
		for( Order order : review.getOrders() )
		{
			boolean isWarehouseExistOrderShipment = false;
			
			/* 订单没有发货单，则订单在同一仓库下存在发货单的条件不成立 */
			if( order.getShipments() != null && order.getShipments().size() < 1 )
			{
				for( Shipment shipment : order.getShipments() )
				{
					for( OrderItem item : order.getItems() )
					{
						if( shipment.getShipWarehouseId()!= null && shipment.getShipWarehouseId().equals( item.getAssignTunnel().getDefaultWarehouse().getId() ) )
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
			isWarehouseExistSomeOrderShipment = isWarehouseExistOrderShipment;
		}
		
		/* 某订单在同一仓库下存在发货单 */
		if( isWarehouseExistSomeOrderShipment )
		{
			review.getCheckMap().put("warehouseExistOrderShipmentError", true);
		}
		else
		{
			/* 不通过 */
			review.setReviewPass(false);
			review.getCheckMap().put("warehouseExistOrderShipmentError", false);
		}
	}

	public OperationReviewDTO confirmOrderWhenGenerateShipment(OperationReviewDTO review)
	{
		this.refreshOrdersBySelectedOrderIds(review);
		
		/* 获取订单列表 */
		List<Order> orders = review.getOrders();
		System.out.println("操作类型 ： " + review.getAction());
			
		/* 验证 1 ： 是否在统一仓库 */
		this.confirmDifferentWarehouse(review);

		
		/* 验证 2 ： 全部订单的发货方式是否相同 */
		Order lastOrder = review.getOrders().get( review.getOrders().size() - 1 );
		review.getCheckMap().put("differentDeliveryMethodError", false);
		for(Order order : orders)
		{
			if( ! order.getDeliveryMethod().equals(lastOrder.getDeliveryMethod())  )
			{
				/* 不通过 */
				review.setReviewPass(false);
				review.getCheckMap().put("differentDeliveryMethodError", true);
				break;
			}
		}
		
		
		/* 验证 3 ： 是否指定快递公司和起始快递单号 */
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
			review.setReviewPass(false);
			review.getCheckMap().put("emptyCourierAndShipNumberError", true);
		}
		
		
		/* 验证 4 ： 订单在同一仓库下是否已存在发货单 */
		this.confirmWarehouseExistOrderShipment(review);
		
		
		/* 验证 5 ： 订单的收货地址是否为空 */
		boolean isReceiveAddressEmpty = false;
		for(Order order : orders)
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
		if( isReceiveAddressEmpty )
		{
			/* 不通过 */
			review.setReviewPass(false);
		}
		review.getCheckMap().put("emptyReceiveAddressError", isReceiveAddressEmpty);
		
		
		/* 如果验证全都通过，并且操作类型是 CONFIRM 则执行创建操作 */
		if(review.isReviewPass() && review.getAction().equals(OperationReviewDTO.CONFIRM));
		{
			System.out.println("生成发货单操作复核检查项全部验证通过");
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
