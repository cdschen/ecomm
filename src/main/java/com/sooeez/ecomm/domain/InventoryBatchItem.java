package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
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
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@Column(name = "product_id", insertable = false, updatable = false)
	private Long productId;

	@Column(name = "warehouse_id", insertable = false, updatable = false)
	private Long warehouseId;

	@Column(name = "inventory_batch_id")
	private Long inventoryBatchId;

	@Column(name = "changed_quantity", nullable = false)
	private Long changedQuantity;

	@Column(name = "actual_quantity")
	private Long actualQuantity;

	@Lob
	@Column(name = "inventory_snapshot")
	private String inventorySnapshot;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expire_date")
	private Date expireDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	private Date createTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_time")
	private Date lastTime;

	@Column(name = "batch_type")
	private Integer batchType;

	@Column(name = "batch_operate")
	private Integer batchOperate;

	/*
	 * Related Properties
	 */

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "product_id")
	private Product product = new Product();

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "warehouse_id")
	private Warehouse warehouse = new Warehouse();

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "warehouse_position_id")
	private WarehousePosition position = new WarehousePosition();

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id")
	private User user = new User();

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "execute_operator_id")
	private User executeOperator = new User();

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "out_batch_id")
	private InventoryBatch outBatch = new InventoryBatch();

	@Transient
	private Date createTimeStart;

	@Transient
	private Date createTimeEnd;

	@Transient
	private String productSKU;

	@Transient
	private String productName;

	// 仓库id集合
	@Transient
	private Long[] warehouseIds;

	//

	public InventoryBatchItem() {
	}

	public Long[] getWarehouseIds() {
		return warehouseIds;
	}

	public void setWarehouseIds(Long[] warehouseIds) {
		this.warehouseIds = warehouseIds;
	}

	public String getProductSKU() {
		return productSKU;
	}

	public void setProductSKU(String productSKU) {
		this.productSKU = productSKU;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public Integer getBatchOperate() {
		return batchOperate;
	}

	public void setBatchOperate(Integer batchOperate) {
		this.batchOperate = batchOperate;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public Integer getBatchType() {
		return batchType;
	}

	public void setBatchType(Integer batchType) {
		this.batchType = batchType;
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

	public Long getId() {
		return id;
	}

	public Long getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(Long actualQuantity) {
		this.actualQuantity = actualQuantity;
	}

	public User getExecuteOperator() {
		return executeOperator;
	}

	public void setExecuteOperator(User executeOperator) {
		this.executeOperator = executeOperator;
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
