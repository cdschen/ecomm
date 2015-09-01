package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.MadeFrom;
import com.sooeez.ecomm.repository.MadeFromRepository;

@Service
public class MadeFromService {

	@Autowired MadeFromRepository madeFromRepository;
	
	/*
	 * MadeFrom
	 */
	
	public MadeFrom saveMadeFrom(MadeFrom madeFrom) {
		return this.madeFromRepository.save(madeFrom);
	}
	
	public void deleteMadeFrom(Long id) {
		this.madeFromRepository.delete(id);
	}
	
	public MadeFrom getMadeFrom(Long id) {
		return this.madeFromRepository.findOne(id);
	}
	
	public List<MadeFrom> getMadeFroms() {
		return this.madeFromRepository.findAll();
	}

	public Page<MadeFrom> getPagedMadeFroms(Pageable pageable) {
		return this.madeFromRepository.findAll(pageable);
	}
}
