package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.Process;
import com.sooeez.ecomm.domain.ProcessStep;
import com.sooeez.ecomm.repository.ProcessRepository;

@Service
public class ProcessService {

	/*
	 * Repository
	 */

	@Autowired
	private ProcessRepository processRepository; 

	/*
	 * Process
	 */

	@Transactional
	public Process saveProcess(Process process) {

		if (StringUtils.hasText(process.getDefaultStepName())) {
			List<ProcessStep> steps = process.getSteps();
			for (int i = 0, len = steps.size(); i < len; i++) {
				ProcessStep step = steps.get(i);
				if (process.getDefaultStepName().equals(step.getName())) {
					process.setDefaultStep(step);
					break;
				}
			}
		}
	
		return processRepository.save(process);
	}

	@Transactional
	public void deleteProcess(Long id) {
		processRepository.delete(id);
	}

	public Boolean existsProcess(Process process) {
		return processRepository.count(getProcessSpecification(process)) > 0 ? true : false;
	}

	public Process getProcess(Long id) {
		return processRepository.findOne(id);
	}

	public List<Process> getProcesses(Process process, Sort sort) {
		return processRepository.findAll(getProcessSpecification(process), sort);
	}

	public Page<Process> getPagedProcesses(Process process, Pageable pageable) {
		return processRepository.findAll(getProcessSpecification(process), pageable);
	}

	private Specification<Process> getProcessSpecification(Process process) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (process.getId() != null) {
				if (process.getCheckUnique() != null && process.getCheckUnique().booleanValue() == true) {
					predicates.add(cb.notEqual(root.get("id"), process.getId()));
				} else {
					predicates.add(cb.equal(root.get("id"), process.getId()));
				}
			}
			if (StringUtils.hasText(process.getName())) {
				predicates.add(cb.equal(root.get("name"), process.getName()));
			}
			if (process.getEnabled() != null) {
				predicates.add(cb.equal(root.get("enabled"), process.getEnabled()));
			}
			if (process.getObjectType() != null) {
				predicates.add(cb.equal(root.get("objectType"), process.getObjectType()));
			}

			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};

	}

}
