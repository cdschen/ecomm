package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "t_warehouse")
public class Warehouse implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "enable_position", nullable = false)
	private Boolean enablePosition;

	@Column(name = "address", nullable = false)
	private String address;

	@Column(name = "phone", nullable = false)
	private String phone;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "enabled", nullable = false)
	private Boolean enabled;

	/*
	 * Related Properties
	 */

	@JoinColumn(name = "warehouse_id")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<WarehousePosition> positions;

	/*
	 * @Transient Properties
	 */

	// 产品在一个仓库下的数量
	@Transient
	private Long total = 0l;

	// 仓库id集
	@Transient
	private Long[] warehouseIds;

	// 检查唯一
	@Transient
	private Boolean checkUnique;

	// 把所有库存条目的保质期都列在仓库的这个属性上
	@Transient
	private List<Date> expireDates;

	// 商品预购数量
	@Transient
	private Long orderedQty = 0l;

	/*
	 * Constructor
	 */

	public Warehouse() {
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

	public Boolean getEnablePosition() {
		return enablePosition;
	}

	public void setEnablePosition(Boolean enablePosition) {
		this.enablePosition = enablePosition;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<WarehousePosition> getPositions() {
		return positions;
	}

	public void setPositions(List<WarehousePosition> positions) {
		this.positions = positions;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long[] getWarehouseIds() {
		return warehouseIds;
	}

	public void setWarehouseIds(Long[] warehouseIds) {
		this.warehouseIds = warehouseIds;
	}

	public Boolean getCheckUnique() {
		return checkUnique;
	}

	public void setCheckUnique(Boolean checkUnique) {
		this.checkUnique = checkUnique;
	}

	public List<Date> getExpireDates() {
		return expireDates;
	}

	public void setExpireDates(List<Date> expireDates) {
		this.expireDates = expireDates;
	}

	public Long getOrderedQty() {
		return orderedQty;
	}

	public void setOrderedQty(Long orderedQty) {
		this.orderedQty = orderedQty;
	}

}
