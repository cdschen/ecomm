package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.InventoryBatchItem;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.repository.InventoryBatchItemRepository;

@Service
public class InventoryBatchItemService {
	
	/*
	 * Repository
	 */
	
	@Autowired 
	private InventoryBatchItemRepository batchItemRepository;

	/*
	 * InventoryBatchItem
	 */

	@Transactional
	public InventoryBatchItem saveBatchItem(InventoryBatchItem item) {
		return batchItemRepository.save(item);
	}

	@Transactional
	public void deleteBatchItem(Long id) {
		batchItemRepository.delete(id);
	}

	public InventoryBatchItem getBatchItem(Long id) {
		return batchItemRepository.findOne(id);
	}

	public List<InventoryBatchItem> getBatchItems(InventoryBatchItem item, Sort sort) {
		return batchItemRepository.findAll(getBatchItemSpecification(item), sort);
	}

	public Page<InventoryBatchItem> getPagedBatchItems(InventoryBatchItem item, Pageable pageable) {
		return batchItemRepository.findAll(getBatchItemSpecification(item), pageable);
	}
	
	private Specification<InventoryBatchItem> getBatchItemSpecification(InventoryBatchItem item) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (item.getId() != null) {
				predicates.add(cb.equal(root.get("id"), item.getId()));
			}
			if (item.getProductId() != null) {
				predicates.add(cb.equal(root.get("productId"), item.getProductId()));
			}
			if (item.getWarehouseId() != null) {
				predicates.add(cb.equal(root.get("warehouseId"), item.getWarehouseId()));
			}
			if (item.getBatchOperate() != null) {
				predicates.add(cb.equal(root.get("batchOperate"), item.getBatchOperate()));
			}
			if (item.getCreateTimeStart() != null && item.getCreateTimeEnd() != null) {
				predicates.add(cb.between(root.get("createTime"), item.getCreateTimeStart(), item.getCreateTimeEnd()));
			} else if (item.getCreateTimeStart() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), item.getCreateTimeStart()));
			} else if (item.getCreateTimeEnd() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), item.getCreateTimeEnd()));
			}
			
			if (StringUtils.hasText(item.getProductSKU()) || StringUtils.hasText(item.getProductName())) {
				Subquery<Product> productSubquery = query.subquery(Product.class);
				Root<Product> productRoot = productSubquery.from(Product.class);
				productSubquery.select(productRoot.get("id"));
				if (StringUtils.hasText(item.getProductSKU())) {
					productSubquery.where(cb.like(productRoot.get("sku"), "%" + item.getProductSKU() + "%"));
				}
				if (StringUtils.hasText(item.getProductName())) {
					productSubquery.where(cb.like(productRoot.get("name"), "%" + item.getProductName() + "%"));
				}
				predicates.add(cb.in(root.get("productId")).value(productSubquery));
			}
			if (item.getWarehouseIds() != null && item.getWarehouseIds().length > 0) {
				predicates.add(cb.in(root.get("warehouseId")).value(item.getWarehouseIds()));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
}
