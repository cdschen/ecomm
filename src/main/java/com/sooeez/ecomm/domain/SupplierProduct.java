package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "t_supplier_product")
public class SupplierProduct implements Serializable {

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
	@Column(name = "product_id", insertable = false, updatable = false)
	private Long productId;

	//	#创建者编号
	@Column(name = "creator_id", nullable = false, insertable = false, updatable = false)
	private Long creatorId;

	//	#供应商产品编码
	@Column(name = "supplier_product_code")
	private String  supplierProductCode;

	//	#条码
	@Column(name = "supplier_product_barcode")
	private String  supplierProductBarcode;

	//	#供应商产品名称
	@Column(name = "supplier_product_name")
	private String  supplierProductName;

	//	#默认采购单价
	@Column(name = "default_purchase_price")
	private BigDecimal defaultPurchasePrice;

	/* 收货单在系统创建时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", nullable = false)
	private Date createTime;

	/* 收货单最近更新时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_update", nullable = false)
	private Date lastUpdate;

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

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "creator_id")
	private User creator;

	/*
	 * @Transient Properties
	 */

	@Transient
	private Long querySupplierId;
	
	@Transient
	private Long queryCreatorId;

	@Transient
	private String queryProductSku;

	@Transient
	private String queryProductBarcode;
	
	@Transient
	private String querySupplierProductCode;
	
	@Transient
	private String querySupplierProductName;
	
	/* 供应商产品创建起始日期 */
	@Transient
	private String queryCreateTimeStart;

	/* 供应商产品创建结束日期 */
	@Transient
	private String queryCreateTimeEnd;
	
	/* 供应商产品最后更新起始日期 */
	@Transient
	private String queryLastUpdateStart;

	/* 供应商产品最后更新结束日期 */
	@Transient
	private String queryLastUpdateEnd;

	/* 采购单里，采购详情得模糊查询 */
	@Transient
	private String queryPurchaseOrderItemFuzzySearchParam;
	
	
	
	/*
	 * 采购单：采购［供应商新品］时，临时用到的毫秒，匹配［供应商产品］和［采购单详情］是否关联
	 */
	@Transient
	private Long currentTimeMillis;


	// 检查唯一
	@Transient
	private Boolean checkUnique;
	

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

	public String getSupplierProductBarcode() {
		return supplierProductBarcode;
	}

	public void setSupplierProductBarcode(String supplierProductBarcode) {
		this.supplierProductBarcode = supplierProductBarcode;
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

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public String getSupplierProductName() {
		return supplierProductName;
	}

	public void setSupplierProductName(String supplierProductName) {
		this.supplierProductName = supplierProductName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Long getQuerySupplierId() {
		return querySupplierId;
	}

	public void setQuerySupplierId(Long querySupplierId) {
		this.querySupplierId = querySupplierId;
	}

	public Long getQueryCreatorId() {
		return queryCreatorId;
	}

	public void setQueryCreatorId(Long queryCreatorId) {
		this.queryCreatorId = queryCreatorId;
	}

	public String getQueryProductSku() {
		return queryProductSku;
	}

	public void setQueryProductSku(String queryProductSku) {
		this.queryProductSku = queryProductSku;
	}

	public String getQueryProductBarcode() {
		return queryProductBarcode;
	}

	public void setQueryProductBarcode(String queryProductBarcode) {
		this.queryProductBarcode = queryProductBarcode;
	}

	public String getQuerySupplierProductCode() {
		return querySupplierProductCode;
	}

	public void setQuerySupplierProductCode(String querySupplierProductCode) {
		this.querySupplierProductCode = querySupplierProductCode;
	}

	public String getQuerySupplierProductName() {
		return querySupplierProductName;
	}

	public void setQuerySupplierProductName(String querySupplierProductName) {
		this.querySupplierProductName = querySupplierProductName;
	}

	public String getQueryCreateTimeStart() {
		return queryCreateTimeStart;
	}

	public void setQueryCreateTimeStart(String queryCreateTimeStart) {
		this.queryCreateTimeStart = queryCreateTimeStart;
	}

	public String getQueryCreateTimeEnd() {
		return queryCreateTimeEnd;
	}

	public void setQueryCreateTimeEnd(String queryCreateTimeEnd) {
		this.queryCreateTimeEnd = queryCreateTimeEnd;
	}

	public String getQueryLastUpdateStart() {
		return queryLastUpdateStart;
	}

	public void setQueryLastUpdateStart(String queryLastUpdateStart) {
		this.queryLastUpdateStart = queryLastUpdateStart;
	}

	public String getQueryLastUpdateEnd() {
		return queryLastUpdateEnd;
	}

	public void setQueryLastUpdateEnd(String queryLastUpdateEnd) {
		this.queryLastUpdateEnd = queryLastUpdateEnd;
	}

	public String getQueryPurchaseOrderItemFuzzySearchParam() {
		return queryPurchaseOrderItemFuzzySearchParam;
	}

	public void setQueryPurchaseOrderItemFuzzySearchParam(String queryPurchaseOrderItemFuzzySearchParam) {
		this.queryPurchaseOrderItemFuzzySearchParam = queryPurchaseOrderItemFuzzySearchParam;
	}

	public Long getCurrentTimeMillis() {
		return currentTimeMillis;
	}

	public void setCurrentTimeMillis(Long currentTimeMillis) {
		this.currentTimeMillis = currentTimeMillis;
	}

	public Boolean getCheckUnique() {
		return checkUnique;
	}

	public void setCheckUnique(Boolean checkUnique) {
		this.checkUnique = checkUnique;
	}


}
