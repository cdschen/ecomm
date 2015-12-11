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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "t_purchase_order")
public class PurchaseOrder implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	//	#公司名
	@Column(name = "company_name")
	private String companyName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "creator_id", nullable = false, insertable = false, updatable = false)
	private Long creatorId;

	@Column(name = "supplier_id", nullable = false, insertable = false, updatable = false)
	private Long supplierId;

	//	#订货方式， 1=邮件， 2=电话， 3=Ecomm系统
	@Column(name = "booking_type", nullable = false)
	private Integer bookingType;

	//	#采购总件数
	@Column(name = "total_purchased_qty", nullable = false)
	private Long totalPurchasedQty;

	//	#采购预计总金额
	@Column(name = "total_estimate_purchased_amount")
	private BigDecimal totalEstimatePurchasedAmount;

	//	#收货总件数
	@Column(name = "total_delivered_qty", nullable = false)
	private Long totalDeliveredQty;

	//	#转credit总件数
	@Column(name = "total_credit_qty", nullable = false)
	private Long totalCreditQty;

	//	#发票总金额 （实际采购总金额)
	@Column(name = "total_invoice_amount")
	private BigDecimal totalInvoiceAmount;

	//	#收货总金额
	@Column(name = "total_delivered_amount")
	private BigDecimal totalDeliveredAmount;

	//	#Credit总金额
	@Column(name = "total_credit_amount")
	private BigDecimal totalCreditAmount;

	//	#结算货币id
	@Column(name = "currency_id", nullable = false, insertable = false, updatable = false)
	private Long currencyId;

	//	#预计到货时间
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "estimate_receive_date")
	private Date estimateReceiveDate;

	//	#备注
	@Column(name = "memo")
	private String memo;

	//	#运送注意
	@Column(name = "deliver_attention")
	private String deliverAttention;

	//	#最近更新时间
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_update", nullable = false)
	private Date lastUpdate;

	//	#收货人姓名
	@Column(name = "receive_name", nullable = false)
	private String receiveName;

	//	#收货人电话
	@Column(name = "receive_phone", nullable = false)
	private String receivePhone;

	//	#收货人手机
	@Column(name = "receive_mobile", nullable = false)
	private String receiveMobile;

	//	#收货人email
	@Column(name = "receive_email")
	private String receiveEmail;

	//	#收货地址
	@Column(name = "receive_address")
	private String receiveAddress;

	//	#收件邮编
	@Column(name = "receive_post")
	private String receivePost;

	//	#采购单状态
	@Column(name = "status")
	private Integer status;

	/*
	 * Related Properties
	 */

	@OneToOne
	@JoinColumn(name = "creator_id")
	@NotFound(action = NotFoundAction.IGNORE)
	private User creator;

	@OneToOne
	@JoinColumn(name = "supplier_id")
	@NotFound(action = NotFoundAction.IGNORE)
	private Supplier supplier;

	@OneToOne
	@JoinColumn(name = "currency_id")
	@NotFound(action = NotFoundAction.IGNORE)
	private Currency currency;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_order_id")
	private List<PurchaseOrderItem> items;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_order_id")
	private List<PurchaseOrderDelivery> purchaseOrderDeliveries;

