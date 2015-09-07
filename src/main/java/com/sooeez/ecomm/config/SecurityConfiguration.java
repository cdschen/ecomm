package com.sooeez.ecomm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.sooeez.ecomm.filter.CsrfCookieGeneratorFilter;
import com.sooeez.ecomm.security.AjaxAuthenticationFailureHandler;
import com.sooeez.ecomm.security.AjaxAuthenticationSuccessHandler;
import com.sooeez.ecomm.security.AjaxLogoutSuccessHandler;
import com.sooeez.ecomm.security.Http401UnauthorizedEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Order(1)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private Http401UnauthorizedEntryPoint authenticationEntryPoint;
	
	@Autowired
	private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

	@Autowired
	private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

	@Autowired
	private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService)
            	.passwordEncoder(passwordEncoder());
    }

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/views/**")
			.antMatchers("/upload/**")
			.antMatchers("/images/**")
			.antMatchers("/styles/**")
			.antMatchers("/bower_components/**")
			.antMatchers("/scripts/**");
	}
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		 http
         	.csrf()
         	.csrfTokenRepository(csrfTokenRepository())
         .and()
            .addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
         .and()
            .formLogin()
            .loginProcessingUrl("/api/authenticate")
            .successHandler(ajaxAuthenticationSuccessHandler)
            .failureHandler(ajaxAuthenticationFailureHandler)
            .usernameParameter("j_username")
            .passwordParameter("j_password")
            .permitAll()
         .and()
          	.logout()
          	.logoutUrl("/api/logout")
          	.logoutSuccessHandler(ajaxLogoutSuccessHandler)
          	.deleteCookies("JSESSIONID")
          	.permitAll()
         .and()
         	.authorizeRequests()
         	.antMatchers("/api/resource").permitAll()
		 	.antMatchers("/api/**").authenticated();
    }
	
	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}

}
