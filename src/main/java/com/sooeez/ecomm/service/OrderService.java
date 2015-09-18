package com.sooeez.ecomm.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.Inventory;
import com.sooeez.ecomm.domain.OrderBatch;
import com.sooeez.ecomm.domain.OrderItem;
import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.domain.Order;
import com.sooeez.ecomm.domain.OrderItem;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.ProductShopTunnel;
import com.sooeez.ecomm.domain.ShopTunnel;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.repository.OrderBatchRepository;
import com.sooeez.ecomm.repository.OrderItemRepository;
import com.sooeez.ecomm.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired private OrderRepository orderRepository;
	
	@Autowired private OrderItemRepository orderItemRepository;
	
	@Autowired private OrderBatchRepository orderBatchRepository;
	
	@Autowired private InventoryService inventoryService;
	
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
				if (orderItem.getQtyOrdered() != null
						&& orderItem.getUnitWeight() != null) {
					/* Accumulate total weight */
					weight += orderItem.getQtyOrdered()
							* orderItem.getUnitWeight();
				}
				if (orderItem.getQtyOrdered() != null) {
					/* Accumulate total items ordered */
					qtyTotalItemOrdered += orderItem.getQtyOrdered();
				}
				if (orderItem.getQtyOrdered() != null
						&& orderItem.getUnitPrice() != null) {
					/* Accumulate grand total */
					grandTotal = grandTotal
							.add(orderItem.getUnitPrice().multiply(
									new BigDecimal(orderItem.getQtyOrdered())));
					subtotal = subtotal.add(orderItem.getUnitPrice().multiply(
							new BigDecimal(orderItem.getQtyOrdered())));
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
	public void confirmDifferentWarehouse(List<Order> orders, Long assginWarehouseId) {
		
		List<Long> sameWarehouseIds = new ArrayList<>();
		boolean differentWarehouseError = false;
		// 循环 order
		for (Order order: orders) {
			// 循环 order item
			for (OrderItem item: order.getItems()) {
				if (item.getAssignTunnel() != null) {
					sameWarehouseIds.add(item.getAssignTunnel().getDefaultWarehouse().getId());
					for (Long warehouseId : sameWarehouseIds) {
						if (warehouseId.longValue() == item.getAssignTunnel().getDefaultWarehouse().getId().longValue()) {
							System.out.println(item.getId() + ":" + item.getAssignTunnel().getDefaultWarehouse().getId());
							differentWarehouseError = true;
							break;
						}
					}
				}
				if (!differentWarehouseError) {
					break;
				}
			}
			if (!differentWarehouseError) {
				break;
			}
		}
		
		if (!differentWarehouseError) {
			for (Order order: orders) {
				order.getCheckMap().put("differentWarehouseError", true);
			}
		} else {
			for (Order order: orders) {
				order.getCheckMap().put("differentWarehouseError", false);
			}
		}
	}

	public void confirmOrderWhenGenerateOutInventory(List<Order> orders, Long assginWarehouseId) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("differentWarehouseError", true);
		map.put("differnetWarehouseErrorOrders", null);
		map.put("productInventoryNotEnoughError", false);
		map.put("productInventoryNotEnoughErrorOrders", new ArrayList<Order>());
		map.put("orderExistOutInventorySheetError", false);
		map.put("orderExistOutInventorySheetErrorOrders", new ArrayList<Order>());
		
		if (assginWarehouseId != null) {
			
		} else {
			Inventory inventoryQuery = new Inventory();
			inventoryQuery.setWarehouseId(assginWarehouseId);
			
			List<Inventory> inventories = inventoryService.getInventories(inventoryQuery, null);
			List<Product> products = inventoryService.refreshInventory(inventories);
			
			//循环订单
			for (Order order: orders) {
				//循环item
				for (OrderItem item: order.getItems()) {
					// 循环products,判断当前的item是否就是当前的产品
					for (Product product: products) {
						if (item.getProduct().getSku().equals(product.getSku())) {
							//将当前的item订购的数量和产品在指定仓库下的库存相比较
							if (product.getTotal().longValue() - item.getQtyOrdered().longValue() < 0) {
								//库存不足,将订单放入productInventoryNotEnoughOrders列表
								((List<Order>)map.get("productInventoryNotEnoughOrders")).add(order);
								map.put("productInventoryNotEnough", true);
							}
							break;
						}
					}
				}
				//循环当前订单下的所有出库单，判断在指定仓库下已有出库单
				for (OrderBatch orderBatch: order.getBatches()) {
					if (orderBatch.getWarehouseId().longValue() == assginWarehouseId.longValue()) {
						// 当前订单在指定仓库中已存在一张出库单
						((List<Order>)map.get("existOutInventorySheetOrders")).add(order);
						map.put("productInventoryNotEnough", true);
						break;
					}
				}
			}
		}
		
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