//	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//	@JoinColumn(name = "object_id")
//	private List<ObjectProcess> processes;

	/*
	 * Query Params;
	 */
	
	@Transient
	private Long queryWarehouseId;
	
	@Transient
	private Long queryPurchaseOrderId;
	
	@Transient
	private Long queryCreatorId;
	
	@Transient
	private Long querySupplierId;
	
	/* 采购单创建起始日期 */
	@Transient
	private String queryCreateTimeStart;

	/* 采购单创建结束日期 */
	@Transient
	private String queryCreateTimeEnd;

	/*
	 * 创建采购单返回参数;
	 */

	@Transient
	private Boolean isSupplierProductCodeChanged;
	
	/*
	 * OperationReview Params
	 */

	@Transient
	private Map<String, Boolean> checkMap = new HashMap<>();

	@Transient
	private Boolean ignoreCheck = false;
	
	
	/* 临时变量 */
	@Transient
	private List<SupplierProduct> supplierProducts;
	

	//
	
	@Transient
	private List<PurchaseOrder> purchaseOrders;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public Integer getBookingType() {
		return bookingType;
	}

	public void setBookingType(Integer bookingType) {
		this.bookingType = bookingType;
	}

	public Long getTotalPurchasedQty() {
		return totalPurchasedQty;
	}

	public void setTotalPurchasedQty(Long totalPurchasedQty) {
		this.totalPurchasedQty = totalPurchasedQty;
	}

	public BigDecimal getTotalEstimatePurchasedAmount() {
		return totalEstimatePurchasedAmount;
	}

	public void setTotalEstimatePurchasedAmount(BigDecimal totalEstimatePurchasedAmount) {
		this.totalEstimatePurchasedAmount = totalEstimatePurchasedAmount;
	}

	public Long getTotalDeliveredQty() {
		return totalDeliveredQty;
	}

	public void setTotalDeliveredQty(Long totalDeliveredQty) {
		this.totalDeliveredQty = totalDeliveredQty;
	}

	public Long getTotalCreditQty() {
		return totalCreditQty;
	}

	public void setTotalCreditQty(Long totalCreditQty) {
		this.totalCreditQty = totalCreditQty;
	}

	public BigDecimal getTotalInvoiceAmount() {
		return totalInvoiceAmount;
	}

	public void setTotalInvoiceAmount(BigDecimal totalInvoiceAmount) {
		this.totalInvoiceAmount = totalInvoiceAmount;
	}

	public BigDecimal getTotalDeliveredAmount() {
		return totalDeliveredAmount;
	}

	public void setTotalDeliveredAmount(BigDecimal totalDeliveredAmount) {
		this.totalDeliveredAmount = totalDeliveredAmount;
	}

	public BigDecimal getTotalCreditAmount() {
		return totalCreditAmount;
	}

	public void setTotalCreditAmount(BigDecimal totalCreditAmount) {
		this.totalCreditAmount = totalCreditAmount;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public Date getEstimateReceiveDate() {
		return estimateReceiveDate;
	}

	public void setEstimateReceiveDate(Date estimateReceiveDate) {
		this.estimateReceiveDate = estimateReceiveDate;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getDeliverAttention() {
		return deliverAttention;
	}

	public void setDeliverAttention(String deliverAttention) {
		this.deliverAttention = deliverAttention;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
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

	public String getReceiveMobile() {
		return receiveMobile;
	}

	public void setReceiveMobile(String receiveMobile) {
		this.receiveMobile = receiveMobile;
	}

	public String getReceiveEmail() {
		return receiveEmail;
	}

	public void setReceiveEmail(String receiveEmail) {
		this.receiveEmail = receiveEmail;
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

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public List<PurchaseOrderDelivery> getPurchaseOrderDeliveries() {
		return purchaseOrderDeliveries;
	}

	public void setPurchaseOrderDeliveries(List<PurchaseOrderDelivery> purchaseOrderDeliveries) {
		this.purchaseOrderDeliveries = purchaseOrderDeliveries;
	}

//	public List<ObjectProcess> getProcesses() {
//		return processes;
//	}
//
//	public void setProcesses(List<ObjectProcess> processes) {
//		this.processes = processes;
//	}

	public List<PurchaseOrderItem> getItems() {
		return items;
	}

	public void setItems(List<PurchaseOrderItem> items) {
		this.items = items;
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

	public Long getQueryCreatorId() {
		return queryCreatorId;
	}

	public void setQueryCreatorId(Long queryCreatorId) {
		this.queryCreatorId = queryCreatorId;
	}

	public Long getQuerySupplierId() {
		return querySupplierId;
	}

	public void setQuerySupplierId(Long querySupplierId) {
		this.querySupplierId = querySupplierId;
	}

	public String getQueryCreateTimeStart() {
		return queryCreateTimeStart;
	}

	public void setQueryCreateTimeStart(String queryCreateTimeStart) {
		this.queryCreateTimeStart = queryCreateTimeStart;
	}

	public String getQueryCreateTimeEnd() {
		return queryCreateTimeEnd;
	}

	public void setQueryCreateTimeEnd(String queryCreateTimeEnd) {
		this.queryCreateTimeEnd = queryCreateTimeEnd;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<PurchaseOrder> getPurchaseOrders() {
		return purchaseOrders;
	}

	public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
		this.purchaseOrders = purchaseOrders;
	}

	public List<SupplierProduct> getSupplierProducts() {
		return supplierProducts;
	}

	public void setSupplierProducts(List<SupplierProduct> supplierProducts) {
		this.supplierProducts = supplierProducts;
	}

	public Boolean getIsSupplierProductCodeChanged() {
		return isSupplierProductCodeChanged;
	}

	public void setIsSupplierProductCodeChanged(Boolean isSupplierProductCodeChanged) {
		this.isSupplierProductCodeChanged = isSupplierProductCodeChanged;
	}
	

}
