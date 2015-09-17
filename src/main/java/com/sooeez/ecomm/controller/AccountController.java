package com.sooeez.ecomm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.User;
import com.sooeez.ecomm.service.UserService;

@RestController
@RequestMapping("/api")
public class AccountController {
	
	@Autowired
	private UserService userService;
	
//	@RequestMapping(value="/register", method = RequestMethod.POST)
//	public ResponseEntity<?> registerAccount(@RequestBody User user) {
//		userService.createUserInformation(user);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }

    @RequestMapping(value = "/account")
    public ResponseEntity<User> getAccount() {
    	return new ResponseEntity<User>(userService.getUserWithAuthorities(), HttpStatus.OK);
    }

}
