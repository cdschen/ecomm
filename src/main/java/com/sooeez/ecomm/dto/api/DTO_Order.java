package com.sooeez.ecomm.dto.api;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.sooeez.ecomm.dto.api.general.DTO_Process_Status;

public class DTO_Order implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long shop_id;
	
	private String order_sn;
	
	private Integer qty_total_item_ordered;
	
	private Integer qty_total_item_shipped;
	
	private BigDecimal grand_total;
	
	private BigDecimal subtotal;
	
	private BigDecimal shipping_fee;
	
	private BigDecimal tax;
	
	private BigDecimal total_invoiced;
	
	private BigDecimal total_paid;
	
	private BigDecimal total_refunded;
	
	private String currency;
	
	private Integer weight;
	
	private String memo;
	
	private String delivery_method;
	
	private String sender_name;
	
	private String sender_address;
	
	private String sender_phone;
	
	private String sender_email;
	
	private String sender_post;
	
	private String receive_name;
	
	private String receive_phone;
	
	private String receive_email;
	
	private String receive_country;
	
	private String receive_province;
	
	private String receive_city;
	
	private String receive_address;
	
	private String receive_post;
	
	private List<DTO_Process_Status> processing_status;
	
	private String created_time;
	
	private String updated_time;
	
	// 关联的 DTO
	private DTO_Customer customer;
	private List<DTO_OrderItem> order_items;
	private List<DTO_Shipment> shipments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getShop_id() {
		return shop_id;
	}

	public void setShop_id(Long shop_id) {
		this.shop_id = shop_id;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public Integer getQty_total_item_ordered() {
		return qty_total_item_ordered;
	}

	public void setQty_total_item_ordered(Integer qty_total_item_ordered) {
		this.qty_total_item_ordered = qty_total_item_ordered;
	}

	public Integer getQty_total_item_shipped() {
		return qty_total_item_shipped;
	}

	public void setQty_total_item_shipped(Integer qty_total_item_shipped) {
		this.qty_total_item_shipped = qty_total_item_shipped;
	}

	public BigDecimal getGrand_total() {
		return grand_total;
	}

	public void setGrand_total(BigDecimal grand_total) {
		this.grand_total = grand_total;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getShipping_fee() {
		return shipping_fee;
	}

	public void setShipping_fee(BigDecimal shipping_fee) {
		this.shipping_fee = shipping_fee;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public BigDecimal getTotal_invoiced() {
		return total_invoiced;
	}

	public void setTotal_invoiced(BigDecimal total_invoiced) {
		this.total_invoiced = total_invoiced;
	}

	public BigDecimal getTotal_paid() {
		return total_paid;
	}

	public void setTotal_paid(BigDecimal total_paid) {
		this.total_paid = total_paid;
	}

	public BigDecimal getTotal_refunded() {
		return total_refunded;
	}

	public void setTotal_refunded(BigDecimal total_refunded) {
		this.total_refunded = total_refunded;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getDelivery_method() {
		return delivery_method;
	}

	public void setDelivery_method(String delivery_method) {
		this.delivery_method = delivery_method;
	}

	public String getSender_name() {
		return sender_name;
	}

	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
	}

	public String getSender_address() {
		return sender_address;
	}

	public void setSender_address(String sender_address) {
		this.sender_address = sender_address;
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

	public List<DTO_Process_Status> getProcessing_status() {
		return processing_status;
	}

	public void setProcessing_status(List<DTO_Process_Status> processing_status) {
		this.processing_status = processing_status;
	}

	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public String getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}

	public DTO_Customer getCustomer() {
		return customer;
	}

	public void setCustomer(DTO_Customer customer) {
		this.customer = customer;
	}

	public List<DTO_OrderItem> getOrder_items() {
		return order_items;
	}

	public void setOrder_items(List<DTO_OrderItem> order_items) {
		this.order_items = order_items;
	}

	public List<DTO_Shipment> getShipments() {
		return shipments;
	}

	public void setShipments(List<DTO_Shipment> shipments) {
		this.shipments = shipments;
	}
	

}
