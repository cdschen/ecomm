package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "t_warehouse_position")
public class WarehousePosition implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	/*
	 * Related Properties
	 */

	/*
	 * @Transient Properties
	 */

	@Transient
	private Long total;

	@Transient
	private List<InventoryBatch> batches = new ArrayList<>();

	@Transient
	private Long stock = 0L;

	/*
	 * Constructor
	 */

	public WarehousePosition() {
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

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<InventoryBatch> getBatches() {
		return batches;
	}

	public void setBatches(List<InventoryBatch> batches) {
		this.batches = batches;
	}

	public Long getStock() {
		return stock;
	}

	public void setStock(Long stock) {
		this.stock = stock;
	}

}
