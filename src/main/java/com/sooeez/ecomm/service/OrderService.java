package com.sooeez.ecomm.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import com.sooeez.ecomm.domain.Process;
import com.sooeez.ecomm.domain.ProcessStep;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.ProductShopTunnel;
import com.sooeez.ecomm.domain.Shipment;
import com.sooeez.ecomm.domain.ShipmentItem;
import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.domain.ShopTunnel;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.dto.api.DTO_Order;
import com.sooeez.ecomm.dto.api.DTO_OrderItem;
import com.sooeez.ecomm.dto.api.DTO_Shipment;
import com.sooeez.ecomm.dto.api.general.DTO_Pagination;
import com.sooeez.ecomm.dto.api.general.DTO_Process_Status;
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
	@Autowired private InventoryBatchService inventoryBatchService;
	@Autowired private ProcessService processService;
	@Autowired private ShopService shopService;
	@PersistenceContext private EntityManager em;

	/*
	 * Order
	 */
	
	@Transactional
	public Order saveOrder(Order order) {
		/* If id not null then is edit action */
		if (order.getId() == null) {
			
			Process processQuery = new Process();
			processQuery.setObjectType(1);
			processQuery.setEnabled(true);
			List<Process> processes = processService.getProcesses(processQuery, null);
			if (processes != null && processes.size() > 0) {
				for (Process process : processes) {
					if (process.getAutoApply() == true) {
						ObjectProcess objectProcess = new ObjectProcess();
						objectProcess.setObjectType(1);
						objectProcess.setProcess(process);
						ProcessStep step = new ProcessStep();
						if (process.getDefaultStepId() != null) {
							step.setId(process.getDefaultStepId());
						} else {
							step.setId(process.getSteps().get(0).getId());
						}
						objectProcess.setStep(step);
						order.setProcesses(new ArrayList<ObjectProcess>());
						order.getProcesses().add(objectProcess);
					}
				}
			}
			
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
				/* *3/23 */
				tax = order.getSubtotal().multiply( new BigDecimal( 3 ) ).divide( new BigDecimal( 23 ), 2, BigDecimal.ROUND_DOWN );
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
		} else if (order.getShopIds() != null && order.getShopIds().length > 0) {
			String shopIds = "";
			for (int i = 0, len = order.getShopIds().length; i < len - 1; i++) {
				shopIds += order.getShopIds()[0] + ",";
			}
			shopIds += order.getShopIds()[order.getShopIds().length - 1];
			sqlString += " and `order`.shop_id in(" + shopIds + ")";
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
					+ "and productShopTunnel.product_id = orderItem.product_id "
					+ "and productShopTunnel.`shop_id` = shop.id) "
					+ "or "
					+ "exists(select 1 from t_shop_tunnel as shopTunnel2 "
					+ "where shopTunnel2.shop_id = shop.id "
					+ "and shopTunnel2.default_option = 1 "
					+ "and shopTunnel2.default_warehouse_id = " + order.getWarehouseId() + ")"
					+ ")))";
			order.setWarehouseId(null);
		} else if (order.getWarehouseIds() != null && order.getWarehouseIds().length > 0) {
			String warehouseIds = "";
			for (int i = 0, len = order.getWarehouseIds().length; i < len - 1; i++) {
				warehouseIds += order.getWarehouseIds()[0] + ",";
			}
			warehouseIds += order.getWarehouseIds()[order.getWarehouseIds().length - 1];
			
			sqlString += " and(orderItem.warehouse_id in(" + warehouseIds + ")"
					+ " or("
					+ "orderItem.warehouse_id is null"
					+ " and(exists(select 1"
					+ " from t_product_shop_tunnel as productShopTunnel, "
					+ "t_shop_tunnel as shopTunnel1 "
					+ "where shopTunnel1.default_warehouse_id in(" + warehouseIds + ")"
					+ " and shopTunnel1.id = productShopTunnel.tunnel_id "
					+ "and productShopTunnel.product_id = orderItem.product_id "
					+ "and productShopTunnel.`shop_id` = shop.id) "
					+ "or "
					+ "exists(select 1 from t_shop_tunnel as shopTunnel2 "
					+ "where shopTunnel2.shop_id = shop.id "
					+ "and shopTunnel2.default_option = 1 "
					+ "and shopTunnel2.default_warehouse_id in(" + warehouseIds + "))"
					+ ")))";
		}
		
		order.setOrderIds(new ArrayList<>());
		System.out.println("查询出来的可用的订单编号:");
		em.createNativeQuery(sqlString).getResultList().forEach(orderId -> {
			System.out.print(orderId + ", ");
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
					// 判断通道是不是仓库通道，并且行为是包含
					if (tunnel.getType().intValue() == 1 && tunnel.getBehavior().intValue() == 1) {
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
									// 如何选择的通道是一个供应商通道
									if (tunnel.getDefaultWarehouse() == null) {
										item.setAssignTunnel(order.getShop().getDefaultTunnel());
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
		List<Product> products = inventoryBatchService.refreshInventory(inventories);
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
			//this.shopService.initShopDefaultTunnel(o.getShop());
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
		} /*else {
			review.getCheckMap().put("productInventoryNotEnoughError", false);
		}*/
		
		
		if (!review.getIgnoredMap().get("orderExistOutInventorySheet")) {
			this.confirmOrderExistOutInventorySheet(review);
		} /*else {
			review.getCheckMap().put("orderExistOutInventorySheetError", false);
		}*/
		
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
			if (order.getShopIds() != null && order.getShopIds().length > 0) {
				predicates.add(root.get("shopId").in(order.getShopIds()));
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
			if (order.getShippingTimeStart() != null && order.getShippingTimeEnd() != null) {
				try {
					Subquery<Shipment> shipmentSubquery = query.subquery(Shipment.class);
					Root<Shipment> shipmentRoot = shipmentSubquery.from(Shipment.class);
					shipmentSubquery.select(shipmentRoot.get("orderId"));
					shipmentSubquery.where(cb.between(shipmentRoot.get("createTime"),
							new SimpleDateFormat("yyyy-MM-dd").parse(order.getShippingTimeStart()),
							new SimpleDateFormat("yyyy-MM-dd").parse(order.getShippingTimeEnd())));
					predicates.add(cb.in(root.get("id")).value(shipmentSubquery));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (order.getShippingTimeStart() != null) {
				try {
					Subquery<Shipment> shipmentSubquery = query.subquery(Shipment.class);
					Root<Shipment> shipmentRoot = shipmentSubquery.from(Shipment.class);
					shipmentSubquery.select(shipmentRoot.get("orderId"));
					shipmentSubquery.where(cb.greaterThanOrEqualTo(shipmentRoot.get("createTime"), 
							new SimpleDateFormat("yyyy-MM-dd").parse(order.getShippingTimeStart())));
					predicates.add(cb.in(root.get("id")).value(shipmentSubquery));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (order.getShippingTimeEnd() != null) {
				try {
					Subquery<Shipment> shipmentSubquery = query.subquery(Shipment.class);
					Root<Shipment> shipmentRoot = shipmentSubquery.from(Shipment.class);
					shipmentSubquery.select(shipmentRoot.get("orderId"));
					shipmentSubquery.where(cb.lessThanOrEqualTo(shipmentRoot.get("createTime"), 
							new SimpleDateFormat("yyyy-MM-dd").parse(order.getShippingTimeEnd())));
					predicates.add(cb.in(root.get("id")).value(shipmentSubquery));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if( order.getShipNumber() != null && ! order.getShipNumber().trim().equals("") )
			{
				Subquery<Shipment> shipmentSubquery = query.subquery(Shipment.class);
				Root<Shipment> shipmentRoot = shipmentSubquery.from(Shipment.class);
				shipmentSubquery.select(shipmentRoot.get("orderId"));
				shipmentSubquery.where(shipmentRoot.get("shipNumber").in(order.getShipNumber()));
				predicates.add(cb.in(root.get("id")).value(shipmentSubquery));
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
	
	
	/*
	 * BEGIN API Order
	 */
	
	public void getAPIRespondOrderDetail(Shop shop, DTO_Order dtoOrder, Order order)
	{
		/* 设置订单基本信息 */
		dtoOrder.setId( order.getId() );
		dtoOrder.setShop_id( order.getShop().getId() );
		dtoOrder.setOrder_sn( order.getExternalSn() );
		dtoOrder.setQty_total_item_ordered( order.getQtyTotalItemOrdered() );
		dtoOrder.setQty_total_item_shipped( order.getQtyTotalItemShipped() );
		dtoOrder.setGrand_total( order.getGrandTotal() );
		dtoOrder.setSubtotal( order.getSubtotal() );
		dtoOrder.setShipping_fee( order.getShippingFee() );
		dtoOrder.setTax( order.getTax() );
		dtoOrder.setTotal_invoiced( order.getTotalInvoiced() );
		dtoOrder.setTotal_paid( order.getTotalPaid() );
		dtoOrder.setTotal_refunded( order.getTotalRefunded() );
		dtoOrder.setWeight( order.getWeight() );
		dtoOrder.setMemo( order.getMemo() );
		
		String deliveryMethod = "未知运送方式";
		if( order.getDeliveryMethod()!=null )
		{
			switch ( order.getDeliveryMethod() )
			{
				case 1:
					deliveryMethod = "快递";
					break;
				case 2:
					deliveryMethod = "自提";
					break;
				case 3:
					deliveryMethod = "送货上门";
					break;
				default:
					break;
			}
		}
		dtoOrder.setDelivery_method( deliveryMethod );
		
		dtoOrder.setSender_name( order.getSenderName() );
		dtoOrder.setSender_address( order.getSenderAddress() );
		dtoOrder.setSender_phone( order.getSenderPhone() );
		dtoOrder.setSender_email( order.getSenderEmail() );
		dtoOrder.setSender_post( order.getSenderPost() );
		dtoOrder.setReceive_name( order.getReceiveName() );
		dtoOrder.setReceive_phone( order.getReceivePhone() );
		dtoOrder.setReceive_email( order.getReceiveEmail() );
		dtoOrder.setReceive_country( order.getReceiveCountry() );
		dtoOrder.setReceive_province( order.getReceiveProvince() );
		dtoOrder.setReceive_city( order.getReceiveCity() );
		dtoOrder.setReceive_address( order.getReceiveAddress() );
		dtoOrder.setReceive_post( order.getReceivePost() );
		dtoOrder.setCreated_time( order.getInternalCreateTime().toString() );
		dtoOrder.setUpdated_time( order.getLastUpdateTime().toString() );
		
		/* 获得客户信息，数据库中还未加入客户表 */
//		String customerSQL = "SELECT id, name, email, phone FROM t_customer " +
//							 "WHERE id = (" +
//							 "    SELECT customer_id FROM t_order " +
//							 "    WHERE id = ?1" +
//							 ")";
//		Query customerQuery = em.createNativeQuery( customerSQL );
//		customerQuery.setParameter( 1, order.getId() );
//		DTO_Customer customer =  (DTO_Customer) customerQuery.getSingleResult();
//		dtoOrder.setCustomer( customer );
		
		/* 获得货币名称 */
		dtoOrder.setCurrency( order.getCurrency().getName() );
		
		
		/* 获得订单详情 */
		List<DTO_OrderItem> dtoOrderItems = new ArrayList<DTO_OrderItem>();
		for(OrderItem orderItem : order.getItems())
		{
			DTO_OrderItem dtoOrderItem = new DTO_OrderItem();
			dtoOrderItem.setId( orderItem.getId() );
			dtoOrderItem.setSku( orderItem.getSku() );
			dtoOrderItem.setShop_product_sku( orderItem.getExternalSku() );
			dtoOrderItem.setName( orderItem.getName() );
			dtoOrderItem.setShop_product_name( orderItem.getExternal_name() );
			dtoOrderItem.setUnit_weight( orderItem.getUnitWeight() );
			dtoOrderItem.setQty_ordered( orderItem.getQtyOrdered() );
			dtoOrderItem.setQty_shipped( orderItem.getQtyShipped() );
			dtoOrderItem.setUnit_price( orderItem.getUnitPrice() );
			
			dtoOrderItems.add( dtoOrderItem );
		}
		dtoOrder.setOrder_items( dtoOrderItems );
		
		
		/* 获得发货单 */
		List<DTO_Shipment> dtoShipments = new ArrayList<DTO_Shipment>();
		if( order.getShipments() != null )
		{
			for (Shipment shipment : order.getShipments())
			{
				DTO_Shipment dtoShipment = new DTO_Shipment();
				dtoShipment.setId( shipment.getId() );
				dtoShipment.setCourier_name( shipment.getCourier()!=null ? shipment.getCourier().getName() : null );
				dtoShipment.setCreator( shipment.getOperator()!=null ? shipment.getOperator().getUsername() : null );
				dtoShipment.setExecutor( shipment.getExecuteOperator()!=null ? shipment.getExecuteOperator().getUsername() : null );
				dtoShipment.setCourier_name( shipment.getCourier()!=null ? shipment.getCourier().getName() : null );
				dtoShipment.setShip_number( shipment.getShipNumber() );
				
				String shipStatus = "未知状态";
				if( shipment.getShipStatus()!=null )
				{
					switch ( shipment.getShipStatus() )
					{
						case 1: shipStatus="待取件"; break;
						case 2: shipStatus="已发出"; break;
						case 3: shipStatus="已签收"; break;
						case 4: shipStatus="派送异常"; break;
						case 5: shipStatus="作废"; break;
			
						default: shipStatus="未知状态"; break;
					}
				}
				dtoShipment.setShip_status( shipStatus );
				
				dtoShipment.setQty_total_item_shipped( shipment.getQtyTotalItemShipped() );
				dtoShipment.setTotal_weight( shipment.getTotalWeight() );
				dtoShipment.setShipfee_cost( shipment.getShipfeeCost() );
				dtoShipment.setCreate_time( shipment.getCreateTime()!=null ? shipment.getCreateTime().toString() : null );
				dtoShipment.setLast_update( shipment.getLastUpdate()!=null ? shipment.getLastUpdate().toString() : null );
				dtoShipment.setPickup_time( shipment.getPickupTime()!=null ? shipment.getPickupTime().toString() : null );
				dtoShipment.setSignup_time( shipment.getSignupTime()!=null ? shipment.getSignupTime().toString() : null );
				dtoShipment.setMemo( shipment.getMemo() );
				dtoShipment.setSender_name( shipment.getSenderName() );
				dtoShipment.setSender_phone( shipment.getSenderPhone() );
				dtoShipment.setSender_email( shipment.getSenderEmail() );
				dtoShipment.setSender_address( shipment.getSenderAddress() );
				dtoShipment.setSender_address( shipment.getSenderAddress() );
				dtoShipment.setSender_post( shipment.getSenderPost() );
				dtoShipment.setReceive_name( shipment.getReceiveName() );
				dtoShipment.setReceive_phone( shipment.getReceivePhone() );
				dtoShipment.setReceive_email( shipment.getReceiveEmail() );
				dtoShipment.setReceive_country( shipment.getReceiveCountry() );
				dtoShipment.setReceive_province( shipment.getReceiveProvince() );
				dtoShipment.setReceive_city( shipment.getReceiveCity() );
				dtoShipment.setReceive_address( shipment.getReceiveAddress() );
				dtoShipment.setReceive_post( shipment.getReceivePost() );
				
				dtoShipments.add( dtoShipment );
			}
			dtoOrder.setShipments( dtoShipments );
		}
		
		
		/* 获得流程状态 */
		List<DTO_Process_Status> dtoProcessStatus = new ArrayList<DTO_Process_Status>();
		if( order.getProcesses() != null )
		{
			for ( ObjectProcess objProcess : order.getProcesses() )
			{
				DTO_Process_Status processingState = new DTO_Process_Status();
				processingState.setName( objProcess.getProcess().getName() );
				processingState.setValue( objProcess.getStep().getName() );
				dtoProcessStatus.add( processingState );
			}
			dtoOrder.setProcessing_status( dtoProcessStatus );
		}
		
	}

	@SuppressWarnings("unchecked")
	public void getAPIRespondOrders(Shop shop, List<DTO_Order> dtoOrders, DTO_Pagination page_context)
	{
		/* 1. 获得订单总数 */
		String sqlCount = "SELECT COUNT(*) FROM t_order " +
						  "WHERE shop_id = ?1 " +
						  "AND deleted = false";
		Query queryCount = em.createNativeQuery(sqlCount);
		queryCount.setParameter(1, shop.getId());
		BigInteger total_number = (BigInteger) queryCount.getSingleResult();
		page_context.setTotal_number( total_number );

		/* 2. 获得订单信息 */
		String sql = "SELECT * FROM t_order " +
					 "WHERE shop_id = ?1 " +
					 "AND deleted = false " +
					 "LIMIT ?2, ?3";
		Query query =  em.createNativeQuery( sql, Order.class );
		query.setParameter(1, shop.getId());
		query.setParameter(2, (page_context.getPage() <=1 ? 0 : page_context.getPage() - 1) * page_context.getPer_page());
		query.setParameter(3, page_context.getPer_page());

		
		/* 3. 是否有续页 */
		if( page_context.getPage() * page_context.getPer_page() >= page_context.getTotal_number().longValue() )
		{
			page_context.setHas_more_page( false );
		}
		else
		{
			page_context.setHas_more_page( true );
		}
		
		if( !query.getResultList().isEmpty() )
		{
			List<Order> resultList = query.getResultList();
			
			for (Order order : resultList)
			{
				DTO_Order dtoOrder = new DTO_Order();
				
				getAPIRespondOrderDetail(shop, dtoOrder, order);
				
				dtoOrders.add( dtoOrder );
			}
		}
	}

	public void getAPIRespondOrder(Shop shop, Long orderId, String orderSn, DTO_Order dtoOrder)
	{
		/* 2. 获得订单信息 */
		String sql = "SELECT * FROM t_order " +
					 "WHERE shop_id = ?1 " +
					 "AND (id = ?2 OR external_sn = ?3) " +
					 "AND deleted = false " +
					 "LIMIT 1";
		Query query =  em.createNativeQuery( sql, Order.class );
		query.setParameter(1, shop.getId());
		query.setParameter(2, orderId);
		query.setParameter(3, orderSn);

		if( !query.getResultList().isEmpty() )
		{
			Order order = (Order) query.getSingleResult();
			
			getAPIRespondOrderDetail(shop, dtoOrder, order);
		}
	}
	
	public void createAPIRespondOrder(Shop shop, DTO_Order dtoOrder, Map<String, Object> map)
	{
		/* 是否自营店 */
		Boolean isSelfRunShop = shop.getType() == 0 ? true : false;
		
		Boolean isSkuMatched = true;
		
		/* 订购总数量 */
		int qtyOrdered = 0;
		/* 订购总重量 */
		int weight = 0;
		/* 商品总金额 */
		BigDecimal subtotal = new BigDecimal( 0 );
		
		List<OrderItem> orderItems = new ArrayList<OrderItem>();

		/* 如果有传入订单详情 */
		if( dtoOrder.getOrder_items() != null && dtoOrder.getOrder_items().size() > 0 )
		{
			for( DTO_OrderItem dtoOrderItem : dtoOrder.getOrder_items() )
			{
				OrderItem orderItem = new OrderItem();
				
				orderItem.setSku( dtoOrderItem.getSku() );
				orderItem.setExternalSku( dtoOrderItem.getShop_product_sku() );
				orderItem.setExternal_name( dtoOrderItem.getShop_product_name() );
				orderItem.setQtyOrdered( dtoOrderItem.getQty_ordered() );
				
				orderItems.add( orderItem );
				
				/* 2. 获得产品信息 */
				String sql = "SELECT * FROM t_product " +
						 "WHERE sku = ?2 " +
						 "AND id IN (" +
							 "SELECT product_id FROM t_inventory " +
							 "WHERE warehouse_id IN (" +
								 "SELECT warehouse_id FROM t_tunnel_warehouse " +
								 "WHERE tunnel_id IN (" +
									 "SELECT id FROM t_shop_tunnel " +
									 "WHERE shop_id = ?1 " +
								 ") " +
							 ") " +
						 ") " +
						 "AND deleted = false " +
						 "LIMIT 1";
				Query query =  em.createNativeQuery( sql , Product.class);
				query.setParameter(1, shop.getId());
				query.setParameter(2, dtoOrderItem.getSku());

				if( ! query.getResultList().isEmpty() )
				{
					Product product = (Product) query.getSingleResult();
					
					orderItem.setName( product.getName() );
					orderItem.setProduct( product );
					orderItem.setUnitWeight( product.getWeight() );
					
					// 如果 Item 没有指定价格，根据 sku 查对应的 product，没有找到对应的 product 则返回错误信息并停止处理，如果在产品表里找到多个价位？
					if( dtoOrderItem.getUnit_price() == null || dtoOrderItem.getUnit_price().equals( 0 ) )
					{
						/* 是否自营 */
						if( isSelfRunShop )
						{
							/***
							 * 
							 * 		暂时这样 Hardcode，以后要改
							 * 
							 */
							dtoOrderItem.setUnit_price( product.getPriceL1() );
						}
						else
						{
							/* 给订单详情指定合作店铺所对应的产品价位 */
							switch ( shop.getPriceLevel() )
							{
								case 1: dtoOrderItem.setUnit_price( product.getPriceL1() );  break;
								case 2: dtoOrderItem.setUnit_price( product.getPriceL2() );  break;
								case 3: dtoOrderItem.setUnit_price( product.getPriceL3() );  break;
								case 4: dtoOrderItem.setUnit_price( product.getPriceL4() );  break;
								case 5: dtoOrderItem.setUnit_price( product.getPriceL5() );  break;
								case 6: dtoOrderItem.setUnit_price( product.getPriceL6() );  break;
								case 7: dtoOrderItem.setUnit_price( product.getPriceL7() );  break;
								case 8: dtoOrderItem.setUnit_price( product.getPriceL8() );  break;
								case 9: dtoOrderItem.setUnit_price( product.getPriceL9() );  break;
								case 10: dtoOrderItem.setUnit_price( product.getPriceL10() );  break;
							}
						}
					}
					
					orderItem.setUnitPrice( dtoOrderItem.getUnit_price() );
					/***
					 * 
					 * 		每个国家的销售税不是一样的，15% 属于Hardcode
					 * 
					 */
					orderItem.setUnitGst( dtoOrderItem.getUnit_price().subtract( dtoOrderItem.getUnit_price().multiply( new BigDecimal( 0.15 ) ) ) );
				}
				else
				{
					isSkuMatched = false;
					break;
				}
				
				/* 商品总金额（含税) */
				subtotal = subtotal.add( dtoOrderItem.getUnit_price() );
				qtyOrdered++;
				weight += dtoOrderItem.getUnit_weight() != null ? dtoOrderItem.getUnit_weight() : 0;
			}
		}
		
		if( isSkuMatched )
		{
			dtoOrder.setWeight( weight );
			/* 订单总金额 = 商品金额(subtotal) + 运费(shipping_fee) */
			dtoOrder.setGrand_total( subtotal );
			/***
			 * 
			 * 		每个国家的销售税不是一样的，15% 属于Hardcode
			 * 
			 */
			/* 商品包含的税金 */
			dtoOrder.setTax( subtotal.subtract( subtotal.multiply( new BigDecimal( 0.15 ) ) ) );
			/* 商品总件数 */
			dtoOrder.setQty_total_item_ordered( qtyOrdered );
			
			Order order = new Order();
			order.setItems( orderItems );
			order.setShop( shop );
			order.setExternalSn( dtoOrder.getOrder_sn() );
			order.setInternalCreateTime( new Date() );
			order.setLastUpdateTime( new Date() );
			order.setQtyTotalItemOrdered( dtoOrder.getQty_total_item_ordered() );
			order.setQtyTotalItemShipped( 0 );
			order.setMemo( dtoOrder.getMemo() );
			order.setGrandTotal( dtoOrder.getGrand_total() );
			order.setSubtotal( dtoOrder.getSubtotal() );
			order.setTax( dtoOrder.getTax() );
			order.setTotalInvoiced( dtoOrder.getTotal_invoiced() );
			order.setCurrency( shop.getCurrency() );
			order.setWeight( dtoOrder.getWeight() );
			order.setSenderName( dtoOrder.getSender_name() );
			order.setSenderAddress( dtoOrder.getSender_address() );
			order.setSenderPhone( dtoOrder.getSender_phone() );
			order.setSenderEmail( dtoOrder.getSender_email() );
			order.setSenderPost( dtoOrder.getSender_post() );
			order.setReceiveName( dtoOrder.getReceive_name() );
			order.setReceivePhone( dtoOrder.getReceive_phone() );
			order.setReceiveEmail( dtoOrder.getReceive_email() );
			order.setReceiveCountry( dtoOrder.getReceive_country() );
			order.setReceiveProvince( dtoOrder.getReceive_province() );
			order.setReceiveCity( dtoOrder.getReceive_city() );
			order.setReceiveAddress( dtoOrder.getReceive_address() );
			order.setReceivePost( dtoOrder.getReceive_post() );
			
			switch ( dtoOrder.getDelivery_method() )
			{
				case "快递": order.setDeliveryMethod( 1 ); break;
				case "自提": order.setDeliveryMethod( 2 ); break;
				case "送货上门": order.setDeliveryMethod( 3 ); break;
			}
			
			com.sooeez.ecomm.domain.Process process = new com.sooeez.ecomm.domain.Process();
			ProcessStep processStep = new ProcessStep();
			
			process.setId( shop.getInitStep().getProcessId() );
			processStep.setId( shop.getInitStep().getId() );

			ObjectProcess objectProcess = new ObjectProcess();
			objectProcess.setProcess( process );
			objectProcess.setStep( processStep );
			objectProcess.setObjectType( 1 );
			
			List<ObjectProcess> processes = new ArrayList<ObjectProcess>();
			processes.add( objectProcess );
			
			order.setProcesses( processes );
			
			this.saveOrder( order );
			
			this.getAPIRespondOrderDetail(shop, dtoOrder, order);
			
			/* 获得订单初始流程状态 */
			String processName = "";
			String processStepName = "";
			process = this.processService.getProcess( order.getProcesses().get(0).getProcess().getId() );
			processName = process.getName();
			for( ProcessStep ps : process.getSteps() )
			{
				if( ps.getId().equals( order.getProcesses().get(0).getStep().getId() ) )
				{
					processStepName = ps.getName();
				}
			}
			
			/* 将获得的订单初始流程状态赋值给 DTO */
			List<DTO_Process_Status> dtoProcessStatus = new ArrayList<DTO_Process_Status>();
			DTO_Process_Status dtoProcessState = new DTO_Process_Status();
			dtoProcessState.setName( processName );
			dtoProcessState.setValue( processStepName );
			dtoProcessStatus.add( dtoProcessState );
			
			dtoOrder.setProcessing_status( dtoProcessStatus );
			
			map.put("order", dtoOrder);
			
			map.put("message", "New order has been created.");
		}
		else
		{
			map.put("message", "Item sku not find in product.");
		}
	}
	
	public void updateAPIRespondOrder(Shop shop, DTO_Order dtoOrder, Long orderId, String orderSn, Map<String, Object> map)
	{
		/* 2. 获得订单信息 */
		String orderSQL = "SELECT * FROM t_order " +
					 "WHERE shop_id = ?1 " +
					 "AND (id = ?2 OR external_sn = ?3) " +
					 "AND deleted = false " +
					 "LIMIT 1";
		Query orderQuery =  em.createNativeQuery( orderSQL, Order.class );
		orderQuery.setParameter(1, shop.getId());
		orderQuery.setParameter(2, orderId);
		orderQuery.setParameter(3, orderSn);

		/* 如果通过 id 或 sn 找到对应订单 */
		if( ! orderQuery.getResultList().isEmpty() )
		{
			/* 获取订单详细信息 */
			Order order = (Order) orderQuery.getSingleResult();
			
			/***
			 * 
			 * 		可能需要做改动，因为再第一位的有可能不是流程状态？
			 * 
			 */
			/* 如果订单流程状态处在初始化阶段，也就是未处理阶段，那么就可以进行删改操作 */
			if( order.getProcesses().get(0).getStep().getId().equals( shop.getInitStep().getId() ) )
			{
				/* 是否自营店 */
				Boolean isSelfRunShop = shop.getType() == 0 ? true : false;
				
				Boolean isSkuMatched = true;
				
				/* 订购总数量 */
				int qtyOrdered = 0;
				/* 订购总重量 */
				int weight = 0;
				/* 商品总金额 */
				BigDecimal subtotal = new BigDecimal( 0 );
				
				/* 如果指定了订购数量为 0 则意思是删除现有订单详情，就不需要获取传入的订单详情的信息 */
				if( dtoOrder.getQty_total_item_ordered() == null ||
				(dtoOrder.getQty_total_item_ordered() != null && dtoOrder.getQty_total_item_ordered() != 0) )
				{
					/* 如果有传入订单详情 */
					if( dtoOrder.getOrder_items() != null && dtoOrder.getOrder_items().size() > 0 )
					{
						/* 如果订单本身没有详情 */
						if( order.getItems() == null )
						{
							order.setItems( new ArrayList<OrderItem>() );
						}
						/* 如果订单本身有详情，则与传入的订单详情做匹配，如果有 sku 匹配，则将传入的详情的数据更新至数据库获取的订单详情，并将对应的传入订单详情从 list 中移除 */
						else if( order.getItems().size() > 0 )
						{
							for( OrderItem orderItem : order.getItems() )
							{
								Iterator<DTO_OrderItem> dtoOrderItem = dtoOrder.getOrder_items().iterator();
								while ( dtoOrderItem.hasNext() )
								{
									DTO_OrderItem dtoOrderItemIter = dtoOrderItem.next();
									if( dtoOrderItemIter.getSku().equals( orderItem.getSku() ) )
									{
										orderItem.setExternalSku( dtoOrderItemIter.getShop_product_sku() );
										orderItem.setExternal_name( dtoOrderItemIter.getShop_product_name() );
										orderItem.setQtyOrdered( dtoOrderItemIter.getQty_ordered() );
										
										dtoOrderItem.remove();
									}
								}
							}
						}
						
						for( DTO_OrderItem dtoOrderItem : dtoOrder.getOrder_items() )
						{
							OrderItem orderItem = new OrderItem();
							
							orderItem.setSku( dtoOrderItem.getSku() );
							orderItem.setExternalSku( dtoOrderItem.getShop_product_sku() );
							orderItem.setExternal_name( dtoOrderItem.getShop_product_name() );
							orderItem.setQtyOrdered( dtoOrderItem.getQty_ordered() );
							
							order.getItems().add( orderItem );
							
							/* 2. 获得订单信息 */
							String sql = "SELECT * FROM t_product " +
									 "WHERE sku = ?2 " +
									 "AND id IN (" +
										 "SELECT product_id FROM t_inventory " +
										 "WHERE warehouse_id IN (" +
											 "SELECT warehouse_id FROM t_tunnel_warehouse " +
											 "WHERE tunnel_id IN (" +
												 "SELECT id FROM t_shop_tunnel " +
												 "WHERE shop_id = ?1 " +
											 ") " +
										 ") " +
									 ") " +
									 "AND deleted = false " +
									 "LIMIT 1";
							Query query =  em.createNativeQuery( sql , Product.class);
							query.setParameter(1, shop.getId());
							query.setParameter(2, dtoOrderItem.getSku());
				
							if( ! query.getResultList().isEmpty() )
							{
								Product product = (Product) query.getSingleResult();
								
								orderItem.setName( product.getName() );
								orderItem.setProduct( product );
								orderItem.setUnitWeight( product.getWeight() );
								
								// 如果 Item 没有指定价格，根据 sku 查对应的 product，没有找到对应的 product 则返回错误信息并停止处理，如果在产品表里找到多个价位？
								if( dtoOrderItem.getUnit_price() == null || dtoOrderItem.getUnit_price().equals( 0 ) )
								{
									/* 是否自营 */
									if( isSelfRunShop )
									{
										/***
										 * 
										 * 		暂时这样 Hardcode，以后要改
										 * 
										 */
										dtoOrderItem.setUnit_price( product.getPriceL1() );
									}
									else
									{
										/* 给订单详情指定合作店铺所对应的产品价位 */
										switch ( shop.getPriceLevel() )
										{
											case 1: dtoOrderItem.setUnit_price( product.getPriceL1() );  break;
											case 2: dtoOrderItem.setUnit_price( product.getPriceL2() );  break;
											case 3: dtoOrderItem.setUnit_price( product.getPriceL3() );  break;
											case 4: dtoOrderItem.setUnit_price( product.getPriceL4() );  break;
											case 5: dtoOrderItem.setUnit_price( product.getPriceL5() );  break;
											case 6: dtoOrderItem.setUnit_price( product.getPriceL6() );  break;
											case 7: dtoOrderItem.setUnit_price( product.getPriceL7() );  break;
											case 8: dtoOrderItem.setUnit_price( product.getPriceL8() );  break;
											case 9: dtoOrderItem.setUnit_price( product.getPriceL9() );  break;
											case 10: dtoOrderItem.setUnit_price( product.getPriceL10() );  break;
										}
									}
								}
								
								orderItem.setUnitPrice( dtoOrderItem.getUnit_price() );
								/***
								 * 
								 * 		每个国家的销售税不是一样的，15% 属于Hardcode
								 * 
								 */
								orderItem.setUnitGst( dtoOrderItem.getUnit_price().subtract( dtoOrderItem.getUnit_price().multiply( new BigDecimal( 0.15 ) ) ) );
							}
							else
							{
								isSkuMatched = false;
								break;
							}
							
							/* 商品总金额（含税) */
							subtotal = subtotal.add( dtoOrderItem.getUnit_price() );
							qtyOrdered++;
							weight += dtoOrderItem.getUnit_weight() != null ? dtoOrderItem.getUnit_weight() : 0;
						}
					}
					
					if( isSkuMatched )
					{
						dtoOrder.setWeight( weight );
						/* 订单总金额 = 商品金额(subtotal) + 运费(shipping_fee) */
						dtoOrder.setGrand_total( subtotal );
						/***
						 * 
						 * 		每个国家的销售税不是一样的，15% 属于Hardcode
						 * 
						 */
						/* 商品包含的税金 */
						dtoOrder.setTax( subtotal.subtract( subtotal.multiply( new BigDecimal( 0.15 ) ) ) );
						/* 商品总件数 */
						dtoOrder.setQty_total_item_ordered( qtyOrdered );
					}
					else
					{
						map.put("message", "Item sku not find in product.");
						
						return;
					}
				}
				else
				{
					order.getItems().clear();
				}
				
				order.setShop( shop );
				order.setCurrency( shop.getCurrency() );
				order.setLastUpdateTime( new Date() );
				order.setQtyTotalItemOrdered( dtoOrder.getQty_total_item_ordered() );
				
				/* 将所有从API传入的非空值更新到数据库取出的订单中 */
				if( dtoOrder.getOrder_sn() != null && ! dtoOrder.getOrder_sn().trim().equals("") )
				{
					order.setExternalSn( dtoOrder.getOrder_sn() );
				}
				if( dtoOrder.getMemo() != null && ! dtoOrder.getMemo().trim().equals("") )
				{
					order.setMemo( dtoOrder.getMemo() );
				}
				if( dtoOrder.getGrand_total() != null && ( dtoOrder.getGrand_total().compareTo(BigDecimal.ZERO) > 0 ) )
				{
					order.setGrandTotal( dtoOrder.getGrand_total() );
				}
				if( dtoOrder.getSubtotal() != null && ( dtoOrder.getSubtotal().compareTo(BigDecimal.ZERO) > 0 ) )
				{
					order.setSubtotal( dtoOrder.getSubtotal() );
				}
				if( dtoOrder.getTax() != null && ( dtoOrder.getTax().compareTo(BigDecimal.ZERO) > 0 ) )
				{
					order.setTax( dtoOrder.getTax() );
				}
				if( dtoOrder.getTotal_invoiced() != null && ( dtoOrder.getTotal_invoiced().compareTo(BigDecimal.ZERO) > 0 ) )
				{
					order.setTotalInvoiced( dtoOrder.getTotal_invoiced() );
				}
				if( dtoOrder.getWeight() != null && dtoOrder.getWeight() > 0 )
				{
					order.setWeight( dtoOrder.getWeight() );
				}
				if( dtoOrder.getSender_name() != null && ! dtoOrder.getSender_name().trim().equals("") )
				{
					order.setSenderName( dtoOrder.getSender_name() );
				}
				if( dtoOrder.getSender_address() != null && ! dtoOrder.getSender_address().trim().equals("") )
				{
					order.setSenderAddress( dtoOrder.getSender_address() );
				}
				if( dtoOrder.getSender_phone() != null && ! dtoOrder.getSender_phone().trim().equals("") )
				{
					order.setSenderPhone( dtoOrder.getSender_phone() );
				}
				if( dtoOrder.getSender_email() != null && ! dtoOrder.getSender_email().trim().equals("") )
				{
					order.setSenderEmail( dtoOrder.getSender_email() );
				}
				if( dtoOrder.getSender_post() != null && ! dtoOrder.getSender_post().trim().equals("") )
				{
					order.setSenderPost( dtoOrder.getSender_post() );
				}
				if( dtoOrder.getReceive_name() != null && ! dtoOrder.getReceive_name().trim().equals("") )
				{
					order.setReceiveName( dtoOrder.getReceive_name() );
				}
				if( dtoOrder.getReceive_phone() != null && ! dtoOrder.getReceive_phone().trim().equals("") )
				{
					order.setReceivePhone( dtoOrder.getReceive_phone() );
				}
				if( dtoOrder.getReceive_email() != null && ! dtoOrder.getReceive_email().trim().equals("") )
				{
					order.setReceiveEmail( dtoOrder.getReceive_email() );
				}
				if( dtoOrder.getReceive_country() != null && ! dtoOrder.getReceive_country().trim().equals("") )
				{
					order.setReceiveCountry( dtoOrder.getReceive_country() );
				}
				if( dtoOrder.getReceive_province() != null && ! dtoOrder.getReceive_province().trim().equals("") )
				{
					order.setReceiveProvince( dtoOrder.getReceive_province() );
				}
				if( dtoOrder.getReceive_city() != null && ! dtoOrder.getReceive_city().trim().equals("") )
				{
					order.setReceiveCity( dtoOrder.getReceive_city() );
				}
				if( dtoOrder.getReceive_address() != null && ! dtoOrder.getReceive_address().trim().equals("") )
				{
					order.setReceiveAddress( dtoOrder.getReceive_address() );
				}
				if( dtoOrder.getReceive_post() != null && ! dtoOrder.getReceive_post().trim().equals("") )
				{
					order.setReceivePost( dtoOrder.getReceive_post() );
				}
				if( dtoOrder.getDelivery_method() != null && ! dtoOrder.getDelivery_method().trim().equals("") )
				{
					switch ( dtoOrder.getDelivery_method() )
					{
						case "快递": order.setDeliveryMethod( 1 ); break;
						case "自提": order.setDeliveryMethod( 2 ); break;
						case "送货上门": order.setDeliveryMethod( 3 ); break;
					}
				}
				
				this.saveOrder( order );
				
				this.getAPIRespondOrderDetail(shop, dtoOrder, order);
				
				/* 获得订单初始流程状态 */
				String processName = "";
				String processStepName = "";
				com.sooeez.ecomm.domain.Process process = this.processService.getProcess( order.getProcesses().get(0).getProcess().getId() );
				processName = process.getName();
				for( ProcessStep ps : process.getSteps() )
				{
					if( ps.getId().equals( order.getProcesses().get(0).getStep().getId() ) )
					{
						processStepName = ps.getName();
					}
				}
				
				/* 将获得的订单初始流程状态赋值给 DTO */
				List<DTO_Process_Status> dtoProcessStatus = new ArrayList<DTO_Process_Status>();
				DTO_Process_Status dtoProcessState = new DTO_Process_Status();
				dtoProcessState.setName( processName );
				dtoProcessState.setValue( processStepName );
				dtoProcessStatus.add( dtoProcessState );
				
				dtoOrder.setProcessing_status( dtoProcessStatus );
				
				map.put("order", dtoOrder);
				
				map.put("message", "The order has been updated.");
			}
			/* 否则订单流程状态不处在初始化阶段，也就是处在已处理阶段，那么就不可以进行删改操作 */
			else
			{
				map.put("message", "The order has been processed, please contact your client manager for help.");
			}
		}
		else
		{
			map.put("message", "Could not find order by specified id or sn, or it has been deleted.");
		}
	}
	
	public void deleteAPIRespondOrder(Shop shop, Long orderId, String orderSn, Map<String, Object> map)
	{
		/* 2. 获得订单信息 */
		String sql = "SELECT * FROM t_order " +
					 "WHERE shop_id = ?1 " +
					 "AND (id = ?2 OR external_sn = ?3) " +
					 "AND deleted = false " +
					 "LIMIT 1";
		Query query =  em.createNativeQuery( sql, Order.class );
		query.setParameter(1, shop.getId());
		query.setParameter(2, orderId);
		query.setParameter(3, orderSn);

		if( ! query.getResultList().isEmpty() )
		{
			Order order = (Order) query.getSingleResult();
			
			/***
			 * 
			 * 		可能需要做改动，因为再第一位的有可能不是流程状态？
			 * 
			 */
			/* 如果订单流程状态处在初始化阶段，也就是未处理阶段，那么就可以进行删改操作 */
			if( order.getProcesses().get(0).getStep().getId().equals( shop.getInitStep().getId() ) )
			{
				/* 将订单是否删除更新成 true */
				order.setDeleted( true );
				this.saveOrder( order );
				
				map.put("code", "0");
				map.put("message", "The order has been deleted.");
			}
			/* 否则订单流程状态不处在初始化阶段，也就是处在已处理阶段，那么就不可以进行删改操作 */
			else
			{
				map.put("message", "The order has been processed, please contact your client manager for help.");
			}
		}
		else
		{
			map.put("message", "Could not find order by specified id or sn, or it has been deleted.");
		}
	}
	
	/*
	 * END API Order
	 */
}
