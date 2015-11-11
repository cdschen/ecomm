package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sooeez.ecomm.domain.Process;

public interface ProcessRepository extends JpaRepository<Process, Long>, JpaSpecificationExecutor<Process> {
	
}
