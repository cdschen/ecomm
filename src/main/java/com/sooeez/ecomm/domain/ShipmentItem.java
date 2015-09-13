package com.sooeez.ecomm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_shipment_item")
public class ShipmentItem implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "shipment_id")
	private Long shipmentId;
	
	@Column(name = "qty_shipped")
	private Integer qtyShipped;
	
	/*
	 * Related Properties
	 */
	@OneToOne
	@JoinColumn(name = "order_item_id")
	private OrderItem orderItem;

	//

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(Long shipmentId) {
		this.shipmentId = shipmentId;
	}

	public Integer getQtyShipped() {
		return qtyShipped;
	}

	public void setQtyShipped(Integer qtyShipped) {
		this.qtyShipped = qtyShipped;
	}

	public OrderItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}
	
}
