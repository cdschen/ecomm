package com.sooeez.ecomm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.sooeez.ecomm.domain.Source;
import com.sooeez.ecomm.service.SourceService;

@RestController
@RequestMapping("/api")
public class SourceController {

	/*
	 * Service
	 */

	@Autowired
	private SourceService sourceService;

	/*
	 * Source
	 */

	@RequestMapping(value = "/sources/check-unique")
	public Boolean existsSource(Source source) {
		return sourceService.existsSource(source);
	}

	@RequestMapping(value = "/sources/{id}")
	public Source getSource(@PathVariable("id") Long id) {
		return sourceService.getSource(id);
	}

	@RequestMapping(value = "/sources")
	public Page<Source> getPagedSources(Source source, Pageable pageable) {
		return sourceService.getPagedSources(source, pageable);
	}

	@RequestMapping(value = "/sources/get/all")
	public List<Source> getSources(Source source, Sort sort) {
		return sourceService.getSources(source, sort);
	}

	@RequestMapping(value = "/sources", method = RequestMethod.POST)
	public Source saveSource(@RequestBody Source source) {
		return sourceService.saveSource(source);
	}

	@RequestMapping(value = "/sources/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteSource(@PathVariable("id") Long id) {
		sourceService.deleteSource(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
