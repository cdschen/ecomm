package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/* 创建人编号 */
	@Column(name = "operator_id")
	private Long operatorId;

	/* 发货人编号 */
	@Column(name = "execute_operator_id")
	private Long executeOperatorId;

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

	/* 快递单打印详情内容 */
	@Column(name = "print_item_content")
	private String printItemContent;
	
	/*
	 * Related Properties
	 */

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "ship_warehouse_id", insertable = false, updatable = false)
	private Warehouse warehouse;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "operator_id", insertable = false, updatable = false)
	private User operator;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "execute_operator_id", insertable = false, updatable = false)
	private User executeOperator;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "courier_id", insertable = false, updatable = false)
	private Courier courier;

	@OneToMany( cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY )
	@JoinColumn(name = "shipment_id")
	private List<ShipmentItem> shipmentItems;

	//

	/* 发货单集合 */
	@Transient
	private List<Shipment> shipments;

	/* 店铺编号 */
	@Transient
	private Long shopId;

	/* 获取店铺用 */
	@Transient
	private Shop shop;

	/* 创建起始日期 */
	@Transient
	private String createTimeStart;

	/* 创建结束日期 */
	@Transient
	private String createTimeEnd;

	/* 更新起始日期 */
	@Transient
	private String lastUpdateStart;

	/* 更新结束日期 */
	@Transient
	private String lastUpdateEnd;

	/* 取件起始日期 */
	@Transient
	private String pickupTimeStart;

	/* 取件结束日期 */
	@Transient
	private String pickupTimeEnd;

	/* 签收起始日期 */
	@Transient
	private String signupTimeStart;

	/* 签收结束日期 */
	@Transient
	private String signupTimeEnd;

	/* 货运描述 */
	@Transient
	private String shippingDescription;
	
	/* 发货方式 */
	@Transient
	private Integer deliveryMethod;
	
	/* 发货方式 */
	@Transient
	private List<Long> deleteIds;

	/**
	 * 复核操作
	 */
	/* 检查项 */
	@Transient
	private Map<String, Boolean> checkMap = new HashMap<>();

	@Transient
	private Boolean ignoreCheck = false;

	/**
	 * 导入发货单验证，插入所需属性
	 */
	/*
	 * 物品内容
	 */
	@Transient
	private String productContent;

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

	public User getOperator() {
		return operator;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}

	public User getExecuteOperator() {
		return executeOperator;
	}

	public void setExecuteOperator(User executeOperator) {
		this.executeOperator = executeOperator;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getLastUpdateStart() {
		return lastUpdateStart;
	}

	public void setLastUpdateStart(String lastUpdateStart) {
		this.lastUpdateStart = lastUpdateStart;
	}

	public String getLastUpdateEnd() {
		return lastUpdateEnd;
	}

	public void setLastUpdateEnd(String lastUpdateEnd) {
		this.lastUpdateEnd = lastUpdateEnd;
	}

	public String getPickupTimeStart() {
		return pickupTimeStart;
	}

	public void setPickupTimeStart(String pickupTimeStart) {
		this.pickupTimeStart = pickupTimeStart;
	}

	public String getPickupTimeEnd() {
		return pickupTimeEnd;
	}

	public void setPickupTimeEnd(String pickupTimeEnd) {
		this.pickupTimeEnd = pickupTimeEnd;
	}

	public String getSignupTimeStart() {
		return signupTimeStart;
	}

	public void setSignupTimeStart(String signupTimeStart) {
		this.signupTimeStart = signupTimeStart;
	}

	public String getSignupTimeEnd() {
		return signupTimeEnd;
	}

	public void setSignupTimeEnd(String signupTimeEnd) {
		this.signupTimeEnd = signupTimeEnd;
	}

	public List<Shipment> getShipments() {
		return shipments;
	}

	public void setShipments(List<Shipment> shipments) {
		this.shipments = shipments;
	}

	public Long getExecuteOperatorId() {
		return executeOperatorId;
	}

	public void setExecuteOperatorId(Long executeOperatorId) {
		this.executeOperatorId = executeOperatorId;
	}

	public Map<String, Boolean> getCheckMap() {
		return checkMap;
	}

	public void setCheckMap(Map<String, Boolean> checkMap) {
		this.checkMap = checkMap;
	}

	public Boolean getIgnoreCheck() {
		return ignoreCheck;
	}

	public void setIgnoreCheck(Boolean ignoreCheck) {
		this.ignoreCheck = ignoreCheck;
	}

	public String getProductContent() {
		return productContent;
	}

	public void setProductContent(String productContent) {
		this.productContent = productContent;
	}

	public String getShippingDescription() {
		return shippingDescription;
	}

	public void setShippingDescription(String shippingDescription) {
		this.shippingDescription = shippingDescription;
	}

	public String getPrintItemContent() {
		return printItemContent;
	}

	public void setPrintItemContent(String printItemContent) {
		this.printItemContent = printItemContent;
	}

	public Integer getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(Integer deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public List< Long > getDeleteIds()
	{
		return deleteIds;
	}

	public void setDeleteIds( List< Long > deleteIds )
	{
		this.deleteIds = deleteIds;
	}

}
