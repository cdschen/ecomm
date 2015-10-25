package com.sooeez.ecomm.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.junit.experimental.theories.FromDataPoints;

@Entity
@Table(name = "t_process_step")
public class ProcessStep implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "process_id", insertable = false, updatable = false)
	private Long processId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "sequence", nullable = false)
	private Integer sequence;

	@Column(name = "type", nullable = false)
	private Integer type;

	/*
	 * Related Properties
	 */

//	@ManyToOne
//	@JoinColumn(name = "process_id")
//	private Process process;

	//

	public ProcessStep() {
	}

	// public Process getProcess() {
	// return process;
	// }
	//
	// public void setProcess(Process process) {
	// this.process = process;
	// }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
