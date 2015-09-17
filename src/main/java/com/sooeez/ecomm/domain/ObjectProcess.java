package com.sooeez.ecomm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_object_process")
public class ObjectProcess implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "object_id")
	private Long objectId;

	@Column(name = "object_type", nullable = false)
	private Integer objectType;

	@Column(name = "step_id", nullable = false, insertable = false, updatable = false)
	private Long stepId;

	/*
	 * Related Properties
	 */

	@OneToOne
	@JoinColumn(name = "process_id")
	private Process process;

	@OneToOne
	@JoinColumn(name = "step_id")
	private ProcessStep step;

	//

	public ObjectProcess() {
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public ProcessStep getStep() {
		return step;
	}

	public void setStep(ProcessStep step) {
		this.step = step;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Integer getObjectType() {
		return objectType;
	}

	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}

	public Long getStepId() {
		return stepId;
	}

	public void setStepId(Long stepId) {
		this.stepId = stepId;
	}

}
