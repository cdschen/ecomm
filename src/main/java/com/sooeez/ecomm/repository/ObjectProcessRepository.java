package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sooeez.ecomm.domain.ObjectProcess;

public interface ObjectProcessRepository extends JpaRepository<ObjectProcess, Long>,JpaSpecificationExecutor<ObjectProcess> {

	@Query( "UPDATE ObjectProcess SET stepId = ?1 WHERE objectId = ?2 AND processId = ?3 AND objectType = ?4" )
	@Modifying
	void updateStepId( Long stepId, Long objectId, Long processId, Integer objectType );
	
}
