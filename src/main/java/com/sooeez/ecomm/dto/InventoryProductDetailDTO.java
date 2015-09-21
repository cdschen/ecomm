package com.sooeez.ecomm.dto;

import java.io.Serializable;
import java.util.Date;

import com.sooeez.ecomm.domain.WarehousePosition;

public class InventoryProductDetailDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private WarehousePosition position;
	private Long quantity;
	private Date expireDate;
	private Long batchId;

	public WarehousePosition getPosition() {
		return position;
	}

	public void setPosition(WarehousePosition position) {
		this.position = position;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

}
