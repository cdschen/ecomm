package com.sooeez.ecomm.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sooeez.ecomm.domain.Order;

public class OperationReviewDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	// 忽略规则列表
	private List<String> ignoredCheckers = new ArrayList<>();
	private List<Order> orders;
	private Map<String, Boolean> checkMap = new HashMap<>();
	private Map<String, Object> dataMap = new HashMap<>();

	public List<String> getIgnoredCheckers() {
		return ignoredCheckers;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
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
