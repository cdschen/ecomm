package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "t_supplier_product_code_map")
public class SupplierProductCodeMap implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	//	#供应商编号
	@Column(name = "supplier_id", nullable = false, insertable = false, updatable = false)
	private Long supplierId;

	//	#产品编号
	@Column(name = "product_id", nullable = false, insertable = false, updatable = false)
	private Long productId;

	//	#供应商产品编码
	@Column(name = "supplier_product_code", nullable = false)
	private String  supplierProductCode;

	//	#默认采购单价
	@Column(name = "default_purchase_price", nullable = false)
	private BigDecimal defaultPurchasePrice;

	/*
	 * Related Properties
	 */

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "product_id")
	private Product product;

	//


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getSupplierProductCode() {
		return supplierProductCode;
	}

	public void setSupplierProductCode(String supplierProductCode) {
		this.supplierProductCode = supplierProductCode;
	}

	public BigDecimal getDefaultPurchasePrice() {
		return defaultPurchasePrice;
	}

	public void setDefaultPurchasePrice(BigDecimal defaultPurchasePrice) {
		this.defaultPurchasePrice = defaultPurchasePrice;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}


}
