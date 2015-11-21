package com.sooeez.ecomm.dto;

import java.io.Serializable;
import java.util.List;

public class PageDTO<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<T> content;
	private Boolean first;
	private Boolean last;
	private Integer number;
	private Integer size;
	private Long totalElements;
	private Integer totalPages;

	public List<?> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public Boolean getFirst() {
		return first;
	}

	public void setFirst(Boolean first) {
		this.first = first;
	}

	public Boolean getLast() {
		return last;
	}

	public void setLast(Boolean last) {
		this.last = last;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

}
