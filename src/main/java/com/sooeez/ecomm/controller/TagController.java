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

import com.sooeez.ecomm.domain.Tag;
import com.sooeez.ecomm.service.TagService;

@RestController
@RequestMapping("/api")
public class TagController {

	/*
	 * Service
	 */

	@Autowired
	private TagService tagService;

	/*
	 * Tag
	 */

	@RequestMapping(value = "/tags/check-unique")
	public Boolean existsTag(Tag tag) {
		return tagService.existsTag(tag);
	}

	@RequestMapping(value = "/tags/{id}")
	public Tag getTag(@PathVariable("id") Long id) {
		return this.tagService.getTag(id);
	}

	@RequestMapping(value = "/tags")
	public Page<Tag> getPagedTags(Tag tag, Pageable pageable) {
		return this.tagService.getPagedTags(tag, pageable);
	}

	@RequestMapping(value = "/tags/get/all")
	public List<Tag> getTags(Tag tag, Sort sort) {
		return this.tagService.getTags(tag, sort);
	}

	@RequestMapping(value = "/tags", method = RequestMethod.POST)
	public Tag saveTag(@RequestBody Tag tag) {
		return this.tagService.saveTag(tag);
	}

	@RequestMapping(value = "/tags/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteTag(@PathVariable("id") Long id) {
		this.tagService.deleteTag(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
