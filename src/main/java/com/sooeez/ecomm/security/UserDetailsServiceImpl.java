package com.sooeez.ecomm.security;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.sooeez.ecomm.domain.*;
import com.sooeez.ecomm.repository.UserRepository;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		log.debug("Authenticating {}", username);

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		User user = this.userRepository.findOneByUsername(username);

		user.getRoles().forEach(role -> {
			grantedAuthorities.add(new SimpleGrantedAuthority(role.getCode()));
		});
		
		UserDetails userDetails = new org.springframework.security.core.userdetails.User(
				user.getUsername(), user.getPassword(), grantedAuthorities);

		return userDetails;
	}

}
