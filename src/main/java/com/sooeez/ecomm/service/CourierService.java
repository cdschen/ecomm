package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.Courier;
import com.sooeez.ecomm.repository.CourierRepository;

@Service
public class CourierService {

	@Autowired private CourierRepository courierRepository;
	
	/*
	 * Courier
	 */
	
	public Courier saveCourier(Courier courier) {
		return this.courierRepository.save(courier);
	}
	
	public void deleteCourier(Long id) {
		this.courierRepository.delete(id);
	}
	
	public Courier getCourier(Long id) {
		return this.courierRepository.findOne(id);
	}
	
	public List<Courier> getCouriers() {
		return this.courierRepository.findAll();
	}

	public Page<Courier> getPagedCouriers(Pageable pageable) {
		return this.courierRepository.findAll(pageable);
	}
	
}
