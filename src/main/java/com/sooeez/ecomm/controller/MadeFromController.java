package com.sooeez.ecomm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.MadeFrom;
import com.sooeez.ecomm.service.MadeFromService;

@RestController
@RequestMapping("/api")
public class MadeFromController {
	
	@Autowired MadeFromService madeFromService;
	
	/*
	 * MadeFrom
	 */
	
	@RequestMapping(value = "/madefroms/{id}")
	public MadeFrom getMadeFrom(@PathVariable("id") Long id) {
		return this.madeFromService.getMadeFrom(id);
	}
	
	@RequestMapping(value = "/madefroms")
	public Page<MadeFrom> getPagedMadeFroms(Pageable pageable) {
		return this.madeFromService.getPagedMadeFroms(pageable);
	}
	
	@RequestMapping(value = "/madefroms/get/all")
	public List<MadeFrom> getMadeFroms() {
		return this.madeFromService.getMadeFroms();
	}
	
	@RequestMapping(value = "/madefroms", method = RequestMethod.POST)
	public MadeFrom saveMadeFrom(@RequestBody MadeFrom madeFrom) {
		return this.madeFromService.saveMadeFrom(madeFrom);
	}
	
	@RequestMapping(value = "/madefroms/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMadeFrom(@PathVariable("id") Long id) {
		this.madeFromService.deleteMadeFrom(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
