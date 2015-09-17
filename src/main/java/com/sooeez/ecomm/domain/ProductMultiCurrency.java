package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "t_product_multicurrency")
public class ProductMultiCurrency implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "price_l1", nullable = false)
	private BigDecimal priceL1;

	@Column(name = "price_l2", nullable = false)
	private BigDecimal priceL2;

	@Column(name = "price_l3", nullable = false)
	private BigDecimal priceL3;

	@Column(name = "price_l4", nullable = false)
	private BigDecimal priceL4;

	@Column(name = "price_l5", nullable = false)
	private BigDecimal priceL5;

	@Column(name = "price_l6", nullable = false)
	private BigDecimal priceL6;

	@Column(name = "price_l7", nullable = false)
	private BigDecimal priceL7;

	@Column(name = "price_l8", nullable = false)
	private BigDecimal priceL8;

	@Column(name = "price_l9", nullable = false)
	private BigDecimal priceL9;

	@Column(name = "price_l10", nullable = false)
	private BigDecimal priceL10;

	/*
	 * Related Properties
	 */

	@OneToOne
	@JoinColumn(name = "currency_id")
	private Currency currency;

	//

	public ProductMultiCurrency() {
	}

	

	public Long getProductId() {
		return productId;
	}



	public void setProductId(Long productId) {
		this.productId = productId;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getPriceL1() {
		return priceL1;
	}

	public void setPriceL1(BigDecimal priceL1) {
		this.priceL1 = priceL1;
	}

	public BigDecimal getPriceL2() {
		return priceL2;
	}

	public void setPriceL2(BigDecimal priceL2) {
		this.priceL2 = priceL2;
	}

	public BigDecimal getPriceL3() {
		return priceL3;
	}

	public void setPriceL3(BigDecimal priceL3) {
		this.priceL3 = priceL3;
	}

	public BigDecimal getPriceL4() {
		return priceL4;
	}

	public void setPriceL4(BigDecimal priceL4) {
		this.priceL4 = priceL4;
	}

	public BigDecimal getPriceL5() {
		return priceL5;
	}

	public void setPriceL5(BigDecimal priceL5) {
		this.priceL5 = priceL5;
	}

	public BigDecimal getPriceL6() {
		return priceL6;
	}

	public void setPriceL6(BigDecimal priceL6) {
		this.priceL6 = priceL6;
	}

	public BigDecimal getPriceL7() {
		return priceL7;
	}

	public void setPriceL7(BigDecimal priceL7) {
		this.priceL7 = priceL7;
	}

	public BigDecimal getPriceL8() {
		return priceL8;
	}

	public void setPriceL8(BigDecimal priceL8) {
		this.priceL8 = priceL8;
	}

	public BigDecimal getPriceL9() {
		return priceL9;
	}

	public void setPriceL9(BigDecimal priceL9) {
		this.priceL9 = priceL9;
	}

	public BigDecimal getPriceL10() {
		return priceL10;
	}

	public void setPriceL10(BigDecimal priceL10) {
		this.priceL10 = priceL10;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

}
