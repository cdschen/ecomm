package com.sooeez.ecomm.dto.inventory;

import java.io.Serializable;

public class InventoryBatchDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long receiveId;
	private Long purchaseOrderId;

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
