package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "t_purchase_order_delivery_item")
public class PurchaseOrderDeliveryItem implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	//	#收货单id
	@Column(name = "purchase_order_delivery_id", nullable = false)
	private Long purchaseOrderDeliveryId;
	
	//	#采购单详情id
	@Column(name = "purchase_order_item_id", nullable = false)
	private Long purchaseOrderItemId;
	
	@Column(name = "supplier_product_id", nullable = false, insertable = false, updatable = false)
	private Long supplierProductId;
	
	//	#商品实际采购单价
	@Column(name = "real_purchase_unit_price")
	private BigDecimal realPurchaseUnitPrice;
	
	//	#收货数量
	@Column(name = "receive_qty", nullable = false)
	private Long receiveQty;
	
	//	#转credit数量
	@Column(name = "credit_qty", nullable = false)
	private Long creditQty;
	
	//	#保质期
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expire_date")
	private Date expireDate;
	
	//	#留言
	@Column(name = "comment")
	private String comment;
	

	/*
	 * Related Properties
	 */

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "supplier_product_id")
	private SupplierProduct supplierProduct;

//	@OneToOne
//	@NotFound(action = NotFoundAction.IGNORE)
//	@JoinColumn(name = "purchase_order_item_id")
//	private PurchaseOrderItem purchaseOrderItem;


	//


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getPurchaseOrderDeliveryId() {
		return purchaseOrderDeliveryId;
	}


	public void setPurchaseOrderDeliveryId(Long purchaseOrderDeliveryId) {
		this.purchaseOrderDeliveryId = purchaseOrderDeliveryId;
	}


	public Long getPurchaseOrderItemId() {
		return purchaseOrderItemId;
	}


	public void setPurchaseOrderItemId(Long purchaseOrderItemId) {
		this.purchaseOrderItemId = purchaseOrderItemId;
	}


	public Long getSupplierProductId() {
		return supplierProductId;
	}


	public void setSupplierProductId(Long supplierProductId) {
		this.supplierProductId = supplierProductId;
	}


	public BigDecimal getRealPurchaseUnitPrice() {
		return realPurchaseUnitPrice;
	}


	public void setRealPurchaseUnitPrice(BigDecimal realPurchaseUnitPrice) {
		this.realPurchaseUnitPrice = realPurchaseUnitPrice;
	}


	public Long getReceiveQty() {
		return receiveQty;
	}


	public void setReceiveQty(Long receiveQty) {
		this.receiveQty = receiveQty;
	}


	public Long getCreditQty() {
		return creditQty;
	}


	public void setCreditQty(Long creditQty) {
		this.creditQty = creditQty;
	}


	public SupplierProduct getSupplierProduct() {
		return supplierProduct;
	}


	public void setSupplierProduct(SupplierProduct supplierProduct) {
		this.supplierProduct = supplierProduct;
	}


	public Date getExpireDate() {
		return expireDate;
	}


	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


//	public PurchaseOrderItem getPurchaseOrderItem() {
//		return purchaseOrderItem;
//	}
//
//
//	public void setPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
//		this.purchaseOrderItem = purchaseOrderItem;
//	}

}
