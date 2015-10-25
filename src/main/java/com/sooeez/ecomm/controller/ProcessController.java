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
import com.sooeez.ecomm.service.ProcessService;

@RestController
@RequestMapping("/api")
public class ProcessController {

	@Autowired ProcessService processService;
	
	/*
	 * Process
	 */
	
	@RequestMapping(value = "/processes/{id}")
	public Process getProcess(@PathVariable("id") Long id) {
		return this.processService.getProcess(id);
	}
	
	@RequestMapping(value = "/processes")
	public Page<Process> getPagedProcesss(Pageable pageable) {
		return this.processService.getPagedProcesses(pageable);
	}
	
	@RequestMapping(value = "/processes/get/all")
	public List<Process> getProcesss(Process process, Sort sort) {
		return this.processService.getProcesses(process, sort);
	}
	
	@RequestMapping(value = "/processes", method = RequestMethod.POST)
	public Process saveProcess(@RequestBody Process process) {
		return this.processService.saveProcess(process);
	}
	
	@RequestMapping(value = "/processes/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProcess(@PathVariable("id") Long id) {
		this.processService.deleteProcess(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * ProcessStep
	 */
	
	@RequestMapping(value = "/processsteps/{id}")
	public ProcessStep getProcessStep(@PathVariable("id") Long id) {
		return this.processService.getProcessStep(id);
	}
	
	@RequestMapping(value = "/processsteps")
	public Page<ProcessStep> getPagedProcessSteps(Pageable pageable) {
		return this.processService.getPagedProcessSteps(pageable);
	}
	
	@RequestMapping(value = "/processsteps/get/all")
	public List<ProcessStep> getProcessSteps() {
		return this.processService.getProcessSteps();
	}
	
	@RequestMapping(value = "/processsteps", method = RequestMethod.POST)
	public ProcessStep saveProcessStep(@RequestBody ProcessStep processStep) {
		return this.processService.saveProcessStep(processStep);
	}
	
	@RequestMapping(value = "/processsteps/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProcessStep(@PathVariable("id") Long id) {
		this.processService.deleteProcessStep(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * ObjectProcess
	 */
	
	@RequestMapping(value = "/objectprocesses/{id}")
	public ObjectProcess getObjectProcess(@PathVariable("id") Long id) {
		return this.processService.getObjectProcess(id);
	}
	
	@RequestMapping(value = "/objectprocesses")
	public Page<ObjectProcess> getPagedObjectProcesses(Pageable pageable) {
		return this.processService.getPagedObjectProcesses(pageable);
	}
	
	@RequestMapping(value = "/objectprocesses/get/all")
	public List<ObjectProcess> getObjectProcesses(ObjectProcess objectProcess) {
		return this.processService.getObjectProcesses(objectProcess);
	}
	
	@RequestMapping(value = "/objectprocesses/get/count")
	public long getObjectProcessCount(ObjectProcess objectProcess) {
		return this.processService.getObjectProcessCount(objectProcess);
	}
	
	@RequestMapping(value = "/objectprocesses", method = RequestMethod.POST)
	public ObjectProcess saveObjectProcess(@RequestBody ObjectProcess objectProcess) {
		return this.processService.saveObjectProcess(objectProcess);
	}
	
	@RequestMapping(value = "/objectprocesses/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteObjectProcess(@PathVariable("id") Long id) {
		this.processService.deleteObjectProcess(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
