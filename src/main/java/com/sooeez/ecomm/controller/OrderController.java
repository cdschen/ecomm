package com.sooeez.ecomm.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.Order;
import com.sooeez.ecomm.domain.OrderItem;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.service.OrderService;

@RestController
@RequestMapping("/api")
public class OrderController {
	
	@Autowired private OrderService orderService;
	
	/*
	 * Order
	 */
	
	@RequestMapping(value = "/orders/{id}")
	public Order getOrder(@PathVariable("id") Long id) {
		return this.orderService.getOrder(id);
	}
	
	@RequestMapping(value = "/orders")
	public Page<Order> getPagedOrders(Order order, Pageable pageable) {
		return this.orderService.getPagedOrders(order, pageable);
	}
	
	@RequestMapping(value = "/orders/for/orderdeploy")
	public Page<Order> getPageOrdersForOrderDeploy(Order order, Pageable pageable) {
		return this.orderService.getPagedOrdersForOrderDeploy(order, pageable);
	}
	
	@RequestMapping(value = "/orders/confirm/shipment")
	public OperationReviewDTO confirmOrderWhenGenerateShipment(@RequestBody OperationReviewDTO review) {
		return this.orderService.confirmOrderWhenGenerateShipment(review);
	}
	
	@RequestMapping(value = "/orders/confirm/outinventory")
	public OperationReviewDTO confirmOrderWhenGenerateOutInventory(@RequestBody OperationReviewDTO review) {
		return this.orderService.confirmOrderWhenGenerateOutInventory(review);
	}
	
	@RequestMapping(value = "/orders/get/all")
	public List<Order> getOrders(Order order, Sort sort) {
		return this.orderService.getOrders(order, sort);
	}
	
	@RequestMapping(value = "/orders", method = RequestMethod.POST)
	public Order saveOrder(@RequestBody Order order, @RequestParam String action, HttpServletRequest request) {
		return this.orderService.saveOrder(order);
	}
	
	@RequestMapping(value = "/orders/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteOrder(@PathVariable("id") Long id) {
		this.orderService.deleteOrder(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * OrderItem
	 */
	
	@RequestMapping(value = "/orderitems/{id}")
	public OrderItem getOrderItem(@PathVariable("id") Long id) {
		return this.orderService.getOrderItem(id);
	}
	
	@RequestMapping(value = "/orderitems")
	public Page<OrderItem> getPagedOrderItems(Pageable pageable) {
		return this.orderService.getPagedOrderItems(pageable);
	}
	
	@RequestMapping(value = "/orderitems/get/all")
	public List<OrderItem> getOrderItems(Sort sort) {
		return this.orderService.getOrderItems(sort);
	}
	
	@RequestMapping(value = "/orderitems", method = RequestMethod.POST)
	public OrderItem saveOrderItem(@RequestBody OrderItem orderItem) {
		return this.orderService.saveOrderItem(orderItem);
	}
	
	@RequestMapping(value = "/orderitems/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteOrderItem(@PathVariable("id") Long id) {
		this.orderService.deleteOrderItem(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
