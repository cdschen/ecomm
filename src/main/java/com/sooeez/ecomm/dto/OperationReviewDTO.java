package com.sooeez.ecomm.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.sooeez.ecomm.domain.Order;

public class OperationReviewDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	// 忽略规则列表
	private List<String> ignoredCheckers;
	private List<Order> orders;
	private Map<String, Boolean> checkMap;

	public List<String> getIgnoredCheckers() {
		return ignoredCheckers;
	}

	public void setIgnoredCheckers(List<String> ignoredCheckers) {
		this.ignoredCheckers = ignoredCheckers;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public Map<String, Boolean> getCheckMap() {
		return checkMap;
	}

	public void setCheckMap(Map<String, Boolean> checkMap) {
		this.checkMap = checkMap;
	}

}
