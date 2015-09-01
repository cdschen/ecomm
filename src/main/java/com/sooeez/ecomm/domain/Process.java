package com.sooeez.ecomm.domain;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.persistence.Transient;

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

	@Column(name = "default_step_id")
	private Long defaultStepId;

	@Column(name = "hide_when_complete", nullable = false)
	private boolean hideWhenComplete;

	@Column(name = "deleted", nullable = false)
	private boolean deleted;

	/*
	 * Related Properties
	 */

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "process_id")
	private List<ProcessStep> steps;

	@Transient
	private String defaultStepName;

	//

	public Process() {

	}

	public String getDefaultStepName() {
		return defaultStepName;
	}

	public void setDefaultStepName(String defaultStepName) {
		this.defaultStepName = defaultStepName;
	}

	public List<ProcessStep> getSteps() {
		return steps;
	}

	public void setSteps(List<ProcessStep> steps) {
		this.steps = steps;
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

	public Integer getObjectType() {
		return objectType;
	}

	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}

	public Long getDefaultStepId() {
		return defaultStepId;
	}

	public void setDefaultStepId(Long defaultStepId) {
		this.defaultStepId = defaultStepId;
	}

	public boolean isHideWhenComplete() {
		return hideWhenComplete;
	}

	public void setHideWhenComplete(boolean hideWhenComplete) {
		this.hideWhenComplete = hideWhenComplete;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
