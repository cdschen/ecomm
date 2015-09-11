package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;

@Entity
@Table(name = "t_shop")
public class Shop implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "type", nullable = false)
	private Integer type;

	@Column(name = "status", nullable = false)
	private Integer status;

	@Column(name = "admin_id", nullable = false, insertable = false, updatable = false)
	private Long adminId;

	@Column(name = "api_call_limit", nullable = false)
	private Integer apiCallLimit;

	@Column(name = "language_id", nullable = false, insertable = false, updatable = false)
	private Long languageId;

	@Column(name = "currency_id", nullable = false, insertable = false, updatable = false)
	private Long currencyId;

	@Column(name = "price_level", nullable = false)
	private Integer priceLevel;

	@Column(name = "init_process_step_id", insertable = false, updatable = false)
	private Long initProcessStepId;
	
	@Column(name = "init_picking_process_step_id", insertable = false, updatable = false)
	private Long initPickingProcessStepId;

	@Column(name = "complete_process_step_id", insertable = false, updatable = false)
	private Long completeProcessStepId;

	@Column(name = "error_process_step_id", insertable = false, updatable = false)
	private Long errorProcessStepId;

	@Column(name = "token")
	private String token;

	@Column(name = "deleted")
	private Boolean deleted;

	/*
	 * Related Properties
	 */

	@OneToOne
	@JoinColumn(name = "admin_id")
	private User user;

	@OneToOne
	@JoinColumn(name = "language_id")
	private Language language;

	@OneToOne
	@JoinColumn(name = "currency_id")
	private Currency currency;

	@OneToOne
	@JoinColumn(name = "init_process_step_id")
	private ProcessStep initStep;
	
	@OneToOne
	@JoinColumn(name = "init_picking_process_step_id")
	private ProcessStep initPickingStep;

	@OneToOne
	@JoinColumn(name = "complete_process_step_id")
	private ProcessStep completeStep;

	@OneToOne
	@JoinColumn(name = "error_process_step_id")
	private ProcessStep errorStep;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	private List<ShopTunnel> tunnels;

	//

	public Shop() {
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public Integer getApiCallLimit() {
		return apiCallLimit;
	}

	public void setApiCallLimit(Integer apiCallLimit) {
		this.apiCallLimit = apiCallLimit;
	}

	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public Integer getPriceLevel() {
		return priceLevel;
	}

	public void setPriceLevel(Integer priceLevel) {
		this.priceLevel = priceLevel;
	}

	public Long getInitProcessStepId() {
		return initProcessStepId;
	}

	public void setInitProcessStepId(Long initProcessStepId) {
		this.initProcessStepId = initProcessStepId;
	}

	public Long getCompleteProcessStepId() {
		return completeProcessStepId;
	}

	public void setCompleteProcessStepId(Long completeProcessStepId) {
		this.completeProcessStepId = completeProcessStepId;
	}

	public Long getErrorProcessStepId() {
		return errorProcessStepId;
	}

	public void setErrorProcessStepId(Long errorProcessStepId) {
		this.errorProcessStepId = errorProcessStepId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public ProcessStep getInitStep() {
		return initStep;
	}

	public void setInitStep(ProcessStep initStep) {
		this.initStep = initStep;
	}

	public ProcessStep getCompleteStep() {
		return completeStep;
	}

	public void setCompleteStep(ProcessStep completeStep) {
		this.completeStep = completeStep;
	}

	public ProcessStep getErrorStep() {
		return errorStep;
	}

	public void setErrorStep(ProcessStep errorStep) {
		this.errorStep = errorStep;
	}

	public List<ShopTunnel> getTunnels() {
		return tunnels;
	}

	public void setTunnels(List<ShopTunnel> tunnels) {
		this.tunnels = tunnels;
	}

}