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
import javax.validation.constraints.Size;

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
	@Size(min = 5, max = 50)
	private String username;

	@Column(name = "email", nullable = false)
	@Size(min = 5, max = 100)
	private String email;

	@Column(name = "password", nullable = false)
	@Size(min = 5, max = 100)
	private String password;

	/*
	 * Related Properties
	 */
	
	@ManyToMany
	@JoinTable(name = "t_user_role", 
		joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }, 
		inverseJoinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "id") })
	private Set<Role> roles = new HashSet<>();

	//
	
	public User() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

}
