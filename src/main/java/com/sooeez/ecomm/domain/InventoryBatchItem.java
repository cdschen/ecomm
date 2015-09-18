package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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

	@Column(name = "inventory_batch_id")
	private Long inventoryBatchId;

	@Column(name = "changed_quantity", nullable = false)
	private Long changedQuantity;

	@Lob
	@Column(name = "inventory_snapshot")
	private String inventorySnapshot;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expire_date")
	private Date expireDate;

	/*
	 * Related Properties
	 */

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "product_id")
	private Product product;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "warehouse_id")
	private Warehouse warehouse;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "warehouse_position_id")
	private WarehousePosition position;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "out_batch_id")
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

	public String getInventorySnapshot() {
		return inventorySnapshot;
	}

	public void setInventorySnapshot(String inventorySnapshot) {
		this.inventorySnapshot = inventorySnapshot;
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

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public WarehousePosition getPosition() {
		return position;
	}

	public void setPosition(WarehousePosition position) {
		this.position = position;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public InventoryBatch getOutBatch() {
		return outBatch;
	}

	public void setOutBatch(InventoryBatch outBatch) {
		this.outBatch = outBatch;
	}

}
