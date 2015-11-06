package com.sooeez.ecomm.domain;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name = "t_currency")
public class Currency implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "type", nullable = false)
	private Integer type;

	@Column(name = "exchange_rate", nullable = false)
	private Double exchangeRate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_update", nullable = false)
	private Date lastUpdate;

	/*
	 * Related Properties
	 */

	/*
	 * @Transient Properties
	 */

	/*
	 * Constructor
	 */

	public Currency() {
	}

	/*
	 * Functions
	 */

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(Double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}