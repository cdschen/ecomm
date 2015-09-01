package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "t_shop")
public class Shop implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "type", nullable = false)
	private int type;

	@Column(name = "status", nullable = false)
	private int status;

//	@Column(name = "admin_id", nullable = false, insertable = false, updatable = false)
//	private long adminId;

	@Column(name = "api_call_limit", nullable = false)
	private int apiCallLimit;
	
//	@Column(name = "language_id", nullable = false, insertable = false, updatable = false)
//	private long languageId;
//	
//	@Column(name = "currencyId", nullable = false, insertable = false, updatable = false)
//	private long currency_id;

	@Column(name = "price_level", nullable = false)
	private int priceLevel;

	/*
	 * Related Properties
	 */

	@OneToOne
	@JoinColumn(name = "admin_id")
	private User user;
	
	@OneToOne
	@JoinColumn(name = "language_id")
	private Language language;

	@OneToOne
	@JoinColumn(name = "currency_id")
	private Currency currency;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	private List<ShopTunnel> tunnels; 

	//

	public Shop() {
	}

	public List<ShopTunnel> getTunnels() {
		return tunnels;
	}

	public void setTunnels(List<ShopTunnel> tunnels) {
		this.tunnels = tunnels;
	}

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

//	public long getAdminId() {
//		return adminId;
//	}
//
//	public void setAdminId(long adminId) {
//		this.adminId = adminId;
//	}

	public int getApiCallLimit() {
		return apiCallLimit;
	}

	public void setApiCallLimit(int apiCallLimit) {
		this.apiCallLimit = apiCallLimit;
	}

	public int getPriceLevel() {
		return priceLevel;
	}

	public void setPriceLevel(int priceLevel) {
		this.priceLevel = priceLevel;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

//	public long getLanguageId() {
//		return languageId;
//	}
//
//	public void setLanguageId(long languageId) {
//		this.languageId = languageId;
//	}
//
//	public long getCurrency_id() {
//		return currency_id;
//	}
//
//	public void setCurrency_id(long currency_id) {
//		this.currency_id = currency_id;
//	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}
	
	

}