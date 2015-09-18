package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.Language;
import com.sooeez.ecomm.repository.LanguageRepository;

@Service
public class LanguageService {

	@Autowired private LanguageRepository languageRepository;
	
	/*
	 * Language
	 */
	
	public Language saveLanguage(Language language) {
		return this.languageRepository.save(language);
	}
	
	public void deleteLanguage(Long id) {
		this.languageRepository.delete(id);
	}
	
	public Language getLanguage(Long id) {
		return this.languageRepository.findOne(id);
	}
	
	public List<Language> getLanguages() {
		return this.languageRepository.findAll();
	}

	public Page<Language> getPagedLanguages(Pageable pageable) {
		return this.languageRepository.findAll(pageable);
	}
}
