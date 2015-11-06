package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.repository.ObjectProcessRepository;

@Service
public class ObjectProcessService {
	
	/*
	 * Repository
	 */
	
	@Autowired
	private ObjectProcessRepository objectProcessRepository;

	/*
	 * ObjectProcess
	 */

	@Transactional
	public ObjectProcess saveObjectProcess(ObjectProcess objectProcess) {
		return objectProcessRepository.save(objectProcess);
	}

	@Transactional
	public void deleteObjectProcess(Long id) {
		objectProcessRepository.delete(id);
	}

	public ObjectProcess getObjectProcess(Long id) {
		return objectProcessRepository.findOne(id);
	}

	public List<ObjectProcess> getObjectProcesses(ObjectProcess objectProcess) {
		return objectProcessRepository.findAll(getObjectProcessSpecification(objectProcess));
	}

	public Page<ObjectProcess> getPagedObjectProcesses(Pageable pageable) {
		return objectProcessRepository.findAll(pageable);
	}

	public Long getObjectProcessCount(ObjectProcess objectProcess) {
		return objectProcessRepository.count(getObjectProcessSpecification(objectProcess));
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
