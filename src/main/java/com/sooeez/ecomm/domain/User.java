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
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "t_user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Table Properties
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "username", unique = true, nullable = false)
	// @Size(min = 1, max = 20)
	private String username;

	@Column(name = "email", nullable = false)
	// @Size(min = 1, max = 30)
	private String email;

	@Column(name = "password", nullable = false)
	// @Size(min = 1, max = 20)
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
	private Set<Role> roles = new HashSet<>();

	//

	public User() {
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

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

}
