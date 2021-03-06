package com.sooeez.ecomm.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "t_user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "enabled", nullable = false)
	private Boolean enabled;

	@Column(name = "managed_shops")
	private String managedShops;

	@Column(name = "managed_warehouses")
	private String managedWarehouses;

	/*
	 * Related Properties
	 */

	@ManyToMany
	@JoinTable(name = "t_user_role", joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "id") })
	private List<Role> roles = new ArrayList<>();

	/*
	 * @Transient Properties
	 */

	// 检查唯一
	@Transient
	private Boolean checkUnique;

	/*
	 * Constructor
	 */

	public User() {
	}

	/*
	 * Functions
	 */

	//@JsonIgnore
	public List<Role> getRoles() {
		return roles;
	}

	//@JsonProperty
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getManagedShops() {
		return managedShops;
	}

	public void setManagedShops(String managedShops) {
		this.managedShops = managedShops;
	}

	public String getManagedWarehouses() {
		return managedWarehouses;
	}

	public void setManagedWarehouses(String managedWarehouses) {
		this.managedWarehouses = managedWarehouses;
	}

	public Boolean getCheckUnique() {
		return checkUnique;
	}

	public void setCheckUnique(Boolean checkUnique) {
		this.checkUnique = checkUnique;
	}

}
