package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sooeez.ecomm.domain.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

}
