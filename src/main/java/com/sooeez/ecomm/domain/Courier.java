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
import javax.persistence.Transient;

@Entity
@Table(name = "t_courier")
public class Courier implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "shipfee_cost_per_kg", nullable = false)
	private BigDecimal shipfeeCostPerKg;

	@Column(name = "website")
	private String website;

	@Column(name = "shipment_lookup_url", nullable = false)
	private String shipmentLookupUrl;

	@Column(name = "status", nullable = false)
	private Integer status;

	/*
	 * Related Properties
	 */
	@OneToOne
	@JoinColumn(name = "shipfee_currency_id")
	private Currency currency;

	/* 生成发货单时选择快递公司后指定的起始快递单号 */
	@Transient
	private Long startCourierId;

	//

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getShipfeeCostPerKg() {
		return shipfeeCostPerKg;
	}

	public void setShipfeeCostPerKg(BigDecimal shipfeeCostPerKg) {
		this.shipfeeCostPerKg = shipfeeCostPerKg;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getShipmentLookupUrl() {
		return shipmentLookupUrl;
	}

	public void setShipmentLookupUrl(String shipmentLookupUrl) {
		this.shipmentLookupUrl = shipmentLookupUrl;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Long getStartCourierId() {
		return startCourierId;
	}

	public void setStartCourierId(Long startCourierId) {
		this.startCourierId = startCourierId;
	}
	
}
