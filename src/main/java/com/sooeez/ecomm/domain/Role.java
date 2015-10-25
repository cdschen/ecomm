package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "t_role")
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "desc")
	private String desc;

	@Column(name = "module")
	private String module;

	@Column(name = "module_code")
	private String moduleCode;

	@Column(name = "module_sequence")
	private Integer moduleSequence;

	/*
	 * Related Properties
	 */

	/*
	 * @ManyToMany
	 * 
	 * @JoinTable(name = "t_role_authority", joinColumns = { @JoinColumn(name =
	 * "role_id", referencedColumnName = "id") }, inverseJoinColumns = {
	 * 
	 * @JoinColumn(name = "authority_id", referencedColumnName = "id") })
	 * private Set<Authority> authorities = new HashSet<>();
	 */

	//

	public Role() {
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public Integer getModuleSequence() {
		return moduleSequence;
	}

	public void setModuleSequence(Integer moduleSequence) {
		this.moduleSequence = moduleSequence;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

}
