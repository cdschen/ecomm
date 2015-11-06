package com.sooeez.ecomm.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.ProcessStep;
import com.sooeez.ecomm.repository.ProcessStepRepository;

@Service
public class ProcessStepService {

	/*
	 * Repository
	 */

	@Autowired
	private ProcessStepRepository stepRepository;

	/*
	 * ProcessStep
	 */

	@Transactional
	public ProcessStep saveStep(ProcessStep step) {
		return stepRepository.save(step);
	}

	@Transactional
	public void deleteStep(Long id) {
		stepRepository.delete(id);
	}

	public ProcessStep getStep(Long id) {
		return stepRepository.findOne(id);
	}

	public List<ProcessStep> getSteps() {
		return stepRepository.findAll();
	}

	public Page<ProcessStep> getPagedSteps(Pageable pageable) {
		return stepRepository.findAll(pageable);
	}

}
