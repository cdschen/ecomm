package com.sooeez.ecomm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.config.ECOMMResource;
import com.sooeez.ecomm.domain.Authority;
import com.sooeez.ecomm.domain.Currency;
import com.sooeez.ecomm.domain.Language;
import com.sooeez.ecomm.domain.Role;
import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.domain.User;
import com.sooeez.ecomm.service.CurrencyService;
import com.sooeez.ecomm.service.LanguageService;
import com.sooeez.ecomm.service.ShopService;
import com.sooeez.ecomm.service.UserService;

@RestController
@RequestMapping("/api")
public class SystemController {

	@Autowired private UserService userService;
	
	@Autowired private ShopService shopService;
	
	/*
	 * User
	 */
	
	@RequestMapping(value = "/users/check-unique/{username}")
	public boolean existsUser(@PathVariable("username") String username) {
		return this.userService.existsUser(username);
	}
	
	@RequestMapping(value = "/users/check-unique/{username}/{id}")
	public boolean existsUserNotSelf(@PathVariable("username") String username, Long id) {
		return this.userService.existsNotSelfUser(username, id);
	}
	
	@RequestMapping(value = "/users/{id}")
	public User getUser(@PathVariable("id") Long id) {
		return this.userService.getUser(id);
	}
	
	@RequestMapping(value = "/users")
	public Page<User> getPagedUsers(Pageable pageable) {
		return this.userService.getPagedUsers(pageable);
	}
	
	@RequestMapping(value = "/users/get/all")
	public List<User> getUsers() {
		return this.userService.getUsers();
	}
	
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public User saveUser(@RequestBody User user) {
		return this.userService.saveUser(user);
	}
	
	@RequestMapping(value = "/users/update/password", method = RequestMethod.POST)
	public ResponseEntity<?> updatePassword(@RequestBody User user) {
		this.userService.updatePassword(user);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
		this.userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * Role
	 */
	
	@RequestMapping(value = "/roles/{id}")
	public Role getRole(@PathVariable("id") Long id) {
		return this.userService.getRole(id);
	}
	
	@RequestMapping(value = "/roles")
	public List<Role> getRoles() {
		return this.userService.getRoles();
	}
	
	@RequestMapping(value = "/roles", method = RequestMethod.POST)
	public Role saveRole(@RequestBody Role role) {
		return this.userService.saveRole(role);
	}
	
	@RequestMapping(value = "/roles/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteRole(@PathVariable("id") Long id) {
		this.userService.deleteRole(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * Authority
	 */
	
	@RequestMapping(value = "/authorities")
	public List<Authority> getAuthorites() {
		return this.userService.getAuthorities();
	}
	
	/*
	 * Shop
	 */
	
	@RequestMapping(value = "/shops/{id}")
	public Shop getShop(@PathVariable("id") Long id) {
		return this.shopService.getShop(id);
	}
	
	@RequestMapping(value = "/shops")
	public Page<Shop> getPagedShops(Pageable pageable, Shop shop) {
		return this.shopService.getPagedShops(pageable, shop);
	}
	
	@RequestMapping(value = "/shops/get/all")
	public List<Shop> getShops(Shop shop, Sort sort) {
		return this.shopService.getShops(shop, sort);
	}
	
	@RequestMapping(value = "/shops", method = RequestMethod.POST)
	public Shop saveShop(@RequestBody Shop shop) {
		return this.shopService.saveShop(shop);
	}
	
	@RequestMapping(value = "/shops/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteShop(@PathVariable("id") Long id) {
		this.shopService.deleteShop(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	
}
