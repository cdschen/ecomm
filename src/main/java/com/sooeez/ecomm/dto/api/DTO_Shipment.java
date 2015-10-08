package com.sooeez.ecomm.dto.api;

import java.io.Serializable;
import java.math.BigDecimal;

public class DTO_Shipment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	// 创建者
	private String creator;

	// 发货者
	private String executor;

	private String courier_name;

	private String ship_number;

	// 待取件, 已发出, 已签收, 派送异常, 作废
	private String ship_status;

	private Integer qty_total_item_shipped;

	private Integer total_weight;

	private BigDecimal shipfee_cost;

	private String create_time;

	private String last_update;

	private String pickup_time;

	private String signup_time;

	private String memo;

	private String sender_name;

	private String sender_phone;

	private String sender_email;

	private String sender_address;

	private String sender_post;

	private String receive_name;

	private String receive_phone;

	private String receive_email;

	private String receive_country;

	private String receive_province;

	private String receive_city;

	private String receive_address;

	private String receive_post;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public String getCourier_name() {
		return courier_name;
	}

	public void setCourier_name(String courier_name) {
		this.courier_name = courier_name;
	}

	public String getShip_number() {
		return ship_number;
	}

	public void setShip_number(String ship_number) {
		this.ship_number = ship_number;
	}

	public String getShip_status() {
		return ship_status;
	}

	public void setShip_status(String ship_status) {
		this.ship_status = ship_status;
	}

	public Integer getQty_total_item_shipped() {
		return qty_total_item_shipped;
	}

	public void setQty_total_item_shipped(Integer qty_total_item_shipped) {
		this.qty_total_item_shipped = qty_total_item_shipped;
	}

	public Integer getTotal_weight() {
		return total_weight;
	}

	public void setTotal_weight(Integer total_weight) {
		this.total_weight = total_weight;
	}

	public BigDecimal getShipfee_cost() {
		return shipfee_cost;
	}

	public void setShipfee_cost(BigDecimal shipfee_cost) {
		this.shipfee_cost = shipfee_cost;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getLast_update() {
		return last_update;
	}

	public void setLast_update(String last_update) {
		this.last_update = last_update;
	}

	public String getPickup_time() {
		return pickup_time;
	}

	public void setPickup_time(String pickup_time) {
		this.pickup_time = pickup_time;
	}

	public String getSignup_time() {
		return signup_time;
	}

	public void setSignup_time(String signup_time) {
		this.signup_time = signup_time;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getSender_name() {
		return sender_name;
	}

	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
	}

	public String getSender_phone() {
		return sender_phone;
	}

	public void setSender_phone(String sender_phone) {
		this.sender_phone = sender_phone;
	}

	public String getSender_email() {
		return sender_email;
	}

	public void setSender_email(String sender_email) {
		this.sender_email = sender_email;
	}

	public String getSender_address() {
		return sender_address;
	}

	public void setSender_address(String sender_address) {
		this.sender_address = sender_address;
	}

	public String getSender_post() {
		return sender_post;
	}

	public void setSender_post(String sender_post) {
		this.sender_post = sender_post;
	}

	public String getReceive_name() {
		return receive_name;
	}

	public void setReceive_name(String receive_name) {
		this.receive_name = receive_name;
	}

	public String getReceive_phone() {
		return receive_phone;
	}

	public void setReceive_phone(String receive_phone) {
		this.receive_phone = receive_phone;
	}

	public String getReceive_email() {
		return receive_email;
	}

	public void setReceive_email(String receive_email) {
		this.receive_email = receive_email;
	}

	public String getReceive_country() {
		return receive_country;
	}

	public void setReceive_country(String receive_country) {
		this.receive_country = receive_country;
	}

	public String getReceive_province() {
		return receive_province;
	}

	public void setReceive_province(String receive_province) {
		this.receive_province = receive_province;
	}

	public String getReceive_city() {
		return receive_city;
	}

	public void setReceive_city(String receive_city) {
		this.receive_city = receive_city;
	}

	public String getReceive_address() {
		return receive_address;
	}

	public void setReceive_address(String receive_address) {
		this.receive_address = receive_address;
	}

	public String getReceive_post() {
		return receive_post;
	}

	public void setReceive_post(String receive_post) {
		this.receive_post = receive_post;
	}
}
