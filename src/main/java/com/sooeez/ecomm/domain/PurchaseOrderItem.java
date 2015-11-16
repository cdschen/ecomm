package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "t_purchase_order_item")
public class PurchaseOrderItem implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	//	#采购单id
	@Column(name = "purchase_order_id", nullable = false)
	private Long purchaseOrderId;

	//	#采购数量
	@Column(name = "purchase_qty", nullable = false)
	private Long purchaseQty;

	//	#商品预计采购单价
	@Column(name = "estimate_purchase_unit_price")
	private BigDecimal estimatePurchaseUnitPrice;
	

	/*
	 * Related Properties
	 */

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "supplier_product_id")
	private SupplierProduct supplierProduct;
	
	/*
	 * OperationReview Params
	 */
	
	//	#待收货数量
	@Transient
	private Long pendingQty;
	
	//	#实际收货数量
	@Transient
	private Long realReceivedQty;
	
	//	#Credit数量
	@Transient
	private Long creditQty;
	
	//	#Back Order数量
	@Transient
	private Long backOrderQty;
	
	//	#实际采购单价
	@Transient
	private BigDecimal realPurchaseUnitPrice;

	@Transient
	private Map<String, Boolean> checkMap = new HashMap<>();

	@Transient
	private Boolean ignoreCheck = false;

	
	/*
	 * 采购单：采购［供应商新品］时，临时用到的毫秒，匹配［供应商产品］和［采购单详情］是否关联
	 */
	@Transient
	private Long currentTimeMillis;
	
	
	
	

	//


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

	public Long getPurchaseQty() {
		return purchaseQty;
	}

	public void setPurchaseQty(Long purchaseQty) {
		this.purchaseQty = purchaseQty;
	}

	public BigDecimal getEstimatePurchaseUnitPrice() {
		return estimatePurchaseUnitPrice;
	}

	public void setEstimatePurchaseUnitPrice(BigDecimal estimatePurchaseUnitPrice) {
		this.estimatePurchaseUnitPrice = estimatePurchaseUnitPrice;
	}

	public Long getPendingQty() {
		return pendingQty;
	}

	public void setPendingQty(Long pendingQty) {
		this.pendingQty = pendingQty;
	}

	public Long getRealReceivedQty() {
		return realReceivedQty;
	}

	public void setRealReceivedQty(Long realReceivedQty) {
		this.realReceivedQty = realReceivedQty;
	}

	public Long getCreditQty() {
		return creditQty;
	}

	public void setCreditQty(Long creditQty) {
		this.creditQty = creditQty;
	}

	public Long getBackOrderQty() {
		return backOrderQty;
	}

	public void setBackOrderQty(Long backOrderQty) {
		this.backOrderQty = backOrderQty;
	}

	public BigDecimal getRealPurchaseUnitPrice() {
		return realPurchaseUnitPrice;
	}

	public void setRealPurchaseUnitPrice(BigDecimal realPurchaseUnitPrice) {
		this.realPurchaseUnitPrice = realPurchaseUnitPrice;
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

	public SupplierProduct getSupplierProduct() {
		return supplierProduct;
	}

	public void setSupplierProduct(SupplierProduct supplierProduct) {
		this.supplierProduct = supplierProduct;
	}

	public Long getCurrentTimeMillis() {
		return currentTimeMillis;
	}

	public void setCurrentTimeMillis(Long currentTimeMillis) {
		this.currentTimeMillis = currentTimeMillis;
	}

}
