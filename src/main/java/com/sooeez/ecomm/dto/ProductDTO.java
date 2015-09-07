package com.sooeez.ecomm.dto;

import java.io.Serializable;

import org.springframework.web.bind.annotation.RestController;

public class ProductDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String sku;
	private String name;

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

}
