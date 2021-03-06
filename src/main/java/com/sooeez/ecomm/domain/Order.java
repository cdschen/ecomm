package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "t_order")
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	/* 唯一标示，1000000起 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/* 店铺id */
	@Column(name = "shop_id", nullable = false, insertable = false, updatable = false)
	private Long shopId;

	/* 店铺订单号 */
	@Column(name = "external_sn", nullable = false)
	private String externalSn;

	/* 订单在店铺的创建时间，可以为空。（如果店铺不乐意发过来的话） */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "external_create_time")
	private Date externalCreateTime;

	/* 店铺订单最近更新时间戳 */
	@Column(name = "external_log_time")
	private Long externalLogTime;

	/* 订单在系统创建时间, 为订单导入或在系统创建的时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "internal_create_time", nullable = false)
	private Date internalCreateTime;

	/* 订单最近更新时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_update_time", nullable = false)
	private Date lastUpdateTime;

	/* 商品总件数 */
	@Column(name = "qty_total_item_ordered", nullable = false)
	private Integer qtyTotalItemOrdered;

	/* 已发货商品总件数 */
	@Column(name = "qty_total_item_shipped", nullable = false)
	private Integer qtyTotalItemShipped = 0;

	/* 订单总金额 = 商品金额(subtotal) + 运费(shipping_fee) */
	@Column(name = "grand_total", nullable = false)
	private BigDecimal grandTotal;

	/* 商品总金额（含税) */
	@Column(name = "subtotal", nullable = false)
	private BigDecimal subtotal;

	/* 运费金额 */
	@Column(name = "shipping_fee", nullable = false)
	private BigDecimal shippingFee;

	/* 商品包含的税金 */
	@Column(name = "tax", nullable = false)
	private BigDecimal tax;

	/* 总共开出的发票金额 */
	@Column(name = "total_invoiced", nullable = false)
	private BigDecimal totalInvoiced;

	/* 总共收到的付款金额 */
	@Column(name = "total_paid", nullable = false)
	private BigDecimal totalPaid;

	/* 总共完成的退款金额 */
	@Column(name = "total_refunded", nullable = false)
	private BigDecimal totalRefunded;

	/* 订单结算货币 */
	@Column(name = "currency_id", nullable = false, insertable = false, updatable = false)
	private Long currencyId;

	/* 重量 */
	@Column(name = "weight", nullable = false)
	private Integer weight;

	/*
	 * 客户id, 可以为空，如果店铺不乐意发过来客户信息的话。。。 如果店铺发来了客户信息（用户名，email, 手机号，等等），
	 * 导入时检查有没此客户，如果有，设置id,如果没有， 自动创建新客户，然后设置id
	 */
	@Column(name = "customer_id")
	private Long customerId;

	/* 店铺要求的发货方式描述， 可以为空， 也未必真的通过这种方式发货 */
	@Column(name = "shipping_description")
	private String shippingDescription;

	/* 发货方式， 1=快递， 2=自提， 3=送货上门 */
	@Column(name = "delivery_method")
	private Integer deliveryMethod;

	/* 发件人姓名， 可以为空 */
	@Column(name = "sender_name")
	private String senderName;

	/* 发件地址，可以为空 */
	@Column(name = "sender_address")
	private String senderAddress;

	/* 发件人电话，可以为空 */
	@Column(name = "sender_phone")
	private String senderPhone;

	/* 发件人email, 可以为空 */
	@Column(name = "sender_email")
	private String senderEmail;

	/* 发件人邮编，可以为空 */
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
	@Column(name = "receive_address")
	private String receiveAddress;

	/* 收件邮编 */
	@Column(name = "receive_post")
	private String receivePost;

	/* 订单是否已经被删除 */
	@Column(name = "deleted")
	private Boolean deleted = false;

	/* 订单备注 */
	@Column(name = "memo")
	private String memo;

	/*
	 * Related Properties
	 */

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "shop_id")
	private Shop shop;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "currency_id")
	private Currency currency;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private List<OrderItem> items;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "object_id")
	@Where(clause = "object_type = 1")
	private List<ObjectProcess> processes;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private List<Shipment> shipments;

	/* 一张订单可能有多张与之相连的出库单，但在同一个仓库下，一张订单只可能有一张出库单 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private List<OrderBatch> batches;

	/*
	 * Query Params;
	 */

	/* 下单起始日期 */
	@Transient
	private String internalCreateTimeStart;

	/* 下单结束日期 */
	@Transient
	private String internalCreateTimeEnd;

	/* 快递单号 */
	@Transient
	private String shipNumber;

	/* 发货起始日期 */
	@Transient
	private String shippingTimeStart;

	/* 发货结束日期 */
	@Transient
	private String shippingTimeEnd;

	/* 流程状态 */
	@Transient
	private Integer[] statusIds;

	/* 订单编号 */
	@Transient
	private Long orderId;

	/* 订单编号组 */
	@Transient
	private List<Long> orderIds = new ArrayList<>();

	/* 仓库ID */
	@Transient
	private Long warehouseId;

	@Transient
	private Map<String, Boolean> checkMap = new HashMap<>();

	@Transient
	private Boolean ignoreCheck = false;

	// 仓库id集合
	@Transient
	private Long[] warehouseIds;

	// 店铺id集合
	@Transient
	private Long[] shopIds;

	// order处理行为
	@Transient
	private String action;

	// 查询的时候，判断order是否有出库单
	@Transient
	private Boolean hasOrderBatch;

	//

	public Long getId() {
		return id;
	}

	public Boolean getHasOrderBatch() {
		return hasOrderBatch;
	}

	public void setHasOrderBatch(Boolean hasOrderBatch) {
		this.hasOrderBatch = hasOrderBatch;
	}

	public Long[] getShopIds() {
		return shopIds;
	}

	public void setShopIds(Long[] shopIds) {
		this.shopIds = shopIds;
	}

	public Long[] getWarehouseIds() {
		return warehouseIds;
	}

	public void setWarehouseIds(Long[] warehouseIds) {
		this.warehouseIds = warehouseIds;
	}

	public Boolean getIgnoreCheck() {
		return ignoreCheck;
	}

	public void setIgnoreCheck(Boolean ignoreCheck) {
		this.ignoreCheck = ignoreCheck;
	}

	public Map<String, Boolean> getCheckMap() {
		return checkMap;
	}

	public void setCheckMap(Map<String, Boolean> checkMap) {
		this.checkMap = checkMap;
	}

	public List<OrderBatch> getBatches() {
		return batches;
	}

	public void setBatches(List<OrderBatch> batches) {
		this.batches = batches;
	}

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public List<Long> getOrderIds() {
		return orderIds;
	}

	public void setOrderIds(List<Long> orderIds) {
		this.orderIds = orderIds;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer[] getStatusIds() {
		return statusIds;
	}

	public void setStatusIds(Integer[] statusIds) {
		this.statusIds = statusIds;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public List<ObjectProcess> getProcesses() {
		return processes;
	}

	public void setProcesses(List<ObjectProcess> processes) {
		this.processes = processes;
	}

	public String getInternalCreateTimeStart() {
		return internalCreateTimeStart;
	}

	public void setInternalCreateTimeStart(String internalCreateTimeStart) {
		this.internalCreateTimeStart = internalCreateTimeStart;
	}

	public String getInternalCreateTimeEnd() {
		return internalCreateTimeEnd;
	}

	public void setInternalCreateTimeEnd(String internalCreateTimeEnd) {
		this.internalCreateTimeEnd = internalCreateTimeEnd;
	}

	public String getShipNumber() {
		return shipNumber;
	}

	public void setShipNumber(String shipNumber) {
		this.shipNumber = shipNumber;
	}

	public String getShippingTimeStart() {
		return shippingTimeStart;
	}

	public void setShippingTimeStart(String shippingTimeStart) {
		this.shippingTimeStart = shippingTimeStart;
	}

	public String getShippingTimeEnd() {
		return shippingTimeEnd;
	}

	public void setShippingTimeEnd(String shippingTimeEnd) {
		this.shippingTimeEnd = shippingTimeEnd;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getExternalSn() {
		return externalSn;
	}

	public void setExternalSn(String externalSn) {
		this.externalSn = externalSn;
	}

	public Date getExternalCreateTime() {
		return externalCreateTime;
	}

	public void setExternalCreateTime(Date externalCreateTime) {
		this.externalCreateTime = externalCreateTime;
	}

	public Date getInternalCreateTime() {
		return internalCreateTime;
	}

	public void setInternalCreateTime(Date internalCreateTime) {
		this.internalCreateTime = internalCreateTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Integer getQtyTotalItemOrdered() {
		return qtyTotalItemOrdered;
	}

	public void setQtyTotalItemOrdered(Integer qtyTotalItemOrdered) {
		this.qtyTotalItemOrdered = qtyTotalItemOrdered;
	}

	public Integer getQtyTotalItemShipped() {
		return qtyTotalItemShipped;
	}

	public void setQtyTotalItemShipped(Integer qtyTotalItemShipped) {
		this.qtyTotalItemShipped = qtyTotalItemShipped;
	}

	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getShippingFee() {
		return shippingFee;
	}

	public void setShippingFee(BigDecimal shippingFee) {
		this.shippingFee = shippingFee;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public BigDecimal getTotalInvoiced() {
		return totalInvoiced;
	}

	public void setTotalInvoiced(BigDecimal totalInvoiced) {
		this.totalInvoiced = totalInvoiced;
	}

	public BigDecimal getTotalPaid() {
		return totalPaid;
	}

	public void setTotalPaid(BigDecimal totalPaid) {
		this.totalPaid = totalPaid;
	}

	public BigDecimal getTotalRefunded() {
		return totalRefunded;
	}

	public void setTotalRefunded(BigDecimal totalRefunded) {
		this.totalRefunded = totalRefunded;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getShippingDescription() {
		return shippingDescription;
	}

	public void setShippingDescription(String shippingDescription) {
		this.shippingDescription = shippingDescription;
	}

	public Integer getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(Integer deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
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

	public List<Shipment> getShipments() {
		return shipments;
	}

	public void setShipments(List<Shipment> shipments) {
		this.shipments = shipments;
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

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Long getExternalLogTime()
	{
		return externalLogTime;
	}

	public void setExternalLogTime( Long externalLogTime )
	{
		this.externalLogTime = externalLogTime;
	}

}
