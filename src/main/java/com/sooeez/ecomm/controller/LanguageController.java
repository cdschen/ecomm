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

import com.sooeez.ecomm.domain.Language;
import com.sooeez.ecomm.domain.Language;
import com.sooeez.ecomm.service.LanguageService;

@RestController
@RequestMapping("/api")
public class LanguageController {
	
	@Autowired private LanguageService languageService;

	/*
	 * Language
	 */
	
	@RequestMapping(value = "/languages/{id}")
	public Language getLanguage(@PathVariable("id") Long id) {
		return this.languageService.getLanguage(id);
	}
	
	@RequestMapping(value = "/languages")
	public Page<Language> getPagedLanguages(Pageable pageable) {
		return this.languageService.getPagedLanguages(pageable);
	}
	
	@RequestMapping(value = "/languages/get/all")
	public List<Language> getLanguages() {
		return this.languageService.getLanguages();
	}
	
	@RequestMapping(value = "/languages", method = RequestMethod.POST)
	public Language saveLanguage(@RequestBody Language language) {
		return this.languageService.saveLanguage(language);
	}
	
	@RequestMapping(value = "/languages/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteLanguage(@PathVariable("id") Long id) {
		this.languageService.deleteLanguage(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
