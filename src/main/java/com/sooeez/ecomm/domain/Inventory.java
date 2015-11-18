package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
@Table(name = "t_inventory")
public class Inventory implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "product_id", nullable = false, insertable = false, updatable = false)
	private Long productId;

	@Column(name = "warehouse_id", nullable = false)
	private Long warehouseId;

	@Column(name = "warehouse_position_id", nullable = false, insertable = false, updatable = false)
	private Long warehousePositionId;

	// 入库, 批次ID
	@Column(name = "inventory_batch_id", nullable = false,insertable = false, updatable = false)
	private Long inventoryBatchId;

	@Column(name = "quantity", nullable = false)
	private Long quantity;

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
	
	@OneToOne
	@JoinColumn(name = "inventory_batch_id")
	private InventoryBatch batch;

	/*
	 * @Transient Properties
	 */

	@Transient
	private List<Long> warehouseIds;

	@Transient
	private List<Long> productIds;

	/*
	 * Constructor
	 */

	public Inventory() {
	}

	/*
	 * Functions
	 */

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Long getWarehousePositionId() {
		return warehousePositionId;
	}

	public void setWarehousePositionId(Long warehousePositionId) {
		this.warehousePositionId = warehousePositionId;
	}

	public Long getInventoryBatchId() {
		return inventoryBatchId;
	}

	public void setInventoryBatchId(Long inventoryBatchId) {
		this.inventoryBatchId = inventoryBatchId;
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

	public List<Long> getWarehouseIds() {
		return warehouseIds;
	}

	public void setWarehouseIds(List<Long> warehouseIds) {
		this.warehouseIds = warehouseIds;
	}

	public List<Long> getProductIds() {
		return productIds;
	}

	public void setProductIds(List<Long> productIds) {
		this.productIds = productIds;
	}

	public InventoryBatch getBatch() {
		return batch;
	}

	public void setBatch(InventoryBatch batch) {
		this.batch = batch;
	}

}
