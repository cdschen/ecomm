package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sooeez.ecomm.domain.InventoryBatchItem;

public interface InventoryBatchItemRepository extends JpaRepository<InventoryBatchItem, Long>, JpaSpecificationExecutor<InventoryBatchItem>  {

}
