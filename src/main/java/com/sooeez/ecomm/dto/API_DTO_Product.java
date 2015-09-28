package com.sooeez.ecomm.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public class API_DTO_Product implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String sku;
	
	private String name;
	
	private String status;
	
	private String description;
	
	private String short_description;
	
	// 如果是［合作店铺］则返回 price
	private BigDecimal price;
	
	// 如果是［自营店铺］则返回 prices map, 数据结构为 prices : { level_1 : xxx, level_2 : xxx }
	private Map<String, Object> prices;
	
	private String currecy;
	
	// 通过店铺通道，根据产品编号到库存匹配
	private Long available_stock;
	
	private Integer weight;
	
	private String recent_expire_date;
	
	private String create_time;		// "2014-06-11T17:38:06-0700"
	
	private String updated_time;	// "2014-06-11T20:09:23-0700"
	

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShort_description() {
		return short_description;
	}

	public void setShort_description(String short_description) {
		this.short_description = short_description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCurrecy() {
		return currecy;
	}

	public void setCurrecy(String currecy) {
		this.currecy = currecy;
	}

	public Long getAvailable_stock() {
		return available_stock;
	}

	public void setAvailable_stock(Long available_stock) {
		this.available_stock = available_stock;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getRecent_expire_date() {
		return recent_expire_date;
	}

	public void setRecent_expire_date(String recent_expire_date) {
		this.recent_expire_date = recent_expire_date;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}

	public Map<String, Object> getPrices() {
		return prices;
	}

	public void setPrices(Map<String, Object> prices) {
		this.prices = prices;
	}

}
