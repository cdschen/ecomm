package com.sooeez.ecomm.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.domain.Order;
import com.sooeez.ecomm.domain.OrderItem;
import com.sooeez.ecomm.domain.ProductShopTunnel;
import com.sooeez.ecomm.domain.ShopTunnel;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired private OrderRepository orderRepository;
	
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
		long assginWarehouseId = order.getWarehouseId() != null ? order.getWarehouseId().longValue() : 0;
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
		
		Page<Order> page = this.orderRepository.findAll(getOrderSpecification(order), pageable);
		if (page != null && page.getContent() != null && page.getContent().size() > 0 && assginWarehouseId > 0) {
			for (Order _order: page.getContent()) {
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
												//deletedItemIndexs.add(item.getId().intValue());
												//_order.getItems().remove(item);
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
											//deletedItemIndexs.add(item.getId().intValue());
											//_order.getItems().remove(item);
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
										//deletedItemIndexs.add(item.getId().intValue());
										//_order.getItems().remove(item);
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
		return page;
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
	
	public void testNativeSQL() {
	}
}
