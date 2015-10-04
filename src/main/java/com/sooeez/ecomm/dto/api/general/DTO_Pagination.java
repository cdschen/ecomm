package com.sooeez.ecomm.dto.api.general;

import java.io.Serializable;
import java.math.BigInteger;

public class DTO_Pagination implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/***
	 * Response DTO 基本属性
	 */
	private Long page;
	private Long per_page;
	private BigInteger total_number;
	private Boolean has_more_page;
	
	public Long getPage() {
		return page;
	}
	public void setPage(Long page) {
		this.page = page;
	}
	public Long getPer_page() {
		return per_page;
	}
	public void setPer_page(Long per_page) {
		this.per_page = per_page;
	}
	public BigInteger getTotal_number() {
		return total_number;
	}
	public void setTotal_number(BigInteger total_number) {
		this.total_number = total_number;
	}
	public Boolean getHas_more_page() {
		return has_more_page;
	}
	public void setHas_more_page(Boolean has_more_page) {
		this.has_more_page = has_more_page;
	}

}
