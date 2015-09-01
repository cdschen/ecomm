package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "t_inventory_batch_item")
public class InventoryBatchItem implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "warehouse_id", nullable = false)
	private Long warehouseId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "inventory_batch_id", insertable = false, updatable = false)
	private Long inventoryBatchId;

	@Column(name = "changed_quantity", nullable = false)
	private Long changedQuantity;

	@Column(name = "inventory_snapshot")
	private Long inventorySnapshot;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expire_date")
	private Date expireDate;

	/*
	 * Related Properties
	 */

	@OneToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@OneToOne
	@JoinColumn(name = "warehouse_position_id")
	private WarehousePosition position;
	
	@Transient
	private InventoryBatch outBatch;

	//

	public InventoryBatchItem() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public WarehousePosition getPosition() {
		return position;
	}

	public void setPosition(WarehousePosition position) {
		this.position = position;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getInventoryBatchId() {
		return inventoryBatchId;
	}

	public void setInventoryBatchId(Long inventoryBatchId) {
		this.inventoryBatchId = inventoryBatchId;
	}

	public Long getChangedQuantity() {
		return changedQuantity;
	}

	public void setChangedQuantity(Long changedQuantity) {
		this.changedQuantity = changedQuantity;
	}

	public Long getInventorySnapshot() {
		return inventorySnapshot;
	}

	public void setInventorySnapshot(Long inventorySnapshot) {
		this.inventorySnapshot = inventorySnapshot;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public InventoryBatch getOutBatch() {
		return outBatch;
	}

	public void setOutBatch(InventoryBatch outBatch) {
		this.outBatch = outBatch;
	}


}
