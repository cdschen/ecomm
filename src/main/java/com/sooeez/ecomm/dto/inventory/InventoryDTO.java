package com.sooeez.ecomm.dto.inventory;

import java.io.Serializable;
import java.util.Date;

public class InventoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long inventoryBatchId;
	private Long quantity;
	private Date expireDate;

	private InventoryProductDTO product;
	private InventoryPositionDTO position;
	private InventoryBatchDTO batch;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInventoryBatchId() {
		return inventoryBatchId;
	}

	public void setInventoryBatchId(Long inventoryBatchId) {
		this.inventoryBatchId = inventoryBatchId;
	}

	public InventoryProductDTO getProduct() {
		return product;
	}

	public void setProduct(InventoryProductDTO product) {
		this.product = product;
	}

	public InventoryPositionDTO getPosition() {
		return position;
	}

	public void setPosition(InventoryPositionDTO position) {
		this.position = position;
	}

	public InventoryBatchDTO getBatch() {
		return batch;
	}

	public void setBatch(InventoryBatchDTO batch) {
		this.batch = batch;
	}

}
