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

import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.domain.ProcessStep;
import com.sooeez.ecomm.domain.Process;
import com.sooeez.ecomm.service.ObjectProcessService;
import com.sooeez.ecomm.service.ProcessService;
import com.sooeez.ecomm.service.ProcessStepService;

@RestController
@RequestMapping("/api")
public class ProcessController {

	/*
	 * Service
	 */

	@Autowired
	private ProcessService processService;
	
	@Autowired
	private ProcessStepService stepService;
	
	@Autowired
	private ObjectProcessService objectProcessService;

	/*
	 * Process
	 */
	
	@RequestMapping(value = "/processes/check-unique")
	public Boolean existsProcess(Process process) {
		return processService.existsProcess(process);
	}

	@RequestMapping(value = "/processes/{id}")
	public Process getProcess(@PathVariable("id") Long id) {
		return processService.getProcess(id);
	}

	@RequestMapping(value = "/processes")
	public Page<Process> getPagedProcesses(Process process, Pageable pageable) {
		return processService.getPagedProcesses(process, pageable);
	}

	@RequestMapping(value = "/processes/get/all")
	public List<Process> getProcesses(Process process, Sort sort) {
		return processService.getProcesses(process, sort);
	}

	@RequestMapping(value = "/processes", method = RequestMethod.POST)
	public Process saveProcess(@RequestBody Process process) {
		return processService.saveProcess(process);
	}

	@RequestMapping(value = "/processes/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProcess(@PathVariable("id") Long id) {
		processService.deleteProcess(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/*
	 * ProcessStep
	 */

	@RequestMapping(value = "/process-steps/{id}")
	public ProcessStep getStep(@PathVariable("id") Long id) {
		return stepService.getStep(id);
	}

	@RequestMapping(value = "/process-steps")
	public Page<ProcessStep> getPagedProcessSteps(Pageable pageable) {
		return stepService.getPagedSteps(pageable);
	}

	@RequestMapping(value = "/process-steps/get/all")
	public List<ProcessStep> getSteps() {
		return stepService.getSteps();
	}

	@RequestMapping(value = "/process-steps", method = RequestMethod.POST)
	public ProcessStep saveStep(@RequestBody ProcessStep step) {
		return stepService.saveStep(step);
	}

	@RequestMapping(value = "/process-steps/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteStep(@PathVariable("id") Long id) {
		stepService.deleteStep(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/*
	 * ObjectProcess
	 */

	@RequestMapping(value = "/object-processes/{id}")
	public ObjectProcess getObjectProcess(@PathVariable("id") Long id) {
		return objectProcessService.getObjectProcess(id);
	}

	@RequestMapping(value = "/object-processes")
	public Page<ObjectProcess> getPagedObjectProcesses(Pageable pageable) {
		return objectProcessService.getPagedObjectProcesses(pageable);
	}

	@RequestMapping(value = "/object-processes/get/all")
	public List<ObjectProcess> getObjectProcesses(ObjectProcess objectProcess) {
		return objectProcessService.getObjectProcesses(objectProcess);
	}

	@RequestMapping(value = "/object-processes/get/count")
	public long getObjectProcessCount(ObjectProcess objectProcess) {
		return objectProcessService.getObjectProcessCount(objectProcess);
	}

	@RequestMapping(value = "/object-processes", method = RequestMethod.POST)
	public ObjectProcess saveObjectProcess(@RequestBody ObjectProcess objectProcess) {
		return objectProcessService.saveObjectProcess(objectProcess);
	}

	@RequestMapping(value = "/object-processes/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteObjectProcess(@PathVariable("id") Long id) {
		objectProcessService.deleteObjectProcess(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
