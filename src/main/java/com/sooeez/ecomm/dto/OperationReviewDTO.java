package com.sooeez.ecomm.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sooeez.ecomm.domain.Courier;
import com.sooeez.ecomm.domain.Order;
import com.sooeez.ecomm.domain.PurchaseOrder;
import com.sooeez.ecomm.domain.Shipment;

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
	
	private Boolean deleted = false;
	private Long shopId;
	private Long warehouseId;
	

	/* Logic-Related Attributes */
	/* 订单验证 */
	private List<Order> orders;
	/* 选中快递公司验证 */
	private Courier selectedCourier;
	// 指定出库的仓库id
	private Long assignWarehouseId;
	// 指定订单的店铺id
	private Long assignShopId;
	// 指定配送方式
	private Integer assignDeliveryMethod;
	/* 收货单验证 */
	private List<PurchaseOrder> purchaseOrders;
	
	/* 发货单批量添加 */
	private List<Shipment> shipments;
	
	/* 操作复核查询［快递］之用 */
	private String shippingDescription;
	
	/* 返回查询结果之用 */
	private Integer totalNumber;
	
	private Boolean showDeployedOrders;
	

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

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public List<Shipment> getShipments() {
		return shipments;
	}

	public void setShipments(List<Shipment> shipments) {
		this.shipments = shipments;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Long getAssignShopId() {
		return assignShopId;
	}

	public void setAssignShopId(Long assignShopId) {
		this.assignShopId = assignShopId;
	}

	public Integer getAssignDeliveryMethod() {
		return assignDeliveryMethod;
	}

	public void setAssignDeliveryMethod(Integer assignDeliveryMethod) {
		this.assignDeliveryMethod = assignDeliveryMethod;
	}

	public String getShippingDescription() {
		return shippingDescription;
	}

	public void setShippingDescription(String shippingDescription) {
		this.shippingDescription = shippingDescription;
	}

	public Integer getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}

	public Boolean getShowDeployedOrders() {
		return showDeployedOrders;
	}

	public void setShowDeployedOrders(Boolean showDeployedOrders) {
		this.showDeployedOrders = showDeployedOrders;
	}

}
