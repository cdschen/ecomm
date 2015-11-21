package com.sooeez.ecomm.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sooeez.ecomm.domain.Courier;
import com.sooeez.ecomm.domain.Order;
import com.sooeez.ecomm.domain.PurchaseOrder;

public class OperationReviewDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/* Action */
	public static final String VERIFY = "VERIFY";
	public static final String CONFIRM = "CONFIRM";

	/* Basic Attributes */
	/* 忽略规则列表 */
	private Map<String, Boolean> ignoredMap = new HashMap<>();
	/* 检查项 */
	private Map<String, Boolean> checkMap = new HashMap<>();
	/* 检查结果 */
	private Map<String, Object> resultMap = new HashMap<>();
	/* 额外数据 */
	private Map<String, Object> dataMap = new HashMap<>();
	/* 操作类型，(验证:review / 确认:confirm) */
	private String action;
	/* 复核可确认性 */
	private boolean confirmable = true;

	/* Logic-Related Attributes */
	/* 订单验证 */
	private List<Order> orders;
	/* 选中快递公司验证 */
	private Courier selectedCourier;
	// 指定出库的仓库id
	private Long assignWarehouseId;
	/* 收货单验证 */
	private List<PurchaseOrder> purchaseOrders;
	

	public boolean isConfirmable() {
		return confirmable;
	}

	public void setConfirmable(boolean confirmable) {
		this.confirmable = confirmable;
	}

	public Long getAssignWarehouseId() {
		return assignWarehouseId;
	}

	public void setAssignWarehouseId(Long assignWarehouseId) {
		this.assignWarehouseId = assignWarehouseId;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
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

	public Map<String, Boolean> getIgnoredMap() {
		return ignoredMap;
	}

	public void setIgnoredMap(Map<String, Boolean> ignoredMap) {
		this.ignoredMap = ignoredMap;
	}

	public Map<String, Object> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}

	public List<PurchaseOrder> getPurchaseOrders() {
		return purchaseOrders;
	}

	public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
		this.purchaseOrders = purchaseOrders;
	}
	
}
