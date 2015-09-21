package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sooeez.ecomm.domain.ShipmentItem;

public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, Long> {

}
