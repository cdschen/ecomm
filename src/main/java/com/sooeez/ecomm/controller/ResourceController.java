package com.sooeez.ecomm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.config.ECOMMResource;

@RestController
@RequestMapping("/api")
public class ResourceController {
	
	@Autowired private Environment env;
	
	/*
	 * Resource
	 */
	
	@RequestMapping(value = "/resource")
	public ECOMMResource getECOMMResource() {
		ECOMMResource resource = new ECOMMResource();
		resource.setResourceUrl(env.getProperty("ecomm.resource.url"));
		return resource;
	}
}
