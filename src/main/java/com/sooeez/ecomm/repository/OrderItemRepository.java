package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sooeez.ecomm.domain.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
