package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.math.BigDecimal;
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

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "t_shipment")
public class Shipment implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/* 订单编号 */
	@Column(name = "order_id")
	private Long orderId;

	/* 操作员编号 */
	@Column(name = "operator_id")
	private Long operatorId;

	/* 快递公司编号 */
	@Column(name = "courier_id")
	private Long courierId;

	/* 快递单号 */
	@Column(name = "ship_number")
	private String shipNumber;

	/* 发货单状态 */
	@Column(name = "ship_status", nullable = false)
	private Integer shipStatus;

	/* 发货商品总数 */
	@Column(name = "qty_total_item_shipped", nullable = false)
	private Integer qtyTotalItemShipped;

	/* 发货总重量 */
	@Column(name = "total_weight", nullable = false)
	private Integer totalWeight;

	/* 运费成本 */
	@Column(name = "shipfee_cost", nullable = false)
	private BigDecimal shipfeeCost;

	/* 创建时间 */
	@Column(name = "create_time", nullable = false)
	private Date createTime;

	/* 最近更新时间 */
	@Column(name = "last_update", nullable = false)
	private Date lastUpdate;

	/* 快递取件时间 */
	@Column(name = "pickup_time")
	private Date pickupTime;

	/* 签收时间 */
	@Column(name = "signup_time")
	private Date signupTime;

	/* 备注 */
	@Column(name = "memo")
	private String memo;

	/* 发件人姓名 */
	@Column(name = "sender_name")
	private String senderName;

	/* 发件地址 */
	@Column(name = "sender_address")
	private String senderAddress;

	/* 发件人电话 */
	@Column(name = "sender_phone")
	private String senderPhone;

	/* 发件人email */
	@Column(name = "sender_email")
	private String senderEmail;

	/* 发件人邮编 */
	@Column(name = "sender_post")
	private String senderPost;

	/* 收件人姓名 */
	@Column(name = "receive_name", nullable = false)
	private String receiveName;

	/* 收件人电话 */
	@Column(name = "receive_phone", nullable = false)
	private String receivePhone;

	/* 收件人email */
	@Column(name = "receive_email")
	private String receiveEmail;

	/* 收件国家 */
	@Column(name = "receive_country")
	private String receiveCountry;

	/* 收件省 */
	@Column(name = "receive_province")
	private String receiveProvince;

	/* 收件城市 */
	@Column(name = "receive_city")
	private String receiveCity;

	/* 收件地址 */
	@Column(name = "receive_address", nullable = false)
	private String receiveAddress;

	/* 收件邮编 */
	@Column(name = "receive_post")
	private String receivePost;

	/* 发货仓库编号 */
	@Column(name = "ship_warehouse_id")
	private Long shipWarehouseId;

	/*
	 * Related Properties
	 */
	
//	@OneToOne
//	@NotFound(action = NotFoundAction.IGNORE)
//	@JoinColumn(name = "order_id", insertable = false, updatable = false)
//	private Order order;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "operator_id", insertable = false, updatable = false)
	private User user;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "courier_id", insertable = false, updatable = false)
	private Courier courier;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "shipment_id")
	private List<ShipmentItem> shipmentItems;

	//

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShipNumber() {
		return shipNumber;
	}

	public void setShipNumber(String shipNumber) {
		this.shipNumber = shipNumber;
	}

	public Integer getShipStatus() {
		return shipStatus;
	}

	public void setShipStatus(Integer shipStatus) {
		this.shipStatus = shipStatus;
	}

	public Integer getQtyTotalItemShipped() {
		return qtyTotalItemShipped;
	}

	public void setQtyTotalItemShipped(Integer qtyTotalItemShipped) {
		this.qtyTotalItemShipped = qtyTotalItemShipped;
	}

	public Integer getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(Integer totalWeight) {
		this.totalWeight = totalWeight;
	}

	public BigDecimal getShipfeeCost() {
		return shipfeeCost;
	}

	public void setShipfeeCost(BigDecimal shipfeeCost) {
		this.shipfeeCost = shipfeeCost;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Date getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(Date pickupTime) {
		this.pickupTime = pickupTime;
	}

	public Date getSignupTime() {
		return signupTime;
	}

	public void setSignupTime(Date signupTime) {
		this.signupTime = signupTime;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderPhone() {
		return senderPhone;
	}

	public void setSenderPhone(String senderPhone) {
		this.senderPhone = senderPhone;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getSenderPost() {
		return senderPost;
	}

	public void setSenderPost(String senderPost) {
		this.senderPost = senderPost;
	}

	public String getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public String getReceivePhone() {
		return receivePhone;
	}

	public void setReceivePhone(String receivePhone) {
		this.receivePhone = receivePhone;
	}

	public String getReceiveEmail() {
		return receiveEmail;
	}

	public void setReceiveEmail(String receiveEmail) {
		this.receiveEmail = receiveEmail;
	}

	public String getReceiveCountry() {
		return receiveCountry;
	}

	public void setReceiveCountry(String receiveCountry) {
		this.receiveCountry = receiveCountry;
	}

	public String getReceiveProvince() {
		return receiveProvince;
	}

	public void setReceiveProvince(String receiveProvince) {
		this.receiveProvince = receiveProvince;
	}

	public String getReceiveCity() {
		return receiveCity;
	}

	public void setReceiveCity(String receiveCity) {
		this.receiveCity = receiveCity;
	}

	public String getReceiveAddress() {
		return receiveAddress;
	}

	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}

	public String getReceivePost() {
		return receivePost;
	}

	public void setReceivePost(String receivePost) {
		this.receivePost = receivePost;
	}

//	public Order getOrder() {
//		return order;
//	}
//
//	public void setOrder(Order order) {
//		this.order = order;
//	}

//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}

	public Courier getCourier() {
		return courier;
	}

	public void setCourier(Courier courier) {
		this.courier = courier;
	}

	public Long getShipWarehouseId() {
		return shipWarehouseId;
	}

	public void setShipWarehouseId(Long shipWarehouseId) {
		this.shipWarehouseId = shipWarehouseId;
	}

	public List<ShipmentItem> getShipmentItems() {
		return shipmentItems;
	}

	public void setShipmentItems(List<ShipmentItem> shipmentItems) {
		this.shipmentItems = shipmentItems;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public Long getCourierId() {
		return courierId;
	}

	public void setCourierId(Long courierId) {
		this.courierId = courierId;
	}

//	public Order getOrder() {
//		return order;
//	}
//
//	public void setOrder(Order order) {
//		this.order = order;
//	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
