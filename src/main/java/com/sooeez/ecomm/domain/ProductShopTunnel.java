package com.sooeez.ecomm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_product_shop_tunnel")
public class ProductShopTunnel implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "product_id")
	private Long productId;
	
	@Column(name = "shop_id", nullable = false)
	private Long shopId;

	@Column(name = "tunnel_id", nullable = false)
	private Long tunnelId;

	/*
	 * Related Properties
	 */

	//

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public Long getTunnelId() {
		return tunnelId;
	}

	public void setTunnelId(Long tunnelId) {
		this.tunnelId = tunnelId;
	}

	
}
