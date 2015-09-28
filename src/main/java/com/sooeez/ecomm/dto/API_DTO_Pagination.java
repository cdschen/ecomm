package com.sooeez.ecomm.dto;

import java.io.Serializable;

public class API_DTO_Pagination implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/***
	 * Response DTO 基本属性
	 */
	private Integer page;
	private Integer per_page;
	private Integer total_number;
	private Boolean has_more_page;
	
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getPer_page() {
		return per_page;
	}
	public void setPer_page(Integer per_page) {
		this.per_page = per_page;
	}
	public Integer getTotal_number() {
		return total_number;
	}
	public void setTotal_number(Integer total_number) {
		this.total_number = total_number;
	}
	public Boolean getHas_more_page() {
		return has_more_page;
	}
	public void setHas_more_page(Boolean has_more_page) {
		this.has_more_page = has_more_page;
	}

}
