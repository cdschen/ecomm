package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "t_shop_tunnel")
public class ShopTunnel implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "shop_id", insertable = false, updatable = false)
	private Long shopId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "type", nullable = false)
	private Integer type;

	@Column(name = "behavior", nullable = false)
	private Integer behavior;

	@Column(name = "default_option", nullable = false)
	private Boolean defaultOption;

	/*
	 * Related Properties
	 */

	@ManyToMany
	@JoinTable(name = "t_tunnel_supplier", joinColumns = { @JoinColumn(name = "tunnel_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "supplier_id", referencedColumnName = "id") })
	private List<Supplier> suppliers;

	@ManyToMany
	@JoinTable(name = "t_tunnel_warehouse", joinColumns = { @JoinColumn(name = "tunnel_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "warehouse_id", referencedColumnName = "id") })
	private List<Warehouse> warehouses;

	//

	public ShopTunnel() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getBehavior() {
		return behavior;
	}

	public void setBehavior(Integer behavior) {
		this.behavior = behavior;
	}

	public Boolean getDefaultOption() {
		return defaultOption;
	}

	public void setDefaultOption(Boolean defaultOption) {
		this.defaultOption = defaultOption;
	}

	public List<Supplier> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
	}

	public List<Warehouse> getWarehouses() {
		return warehouses;
	}

	public void setWarehouses(List<Warehouse> warehouses) {
		this.warehouses = warehouses;
	}

}
