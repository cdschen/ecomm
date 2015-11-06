package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "t_inventory_batch")
public class InventoryBatch implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// 外键，仓库id
	@Column(name = "warehouse_id", nullable = false, insertable = false, updatable = false)
	private Long warehouseId;

	// 当作为出库单时，0，作废，1，待完成，2，已完成
	@Column(name = "type")
	private Integer type;

	// 操作： 1： 正常入库， 2： 正常出库， 3：调整入库， 4：调整出库
	@Column(name = "operate", nullable = false)
	private Integer operate;

	// 创建batch的人
	@Column(name = "user_id", nullable = false, insertable = false, updatable = false)
	private Long userId;

	// 完成出库操作的人
	@Column(name = "execute_operator_id", insertable = false, updatable = false)
	private Long executeOperatorId;

	@Column(name = "order_id")
	private Long orderId;

	// 收货单ID,入库时会有的
	@Column(name = "receive_id")
	private Long receiveId;

	// 采购单id,入库时会用
	@Column(name = "purchase_order_id")
	private Long purchaseOrderId;

	// 创建batch的时候，需要
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "operate_time")
	private Date operateTime;

	// 当出库单完成出库的时候，填入
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "out_inventory_time")
	private Date outInventoryTime;

	@Lob
	@Column(name = "memo")
	private String memo;

	/*
	 * Related Properties
	 */

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "warehouse_id")
	private Warehouse warehouse = new Warehouse();

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id")
	private User user = new User();

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "execute_operator_id")
	private User executeOperator = new User();

	@JoinColumn(name = "inventory_batch_id")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<InventoryBatchItem> items = new ArrayList<>();

	// 一张出库单，在一个仓库，绑定到多张订单
	@JoinColumn(name = "batch_id")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<OrderBatch> orderBatches = new ArrayList<>();

	/*
	 * @Transient Properties
	 */

	@Transient
	private Long total;

	@Transient
	private Date operateTimeStart;

	@Transient
	private Date operateTimeEnd;

	@Transient
	private Date outInventoryTimeStart;

	@Transient
	private Date outInventoryTimeEnd;

	// 仓库id集合
	@Transient
	private Long[] warehouseIds;

	/*
	 * Constructor
	 */

	public InventoryBatch() {
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

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
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

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
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

	public List<InventoryBatchItem> getItems() {
		return items;
	}

	public void setItems(List<InventoryBatchItem> items) {
		this.items = items;
	}

	public List<OrderBatch> getOrderBatches() {
		return orderBatches;
	}

	public void setOrderBatches(List<OrderBatch> orderBatches) {
		this.orderBatches = orderBatches;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Date getOperateTimeStart() {
		return operateTimeStart;
	}

	public void setOperateTimeStart(Date operateTimeStart) {
		this.operateTimeStart = operateTimeStart;
	}

	public Date getOperateTimeEnd() {
		return operateTimeEnd;
	}

	public void setOperateTimeEnd(Date operateTimeEnd) {
		this.operateTimeEnd = operateTimeEnd;
	}

	public Date getOutInventoryTimeStart() {
		return outInventoryTimeStart;
	}

	public void setOutInventoryTimeStart(Date outInventoryTimeStart) {
		this.outInventoryTimeStart = outInventoryTimeStart;
	}

	public Date getOutInventoryTimeEnd() {
		return outInventoryTimeEnd;
	}

	public void setOutInventoryTimeEnd(Date outInventoryTimeEnd) {
		this.outInventoryTimeEnd = outInventoryTimeEnd;
	}

	public Long[] getWarehouseIds() {
		return warehouseIds;
	}

	public void setWarehouseIds(Long[] warehouseIds) {
		this.warehouseIds = warehouseIds;
	}

}
