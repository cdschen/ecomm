package com.sooeez.ecomm.dto.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InventoryProductDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String sku;
	private List<?> positions = new ArrayList<>();

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

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public List<?> getPositions() {
		return positions;
	}

	public void setPositions(List<?> positions) {
		this.positions = positions;
	}

}
