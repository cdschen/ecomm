package com.sooeez.ecomm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_warehouse_position")
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

	//
	
	public WarehousePosition() {
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

}
