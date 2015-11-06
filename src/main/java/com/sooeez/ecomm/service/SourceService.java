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

import com.sooeez.ecomm.domain.Source;
import com.sooeez.ecomm.repository.SourceRepository;

@Service
public class SourceService {
	
	/*
	 * Repository
	 */

	@Autowired 
	private SourceRepository sourceRepository;
	
	/*
	 * Source
	 */
	
	@Transactional
	public Source saveSource(Source source) {
		return sourceRepository.save(source);
	}
	
	@Transactional
	public void deleteSource(Long id) {
		sourceRepository.delete(id);
	}
	
	public Boolean existsSource(Source source) {
		return sourceRepository.count(getSourceSpecification(source)) > 0 ? true : false;
	}
	
	public Source getSource(Long id) {
		return sourceRepository.findOne(id);
	}
	
	public List<Source> getSources(Source source, Sort sort) {
		return sourceRepository.findAll(getSourceSpecification(source), sort);
	}

	public Page<Source> getPagedSources(Source source, Pageable pageable) {
		return sourceRepository.findAll(getSourceSpecification(source), pageable);
	}
	
	private Specification<Source> getSourceSpecification(Source source) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (source.getId() != null) {
				if (source.getCheckUnique() != null && source.getCheckUnique().booleanValue() == true) {
					predicates.add(cb.notEqual(root.get("id"), source.getId()));
				} else {
					predicates.add(cb.equal(root.get("id"), source.getId()));
				}
			}
			if (StringUtils.hasText(source.getName())) {
				predicates.add(cb.equal(root.get("name"), source.getName()));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};

	}
}
