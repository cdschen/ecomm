package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "t_process")
public class Process implements Serializable {

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

	@Column(name = "object_type", nullable = false)
	private Integer objectType;

	@Column(name = "auto_apply", nullable = false)
	private Boolean autoApply;

	@Column(name = "default_step_id", insertable = false, updatable = false)
	private Long defaultStepId;

	@Column(name = "hide_when_complete", nullable = false)
	private Boolean hideWhenComplete;

	@Column(name = "enabled", nullable = false)
	private Boolean enabled;

	/*
	 * Related Properties
	 */

	@JoinColumn(name = "process_id")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ProcessStep> steps = new ArrayList<>();

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "default_step_id")
	private ProcessStep defaultStep;

	/*
	 * @Transient Properties
	 */

	@Transient
	private String defaultStepName;

	// 检查唯一
	@Transient
	private Boolean checkUnique;

	/*
	 * Constructor
	 */

	public Process() {
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

	public Integer getObjectType() {
		return objectType;
	}

	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}

	public Boolean getAutoApply() {
		return autoApply;
	}

	public void setAutoApply(Boolean autoApply) {
		this.autoApply = autoApply;
	}

	public Long getDefaultStepId() {
		return defaultStepId;
	}

	public void setDefaultStepId(Long defaultStepId) {
		this.defaultStepId = defaultStepId;
	}

	public Boolean getHideWhenComplete() {
		return hideWhenComplete;
	}

	public void setHideWhenComplete(Boolean hideWhenComplete) {
		this.hideWhenComplete = hideWhenComplete;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<ProcessStep> getSteps() {
		return steps;
	}

	public void setSteps(List<ProcessStep> steps) {
		this.steps = steps;
	}

	public String getDefaultStepName() {
		return defaultStepName;
	}

	public void setDefaultStepName(String defaultStepName) {
		this.defaultStepName = defaultStepName;
	}

	public Boolean getCheckUnique() {
		return checkUnique;
	}

	public void setCheckUnique(Boolean checkUnique) {
		this.checkUnique = checkUnique;
	}

	public ProcessStep getDefaultStep() {
		return defaultStep;
	}

	public void setDefaultStep(ProcessStep defaultStep) {
		this.defaultStep = defaultStep;
	}

}
