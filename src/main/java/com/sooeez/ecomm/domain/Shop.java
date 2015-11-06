package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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

	@Column(name = "deploy_process_step_id", insertable = false, updatable = false)
	private Long deployProcessStepId;

	@Column(name = "complete_process_step_id", insertable = false, updatable = false)
	private Long completeProcessStepId;

	@Column(name = "error_process_step_id", insertable = false, updatable = false)
	private Long errorProcessStepId;

	@Column(name = "token", nullable = false)
	private String token;

	@Column(name = "default_tunnel_id", insertable = false, updatable = false)
	private String defaultTunnelId;

	@Column(name = "enabled", nullable = false)
	private Boolean enabled;

	/*
	 * Related Properties
	 */

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "language_id")
	private Language language;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "currency_id")
	private Currency currency;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "default_tunnel_id")
	private ShopTunnel defaultTunnel;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "init_process_step_id")
	private ProcessStep initStep;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "deploy_process_step_id")
	private ProcessStep deployStep;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "complete_process_step_id")
	private ProcessStep completeStep;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "error_process_step_id")
	private ProcessStep errorStep;

	@JoinColumn(name = "shop_id")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ShopTunnel> tunnels;

	/*
	 * @Transient Properties
	 */

	// 商店id集合
	@Transient
	private Long[] shopIds;

	// 检查唯一
	@Transient
	private Boolean checkUnique;

	/*
	 * Constructor
	 */

	public Shop() {
	}

	/*
	 * Functions
	 */

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

	public Long getDeployProcessStepId() {
		return deployProcessStepId;
	}

	public void setDeployProcessStepId(Long deployProcessStepId) {
		this.deployProcessStepId = deployProcessStepId;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
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

	public ProcessStep getDeployStep() {
		return deployStep;
	}

	public void setDeployStep(ProcessStep deployStep) {
		this.deployStep = deployStep;
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

	public ShopTunnel getDefaultTunnel() {
		return defaultTunnel;
	}

	public void setDefaultTunnel(ShopTunnel defaultTunnel) {
		this.defaultTunnel = defaultTunnel;
	}

	public Long[] getShopIds() {
		return shopIds;
	}

	public void setShopIds(Long[] shopIds) {
		this.shopIds = shopIds;
	}

	public Boolean getCheckUnique() {
		return checkUnique;
	}

	public void setCheckUnique(Boolean checkUnique) {
		this.checkUnique = checkUnique;
	}

}