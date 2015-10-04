package com.sooeez.ecomm.dto.api;

import java.io.Serializable;
import java.math.BigDecimal;

public class DTO_OrderItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String sku;
	
	private String shop_product_sku;
	
	private String name;
	
	private String shop_product_name;
	
	private Integer unit_weight;
	
	private Integer qty_ordered;
	
	private Integer qty_shipped;
	
	private BigDecimal unit_price;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getShop_product_sku() {
		return shop_product_sku;
	}

	public void setShop_product_sku(String shop_product_sku) {
		this.shop_product_sku = shop_product_sku;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShop_product_name() {
		return shop_product_name;
	}

	public void setShop_product_name(String shop_product_name) {
		this.shop_product_name = shop_product_name;
	}

	public Integer getUnit_weight() {
		return unit_weight;
	}

	public void setUnit_weight(Integer unit_weight) {
		this.unit_weight = unit_weight;
	}

	public Integer getQty_ordered() {
		return qty_ordered;
	}

	public void setQty_ordered(Integer qty_ordered) {
		this.qty_ordered = qty_ordered;
	}

	public Integer getQty_shipped() {
		return qty_shipped;
	}

	public void setQty_shipped(Integer qty_shipped) {
		this.qty_shipped = qty_shipped;
	}

	public BigDecimal getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(BigDecimal unit_price) {
		this.unit_price = unit_price;
	}

}
