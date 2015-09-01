package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.domain.Process;

public interface ObjectProcessRepository extends JpaRepository<ObjectProcess, Long>,JpaSpecificationExecutor<ObjectProcess> {

}
