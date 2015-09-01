package com.sooeez.ecomm.config;

import java.io.Serializable;

public class ECOMMResource implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String resourceUrl;

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}	

	
}
