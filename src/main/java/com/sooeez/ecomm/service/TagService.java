package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.Tag;
import com.sooeez.ecomm.repository.TagRepository;

@Service
public class TagService {

	@Autowired TagRepository tagRepository;
	
	/*
	 * Tag
	 */
	
	public Tag saveTag(Tag tag) {
		return this.tagRepository.save(tag);
	}
	
	public void deleteTag(Long id) {
		this.tagRepository.delete(id);
	}
	
	public Tag getTag(Long id) {
		return this.tagRepository.findOne(id);
	}
	
	public List<Tag> getTags() {
		return this.tagRepository.findAll();
	}

	public Page<Tag> getPagedTags(Pageable pageable) {
		return this.tagRepository.findAll(pageable);
	}
}
