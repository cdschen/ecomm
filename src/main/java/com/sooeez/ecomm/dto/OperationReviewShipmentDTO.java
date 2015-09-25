package com.sooeez.ecomm.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.sooeez.ecomm.domain.Shipment;

public class OperationReviewShipmentDTO implements Serializable {

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
	/* 发货单验证 */
	private Shipment shipment;
	

	public boolean isConfirmable() {
		return confirmable;
	}

	public void setConfirmable(boolean confirmable) {
		this.confirmable = confirmable;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
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

	public Shipment getShipment() {
		return shipment;
	}

	public void setShipment(Shipment shipment) {
		this.shipment = shipment;
	}

}
