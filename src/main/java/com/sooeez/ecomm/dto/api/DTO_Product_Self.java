package com.sooeez.ecomm.dto.api;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sooeez.ecomm.dto.api.general.DTO_Process_Status;

public class DTO_Product_Self implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String sku;
	
	private String name;
	
	private List<DTO_Process_Status> status;
	
	private String description;
	
	private String short_description;
	
	// 如果是［自营店铺］则返回 prices map, 数据结构为 prices : { level_1 : xxx, level_2 : xxx }
	private Map<String, BigDecimal> prices = new HashMap<String, BigDecimal>();
	
	private String currecy;
	
	// 通过店铺通道，根据产品编号到库存匹配
	private Integer available_stock;
	
	private Integer weight;
	
	private List<String> recent_expire_dates;
	
	private Date create_time;		// "2014-06-11T17:38:06-0700"
	
	private Date updated_time;	// "2014-06-11T20:09:23-0700"

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

	public List<DTO_Process_Status> getStatus() {
		return status;
	}

	public void setStatus(List<DTO_Process_Status> status) {
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

	public Map<String, BigDecimal> getPrices() {
		return prices;
	}

	public void setPrices(Map<String, BigDecimal> prices) {
		this.prices = prices;
	}

	public String getCurrecy() {
		return currecy;
	}

	public void setCurrecy(String currecy) {
		this.currecy = currecy;
	}

	public Integer getAvailable_stock() {
		return available_stock;
	}

	public void setAvailable_stock(Integer available_stock) {
		this.available_stock = available_stock;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public List<String> getRecent_expire_dates() {
		return recent_expire_dates;
	}

	public void setRecent_expire_dates(List<String> recent_expire_dates) {
		this.recent_expire_dates = recent_expire_dates;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(Date updated_time) {
		this.updated_time = updated_time;
	}
}
