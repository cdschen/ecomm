package com.sooeez.ecomm.dto;

import java.io.Serializable;
import java.util.List;

public class API_Response_DTO_Product implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/***
	 * APIDTO 基本属性
	 */
	/* ECOMM 错误码，成功返回 0 */
	private String code;
	/* API 返回信息 */
	private String message;
	/* 分页信息 */
	private API_DTO_Pagination page_context;
	
	/***
	 * APIDTO 附加信息
	 */
	/* 获取单个产品 */
	private API_DTO_Product product;
	
	/* 获取多个产品 */
	private List<API_DTO_Product> products;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public API_DTO_Pagination getPage_context() {
		return page_context;
	}

	public void setPage_context(API_DTO_Pagination page_context) {
		this.page_context = page_context;
	}

	public API_DTO_Product getProduct() {
		return product;
	}

	public void setProduct(API_DTO_Product product) {
		this.product = product;
	}

	public List<API_DTO_Product> getProducts() {
		return products;
	}

	public void setProducts(List<API_DTO_Product> products) {
		this.products = products;
	}

}
