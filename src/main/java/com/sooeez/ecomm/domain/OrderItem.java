package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_order_item")
public class OrderItem implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	/* 唯一标示，1000000起 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/* 订单id */
	@Column(name = "order_id", nullable = false)
	private Long orderId;

	/* 商品id */
	@Column(name = "product_id", nullable = false)
	private Long productId;

	/* 商品外部SKU(店铺提供的SKU，可以为空) */
	@Column(name = "external_sku")
	private String externalSku;

	/* 商品sku */
	@Column(name = "sku", nullable = false)
	private String sku;

	/* 商品外部名称（店铺提供，可以为空) */
	@Column(name = "external_name")
	private String external_name;

	/* 商品名称 */
	@Column(name = "name", nullable = false)
	private String name;
	
	/* 商品重量 */
	@Column(name = "unit_weight", nullable = false)
	private Integer unitWeight;

	/* 订购数量 */
	@Column(name = "qty_ordered", nullable = false)
	private Integer qtyOrdered;

	/* 已发货数量 */
	@Column(name = "qty_shipped", nullable = false)
	private Integer qtyShipped;

	/* 单位售价（含税） */
	@Column(name = "unit_price", nullable = false)
	private BigDecimal unitPrice;

	/* 单位成本 */
	@Column(name = "unit_cost", nullable = false)
	private BigDecimal unitCost;

	/* 单位售价包含的税金 */
	@Column(name = "unit_gst", nullable = false)
	private BigDecimal unitGst;
	
	//

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getExternalSku() {
		return externalSku;
	}

	public void setExternalSku(String externalSku) {
		this.externalSku = externalSku;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getExternal_name() {
		return external_name;
	}

	public void setExternal_name(String external_name) {
		this.external_name = external_name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getUnitWeight() {
		return unitWeight;
	}

	public void setUnitWeight(Integer unitWeight) {
		this.unitWeight = unitWeight;
	}

	public Integer getQtyOrdered() {
		return qtyOrdered;
	}

	public void setQtyOrdered(Integer qtyOrdered) {
		this.qtyOrdered = qtyOrdered;
	}

	public Integer getQtyShipped() {
		return qtyShipped;
	}

	public void setQtyShipped(Integer qtyShipped) {
		this.qtyShipped = qtyShipped;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(BigDecimal unitCost) {
		this.unitCost = unitCost;
	}

	public BigDecimal getUnitGst() {
		return unitGst;
	}

	public void setUnitGst(BigDecimal unitGst) {
		this.unitGst = unitGst;
	}
	
	
	
}
