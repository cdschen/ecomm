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

import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.domain.Process;
import com.sooeez.ecomm.domain.ProcessStep;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.repository.ObjectProcessRepository;
import com.sooeez.ecomm.repository.ProcessRepository;
import com.sooeez.ecomm.repository.ProcessStepRepository;

@Service
public class ProcessService {
	
	@Autowired ProcessRepository processRepository;
	@Autowired ProcessStepRepository processStepRepository;
	@Autowired ObjectProcessRepository objectProcessRepository;
	
	/*
	 * Process
	 */
	
	@Transactional
	public Process saveProcess(Process process) {
//		process.getSteps().forEach(step -> {
//			step.setProcess(process);
//		});
		this.processRepository.save(process);
		System.out.println("process.getDefaultStepName():" + process.getDefaultStepName());
		System.out.println("process.getId():" + process.getId());
		if (StringUtils.hasText(process.getDefaultStepName())) {
			List<ProcessStep> steps = process.getSteps();
			for (int i = 0, len = steps.size(); i < len; i++) {
				ProcessStep step = steps.get(i);
				if (process.getDefaultStepName().equals(step.getName())) {
					process.setDefaultStepId(step.getId());
					return this.processRepository.save(process);
				}
			}
		}
		return process;
	}
	
	public void deleteProcess(Long id) {
		this.processRepository.delete(id);
	}
	
	public Process getProcess(Long id) {
		return this.processRepository.findOne(id);
	}
	
	public List<Process> getProcesses(Process process, Sort sort) {
		return this.processRepository.findAll(getProcessSpecification(process), sort);
	}

	public Page<Process> getPagedProcesses(Pageable pageable) {
		return this.processRepository.findAll(pageable);
	}
	
	private Specification<Process> getProcessSpecification(Process process) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (process.getObjectType() != null) {
				predicates.add(cb.equal(root.get("objectType"), process.getObjectType()));
			}
			predicates.add(cb.equal(root.get("deleted"), process.getDeleted()));
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
	
	/*
	 * ProcessStep
	 */
	
	public ProcessStep saveProcessStep(ProcessStep processStep) {
		return this.processStepRepository.save(processStep);
	}
	
	public void deleteProcessStep(Long id) {
		this.processStepRepository.delete(id);
	}
	
	public ProcessStep getProcessStep(Long id) {
		return this.processStepRepository.findOne(id);
	}
	
	public List<ProcessStep> getProcessSteps() {
		return this.processStepRepository.findAll();
	}

	public Page<ProcessStep> getPagedProcessSteps(Pageable pageable) {
		return this.processStepRepository.findAll(pageable);
	}
	
	/*
	 * ObjectProcess
	 */
	
	public ObjectProcess saveObjectProcess(ObjectProcess objectProcess) {
		return this.objectProcessRepository.save(objectProcess);
	}
	
	public void deleteObjectProcess(Long id) {
		this.objectProcessRepository.delete(id);
	}
	
	public ObjectProcess getObjectProcess(Long id) {
		return this.objectProcessRepository.findOne(id);
	}
	
	public List<ObjectProcess> getObjectProcesses(ObjectProcess objectProcess) {
		return this.objectProcessRepository.findAll(getObjectProcessSpecification(objectProcess));
	}

	public Page<ObjectProcess> getPagedObjectProcesses(Pageable pageable) {
		return this.objectProcessRepository.findAll(pageable);
	}
	
	public long getObjectProcessCount(ObjectProcess objectProcess) {
		return this.objectProcessRepository.count(getObjectProcessSpecification(objectProcess));
	}
	
	private Specification<ObjectProcess> getObjectProcessSpecification(ObjectProcess objectProcess) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (objectProcess.getObjectId() != null) {
				predicates.add(cb.equal(root.get("objectId"), objectProcess.getObjectId()));
			}
			if (objectProcess.getProcessId() != null) {
				predicates.add(cb.equal(root.get("processId"), objectProcess.getProcessId()));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

}
