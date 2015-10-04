package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.sooeez.ecomm.dto.InventoryProductDetailDTO;

@Entity
@Table(name = "t_product")
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "product_type", nullable = false)
	private Integer productType;

	@Column(name = "sku", nullable = false)
	private String sku;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "short_name", nullable = false)
	private String shortName;

	@Column(name = "price_l1", nullable = false)
	private BigDecimal priceL1;

	@Column(name = "price_l2", nullable = false)
	private BigDecimal priceL2;

	@Column(name = "price_l3", nullable = false)
	private BigDecimal priceL3;

	@Column(name = "price_l4", nullable = false)
	private BigDecimal priceL4;

	@Column(name = "price_l5", nullable = false)
	private BigDecimal priceL5;

	@Column(name = "price_l6", nullable = false)
	private BigDecimal priceL6;

	@Column(name = "price_l7", nullable = false)
	private BigDecimal priceL7;

	@Column(name = "price_l8", nullable = false)
	private BigDecimal priceL8;

	@Column(name = "price_l9", nullable = false)
	private BigDecimal priceL9;

	@Column(name = "price_l10", nullable = false)
	private BigDecimal priceL10;

	@Column(name = "short_description")
	private String shortDescription;

	@Lob
	@Column(name = "full_description")
	private String fullDescription;

	@Column(name = "weight")
	private Integer weight;

	@Column(name = "thumbnail_url")
	private String thumbnailUrl;

	@Column(name = "image1_url")
	private String image1Url;

	@Column(name = "image2_url")
	private String image2Url;

	@Column(name = "image3_url")
	private String image3Url;

	@Column(name = "image4_url")
	private String image4Url;

	@Column(name = "image5_url")
	private String image5Url;

	@Column(name = "image6_url")
	private String image6Url;

	@Column(name = "image7_url")
	private String image7Url;

	@Column(name = "image8_url")
	private String image8Url;

	@Column(name = "image9_url")
	private String image9Url;

	@Column(name = "deleted")
	private Boolean deleted;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	private Date createTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_update")
	private Date lastUpdate;

	/*
	 * Related Properties
	 */

	@OneToOne
	@JoinColumn(name = "brand_id")
	private Brand brand;

	@OneToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToOne
	@JoinColumn(name = "made_from_id")
	private MadeFrom madeFrom;

	@OneToOne
	@JoinColumn(name = "default_language_id")
	private Language language;

	@OneToOne
	@JoinColumn(name = "default_currency_id")
	private Currency currency;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private List<ProductMultiCurrency> multiCurrencies;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private List<ProductMultiLanguage> multiLanguages;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_product_id")
	private List<ProductMember> members;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "object_id")
	private List<ObjectProcess> processes;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private List<ProductShopTunnel> shopTunnels;

	@ManyToMany
	@JoinTable(name = "t_product_tag", joinColumns = { @JoinColumn(name = "product_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "tag_id", referencedColumnName = "id") })
	private List<Tag> tags;

	@Transient
	private Integer[] statusIds;

	// 产品在某一个仓库的库存量
	@Transient
	private Long total;

	// 产品所在仓库
	@Transient
	private List<Warehouse> warehouses = new ArrayList<>();

	// 某仓库产品上的库位列表
	@Transient
	private List<WarehousePosition> positions = new ArrayList<>();

	// 某仓库产品上有库位
	@Transient
	private Boolean existPosition = false;

	@Transient
	private List<InventoryProductDetailDTO> details = new ArrayList<>();

	// 某仓库中产品上的所有批次
	@Transient
	private List<InventoryBatch> batches = new ArrayList<>();

	//

	public Product() {
	}

	public List<InventoryBatch> getBatches() {
		return batches;
	}

	public void setBatches(List<InventoryBatch> batches) {
		this.batches = batches;
	}

	public List<InventoryProductDetailDTO> getDetails() {
		return details;
	}

	public void setDetails(List<InventoryProductDetailDTO> details) {
		this.details = details;
	}

	public Boolean getExistPosition() {
		if (positions.size() > 0) {
			this.existPosition = true;
		} else {
			this.existPosition = false;
		}
		return existPosition;
	}

	public void setExistPosition(Boolean existPosition) {
		this.existPosition = existPosition;
	}

	public List<WarehousePosition> getPositions() {
		return positions;
	}

	public void setPositions(List<WarehousePosition> positions) {
		this.positions = positions;
	}

	public List<Warehouse> getWarehouses() {
		return warehouses;
	}

	public void setWarehouses(List<Warehouse> warehouses) {
		this.warehouses = warehouses;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Integer[] getStatusIds() {
		return statusIds;
	}

	public void setStatusIds(Integer[] statusIds) {
		this.statusIds = statusIds;
	}

	public List<ObjectProcess> getProcesses() {
		return processes;
	}

	public void setProcesses(List<ObjectProcess> processes) {
		this.processes = processes;
	}

	public List<ProductMember> getMembers() {
		return members;
	}

	public void setMembers(List<ProductMember> members) {
		this.members = members;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<ProductMultiCurrency> getMultiCurrencies() {
		return multiCurrencies;
	}

	public void setMultiCurrencies(List<ProductMultiCurrency> multiCurrencies) {
		this.multiCurrencies = multiCurrencies;
	}

	public List<ProductMultiLanguage> getMultiLanguages() {
		return multiLanguages;
	}

	public void setMultiLanguages(List<ProductMultiLanguage> multiLanguages) {
		this.multiLanguages = multiLanguages;
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

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getImage1Url() {
		return image1Url;
	}

	public void setImage1Url(String image1Url) {
		this.image1Url = image1Url;
	}

	public String getImage2Url() {
		return image2Url;
	}

	public void setImage2Url(String image2Url) {
		this.image2Url = image2Url;
	}

	public String getImage3Url() {
		return image3Url;
	}

	public void setImage3Url(String image3Url) {
		this.image3Url = image3Url;
	}

	public String getImage4Url() {
		return image4Url;
	}

	public void setImage4Url(String image4Url) {
		this.image4Url = image4Url;
	}

	public String getImage5Url() {
		return image5Url;
	}

	public void setImage5Url(String image5Url) {
		this.image5Url = image5Url;
	}

	public String getImage6Url() {
		return image6Url;
	}

	public void setImage6Url(String image6Url) {
		this.image6Url = image6Url;
	}

	public String getImage7Url() {
		return image7Url;
	}

	public void setImage7Url(String image7Url) {
		this.image7Url = image7Url;
	}

	public String getImage8Url() {
		return image8Url;
	}

	public void setImage8Url(String image8Url) {
		this.image8Url = image8Url;
	}

	public String getImage9Url() {
		return image9Url;
	}

	public void setImage9Url(String image9Url) {
		this.image9Url = image9Url;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public MadeFrom getMadeFrom() {
		return madeFrom;
	}

	public void setMadeFrom(MadeFrom madeFrom) {
		this.madeFrom = madeFrom;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public BigDecimal getPriceL1() {
		return priceL1;
	}

	public void setPriceL1(BigDecimal priceL1) {
		this.priceL1 = priceL1;
	}

	public BigDecimal getPriceL2() {
		return priceL2;
	}

	public void setPriceL2(BigDecimal priceL2) {
		this.priceL2 = priceL2;
	}

	public BigDecimal getPriceL3() {
		return priceL3;
	}

	public void setPriceL3(BigDecimal priceL3) {
		this.priceL3 = priceL3;
	}

	public BigDecimal getPriceL4() {
		return priceL4;
	}

	public void setPriceL4(BigDecimal priceL4) {
		this.priceL4 = priceL4;
	}

	public BigDecimal getPriceL5() {
		return priceL5;
	}

	public void setPriceL5(BigDecimal priceL5) {
		this.priceL5 = priceL5;
	}

	public BigDecimal getPriceL6() {
		return priceL6;
	}

	public void setPriceL6(BigDecimal priceL6) {
		this.priceL6 = priceL6;
	}

	public BigDecimal getPriceL7() {
		return priceL7;
	}

	public void setPriceL7(BigDecimal priceL7) {
		this.priceL7 = priceL7;
	}

	public BigDecimal getPriceL8() {
		return priceL8;
	}

	public void setPriceL8(BigDecimal priceL8) {
		this.priceL8 = priceL8;
	}

	public BigDecimal getPriceL9() {
		return priceL9;
	}

	public void setPriceL9(BigDecimal priceL9) {
		this.priceL9 = priceL9;
	}

	public BigDecimal getPriceL10() {
		return priceL10;
	}

	public void setPriceL10(BigDecimal priceL10) {
		this.priceL10 = priceL10;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

	public List<ProductShopTunnel> getShopTunnels() {
		return shopTunnels;
	}

	public void setShopTunnels(List<ProductShopTunnel> shopTunnels) {
		this.shopTunnels = shopTunnels;
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

}
