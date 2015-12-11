package com.sooeez.ecomm.dto.inventory;

import java.io.Serializable;
import java.util.Date;

import com.sooeez.ecomm.dto.UserDTO;
import com.sooeez.ecomm.dto.WarehouseDTO;

public class InventoryBatchDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long receiveId;
	private Long purchaseOrderId;
	private Date operateTime;
	private Date outInventoryTime;
	private UserDTO user;
	private UserDTO executeOperator;
	private WarehouseDTO warehouse;
	private Integer type;
	private Integer operate;

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public Date getOutInventoryTime() {
		return outInventoryTime;
	}

	public void setOutInventoryTime(Date outInventoryTime) {
		this.outInventoryTime = outInventoryTime;
	}

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

	public UserDTO getExecuteOperator() {
		return executeOperator;
	}

	public void setExecuteOperator(UserDTO executeOperator) {
		this.executeOperator = executeOperator;
	}

	public WarehouseDTO getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(WarehouseDTO warehouse) {
		this.warehouse = warehouse;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getOperate() {
		return operate;
	}

	public void setOperate(Integer operate) {
		this.operate = operate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getReceiveId() {
		return receiveId;
	}

	public void setReceiveId(Long receiveId) {
		this.receiveId = receiveId;
	}

	public Long getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Long purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

}
