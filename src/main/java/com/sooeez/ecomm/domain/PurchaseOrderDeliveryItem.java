package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
	
	@Column(name = "product_id", nullable = false, insertable = false, updatable = false)
	private Long productId;
	
	//	#商品实际采购单价
	@Column(name = "real_purchase_unit_price")
	private BigDecimal realPurchaseUnitPrice;
	
	//	#收货数量
	@Column(name = "receive_qty", nullable = false)
	private Long receiveQty;
	
	//	#转credit数量
	@Column(name = "credit_qty", nullable = false)
	private Long creditQty;
	

	/*
	 * Related Properties
	 */

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "product_id")
	private Product product;

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


	public Long getProductId() {
		return productId;
	}


	public void setProductId(Long productId) {
		this.productId = productId;
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


	public Product getProduct() {
		return product;
	}


	public void setProduct(Product product) {
		this.product = product;
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
