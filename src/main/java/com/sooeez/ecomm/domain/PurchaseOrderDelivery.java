package com.sooeez.ecomm.domain;

import java.io.Serializable;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "t_purchase_order_delivery")
public class PurchaseOrderDelivery implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// #采购单id
	@Column(name = "purchase_order_id", nullable = false)
	private Long purchaseOrderId;

	// #收货时间
	@Column(name = "receive_time", nullable = false)
	private Date receiveTime;

	// #收货人
	@Column(name = "receive_user_id", nullable = false, insertable = false, updatable = false)
	private Long receiveUserId;

	/*
	 * Related Properties
	 */

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "receive_user_id")
	private User receiveUser;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_order_delivery_id")
	private List<PurchaseOrderDeliveryItem> items;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "receive_id")
	private List<InventoryBatch> batches;

	/*
	 * @Transient Properties
	 */

	@Transient
	private Long queryWarehouseId;

	@Transient
	private Long queryPurchaseOrderId;

	@Transient
	private Long queryPurchaseOrderDeliveryId;

	/* 收货单创建起始日期 */
	@Transient
	private String queryReceiveTimeStart;

	/* 收货单创建结束日期 */
	@Transient
	private String queryReceiveTimeEnd;

	/*
	 * Constructor
	 */

	public PurchaseOrderDelivery() {
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

	public Long getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Long purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public Date getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	public Long getReceiveUserId() {
		return receiveUserId;
	}

	public void setReceiveUserId(Long receiveUserId) {
		this.receiveUserId = receiveUserId;
	}

	public User getReceiveUser() {
		return receiveUser;
	}

	public void setReceiveUser(User receiveUser) {
		this.receiveUser = receiveUser;
	}

	public List<PurchaseOrderDeliveryItem> getItems() {
		return items;
	}

	public void setItems(List<PurchaseOrderDeliveryItem> items) {
		this.items = items;
	}

	public List<InventoryBatch> getBatches() {
		return batches;
	}

	public void setBatches(List<InventoryBatch> batches) {
		this.batches = batches;
	}

	public Long getQueryWarehouseId() {
		return queryWarehouseId;
	}

	public void setQueryWarehouseId(Long queryWarehouseId) {
		this.queryWarehouseId = queryWarehouseId;
	}

	public Long getQueryPurchaseOrderId() {
		return queryPurchaseOrderId;
	}

	public void setQueryPurchaseOrderId(Long queryPurchaseOrderId) {
		this.queryPurchaseOrderId = queryPurchaseOrderId;
	}

	public Long getQueryPurchaseOrderDeliveryId() {
		return queryPurchaseOrderDeliveryId;
	}

	public void setQueryPurchaseOrderDeliveryId(
			Long queryPurchaseOrderDeliveryId) {
		this.queryPurchaseOrderDeliveryId = queryPurchaseOrderDeliveryId;
	}

	public String getQueryReceiveTimeStart() {
		return queryReceiveTimeStart;
	}

	public void setQueryReceiveTimeStart(String queryReceiveTimeStart) {
		this.queryReceiveTimeStart = queryReceiveTimeStart;
	}

	public String getQueryReceiveTimeEnd() {
		return queryReceiveTimeEnd;
	}

	public void setQueryReceiveTimeEnd(String queryReceiveTimeEnd) {
		this.queryReceiveTimeEnd = queryReceiveTimeEnd;
	}

}
