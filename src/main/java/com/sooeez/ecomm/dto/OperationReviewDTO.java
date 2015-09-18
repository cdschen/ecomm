package com.sooeez.ecomm.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sooeez.ecomm.domain.Courier;
import com.sooeez.ecomm.domain.Order;

public class OperationReviewDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/* Action */
	public static final String VERIFY = "VERIFY";
	public static final String CONFIRM = "CONFIRM";

	/* Basic Attributes */
	/* 忽略规则列表 */
	private List<String> ignoredCheckers = new ArrayList<>();
	/* 检查项 */
	private Map<String, Boolean> checkMap = new HashMap<>();
	/* 额外数据 */
	private Map<String, Object> dataMap = new HashMap<>();
	/* 操作类型，(验证:review / 确认:confirm) */
	private String action;
	/* 复核是否全部通过 */
	private boolean reviewPass = true;

	/* Logic-Related Attributes */
	/* 订单验证 */
	private List<Order> orders;
	/* 选中快递公司验证 */
	private Courier selectedCourier;

	public boolean isReviewPass() {
		return reviewPass;
	}

	public void setReviewPass(boolean reviewPass) {
		this.reviewPass = reviewPass;
	}

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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Courier getSelectedCourier() {
		return selectedCourier;
	}

	public void setSelectedCourier(Courier selectedCourier) {
		this.selectedCourier = selectedCourier;
	}

}
