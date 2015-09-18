package com.sooeez.ecomm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "t_product_multilanguage")
public class ProductMultiLanguage implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "language_id", nullable = false, insertable = false, updatable = false)
	private Long languageId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "short_name")
	private String shortName;

	@Column(name = "short_description", length = 1000)
	private String shortDescription;

	@Lob
	@Column(name = "full_description")
	private String fullDescription;

	/*
	 * Related Properties
	 */

	@OneToOne
	@JoinColumn(name = "language_id")
	private Language language;

	//

	public ProductMultiLanguage() {
	}

	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
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
}
