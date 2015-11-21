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
import javax.persistence.Transient;

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

	// 外键，产品id
	@Column(name = "product_id", nullable = false, insertable = false, updatable = false)
	private Long productId;

	// 外键，仓库id
	@Column(name = "warehouse_id", nullable = false, insertable = false, updatable = false)
	private Long warehouseId;

	// 外键，库位id
	@Column(name = "warehouse_position_id", nullable = false, insertable = false, updatable = false)
	private Long warehousePositionId;

	// 外键，谁创建了这个
	@Column(name = "user_id", nullable = false, insertable = false, updatable = false)
	private Long userId;

	// 当出库的时候，才用到的
	@Column(name = "execute_operator_id", insertable = false, updatable = false)
	private Long executeOperatorId;

	@Column(name = "inventory_batch_id")
	private Long inventoryBatchId;

	@Column(name = "out_batch_id", insertable = false, updatable = false)
	private Long outBatchId;

	@Column(name = "changed_quantity")
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

	@Lob
	@Column(name = "memo")
	private String memo;

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

	/*
	 * @Transient Properties
	 */

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

	/*
	 * Constructor
	 */

	public InventoryBatchItem() {
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getExecuteOperatorId() {
		return executeOperatorId;
	}

	public void setExecuteOperatorId(Long executeOperatorId) {
		this.executeOperatorId = executeOperatorId;
	}

	public Long getInventoryBatchId() {
		return inventoryBatchId;
	}

	public void setInventoryBatchId(Long inventoryBatchId) {
		this.inventoryBatchId = inventoryBatchId;
	}

	public Long getOutBatchId() {
		return outBatchId;
	}

	public void setOutBatchId(Long outBatchId) {
		this.outBatchId = outBatchId;
	}

	public Long getChangedQuantity() {
		return changedQuantity;
	}

	public void setChangedQuantity(Long changedQuantity) {
		this.changedQuantity = changedQuantity;
	}

	public Long getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(Long actualQuantity) {
		this.actualQuantity = actualQuantity;
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

	public Integer getBatchOperate() {
		return batchOperate;
	}

	public void setBatchOperate(Integer batchOperate) {
		this.batchOperate = batchOperate;
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

	public User getExecuteOperator() {
		return executeOperator;
	}

	public void setExecuteOperator(User executeOperator) {
		this.executeOperator = executeOperator;
	}

	public InventoryBatch getOutBatch() {
		return outBatch;
	}

	public void setOutBatch(InventoryBatch outBatch) {
		this.outBatch = outBatch;
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

	public Long[] getWarehouseIds() {
		return warehouseIds;
	}

	public void setWarehouseIds(Long[] warehouseIds) {
		this.warehouseIds = warehouseIds;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
