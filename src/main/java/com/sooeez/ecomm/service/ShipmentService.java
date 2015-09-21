package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.Shipment;
import com.sooeez.ecomm.domain.ShipmentItem;
import com.sooeez.ecomm.repository.ShipmentItemRepository;
import com.sooeez.ecomm.repository.ShipmentRepository;

@Service
public class ShipmentService {

	@Autowired private ShipmentRepository shipmentRepository;

	@Autowired private ShipmentItemRepository shipmentItemRepository;
	
	/*
	 * Shipment
	 */
	
	public Shipment saveShipment(Shipment shipment) {
		return this.shipmentRepository.save(shipment);
	}
	
	public void deleteShipment(Long id) {
		this.shipmentRepository.delete(id);
	}
	
	public Shipment getShipment(Long id) {
		return this.shipmentRepository.findOne(id);
	}
	
	public List<Shipment> getShipments() {
		return this.shipmentRepository.findAll();
	}

	public Page<Shipment> getPagedShipments(Pageable pageable) {
		return this.shipmentRepository.findAll(pageable);
	}
	
	/*
	 * ShipmentItem
	 */
	
	public ShipmentItem saveShipmentItem(ShipmentItem shipmentItem) {
		return this.shipmentItemRepository.save(shipmentItem);
	}
	
	public void deleteShipmentItem(Long id) {
		this.shipmentItemRepository.delete(id);
	}
	
	public ShipmentItem getShipmentItem(Long id) {
		return this.shipmentItemRepository.findOne(id);
	}
	
	public List<ShipmentItem> getShipmentItems() {
		return this.shipmentItemRepository.findAll();
	}

	public Page<ShipmentItem> getPagedShipmentItems(Pageable pageable) {
		return this.shipmentItemRepository.findAll(pageable);
	}
	
}
