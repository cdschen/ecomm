package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.WarehousePosition;
import com.sooeez.ecomm.repository.WarehousePositionRepository;

@Service
public class WarehousePositionService {

	/*
	 * Repository
	 */
	
	@Autowired
	private WarehousePositionRepository positionRepository;
	
	/*
	 * WarehousePosition
	 */

	@Transactional
	public WarehousePosition savePosition(WarehousePosition position) {
		return positionRepository.save(position);
	}

	@Transactional
	public List<WarehousePosition> savePositions(List<WarehousePosition> positions) {
		return positionRepository.save(positions);
	}

	@Transactional
	public void deletePosition(Long id) {
		positionRepository.delete(id);
	}

	public WarehousePosition getPosition(Long id) {
		return positionRepository.findOne(id);
	}

	public List<WarehousePosition> getPositions() {
		return positionRepository.findAll();
	}

	public Page<WarehousePosition> getPagedPositions(Pageable pageable) {
		return positionRepository.findAll(pageable);
	}
	
}
