package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.Tag;
import com.sooeez.ecomm.repository.TagRepository;

@Service
public class TagService {
	
	/*
	 * Repository
	 */

	@Autowired 
	private TagRepository tagRepository;
	
	/*
	 * Tag
	 */
	
	@Transactional
	public Tag saveTag(Tag tag) {
		return tagRepository.save(tag);
	}
	
	@Transactional
	public void deleteTag(Long id) {
		tagRepository.delete(id);
	}
	
	public Boolean existsTag(Tag tag) {
		return tagRepository.count(getTagSpecification(tag)) > 0 ? true : false;
	}
	
	public Tag getTag(Long id) {
		return tagRepository.findOne(id);
	}
	
	public List<Tag> getTags(Tag tag, Sort sort) {
		return tagRepository.findAll(getTagSpecification(tag), sort);
	}

	public Page<Tag> getPagedTags(Tag tag, Pageable pageable) {
		return tagRepository.findAll(getTagSpecification(tag), pageable);
	}
	
	private Specification<Tag> getTagSpecification(Tag tag) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (tag.getId() != null) {
				if (tag.getCheckUnique() != null && tag.getCheckUnique().booleanValue() == true) {
					predicates.add(cb.notEqual(root.get("id"), tag.getId()));
				} else {
					predicates.add(cb.equal(root.get("id"), tag.getId()));
				}
			}
			if (StringUtils.hasText(tag.getName())) {
				predicates.add(cb.equal(root.get("name"), tag.getName()));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};

	}
}
