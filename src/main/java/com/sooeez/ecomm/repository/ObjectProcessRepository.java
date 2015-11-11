package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sooeez.ecomm.domain.ObjectProcess;

public interface ObjectProcessRepository extends JpaRepository<ObjectProcess, Long>,JpaSpecificationExecutor<ObjectProcess> {

}
