package com.sooeez.ecomm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.sooeez.ecomm.security.Http401UnauthorizedEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private Http401UnauthorizedEntryPoint authenticationEntryPoint;

	@Override
    protected void configure(HttpSecurity http) throws Exception {
		 http
         	.csrf().disable()
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
         .and()
         	.authorizeRequests()
         	.antMatchers("/oauth/api/**").permitAll();
    }
}
