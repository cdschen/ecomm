package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sooeez.ecomm.domain.PurchaseOrderDeliveryItem;

public interface PurchaseOrderDeliveryItemRepository extends JpaRepository<PurchaseOrderDeliveryItem, Long>, JpaSpecificationExecutor<PurchaseOrderDeliveryItem> {

}
