package com.sooeez.ecomm.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.Order;
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
	public Page<Order> getPagedOrders(Pageable pageable, Order order) {
		return this.orderService.getPagedOrders(pageable, order);
	}
	
	@RequestMapping(value = "/orders/get/all")
	public List<Order> getOrders(Order order) {
		return this.orderService.getOrders(order);
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
	
}