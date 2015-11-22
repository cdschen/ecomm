package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.Inventory;
import com.sooeez.ecomm.domain.InventoryBatch;
import com.sooeez.ecomm.domain.InventoryBatchItem;
import com.sooeez.ecomm.domain.Order;
import com.sooeez.ecomm.domain.OrderBatch;
import com.sooeez.ecomm.domain.OrderItem;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.PurchaseOrderDelivery;
import com.sooeez.ecomm.domain.PurchaseOrderDeliveryItem;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.domain.WarehousePosition;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.repository.InventoryBatchRepository;
import com.sooeez.ecomm.repository.InventoryRepository;
import com.sooeez.ecomm.repository.PurchaseOrderDeliveryRepository;
import com.sooeez.ecomm.repository.WarehousePositionRepository;
import com.sooeez.ecomm.repository.WarehouseRepository;

@Service
public class InventoryBatchService {

	/*
	 * Repository
	 */
	
	@Autowired 
	private WarehouseRepository warehouseRepository;
	
	@Autowired 
	private WarehousePositionRepository positionRepository;
	
	@Autowired 
	private InventoryRepository inventoryRepository;
	
	@Autowired 
	private InventoryBatchRepository batchRepository;
	
	@Autowired 
	private PurchaseOrderDeliveryRepository purchaseOrderDeliveryRepository;
	
	/*
	 * Service
	 */
	
	@Autowired 
	private OrderService orderService;
	
	@Autowired
	private InventoryService inventoryService;
	
	/*
	 * InventoryBatch
	 */
	
	@Transactional
	public List<InventoryBatch> saveInventoryBatches(List<InventoryBatch> batches) {
		Long receiveId = null;
		for (InventoryBatch batch : batches) {
			saveInventoryBatch(batch);
			receiveId = batch.getReceiveId();
		}
		
		if (receiveId != null) {
			
			PurchaseOrderDelivery receive = purchaseOrderDeliveryRepository.findOne(receiveId);
			long enterableQty = 0;
            long enteredQty = 0;
			for (PurchaseOrderDeliveryItem item : receive.getItems()) {
				enterableQty += item.getReceiveQty().longValue();
			}
			for (InventoryBatch batch: receive.getBatches()) {
				for (InventoryBatchItem item : batch.getItems()) {
					if (item.getChangedQuantity() != null) {
						enteredQty += item.getChangedQuantity().longValue();
					}
				}
			}
			
			if (enterableQty == enteredQty) {
				purchaseOrderDeliveryRepository.updateStatus(2, receiveId);
			} else {
				purchaseOrderDeliveryRepository.updateStatus(4, receiveId);
			}
		}
		return batches;
	}

	@Transactional
	public InventoryBatch saveInventoryBatch(InventoryBatch batch) {
		
		if (batch.getOperate().intValue() == 1) { // 正常入库并增加库存
			
			batch.setOperateTime(new Date(System.currentTimeMillis()));
			
			Inventory inventoryQuery = new Inventory();
			inventoryQuery.setWarehouseId(batch.getWarehouse().getId());
			
			List<Inventory> inventories = inventoryService.getInventories(inventoryQuery, new Sort(Sort.Direction.ASC, "productId", "inventoryBatchId"));
			System.out.println("batch.getWarehouse().getId():" + batch.getWarehouse().getId());
			List<Product> products = refreshInventoryWhenOneWarehouse(inventories);
			
			for (InventoryBatchItem item: batch.getItems()) {
				
				item.setBatchOperate(batch.getOperate());
				item.setCreateTime(batch.getOperateTime());
				
				// 入库的时候判断，入库的商品是否存在库存
				boolean existProduct = false;
				for (Product product: products) {
					if (item.getProduct().getId().longValue() == product.getId().longValue()) {
						existProduct = true;
						String snapshot = 
						"{"
					    + "\"before\":{"
						  + "\"total\":" + product.getTotal().longValue() + ","
						  + "\"positions\":[";
						for (int i = 0, len = product.getPositions().size() - 1; i < len; i++) {
							WarehousePosition position = product.getPositions().get(i);
							snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "},";
						}
						WarehousePosition position = product.getPositions().get(product.getPositions().size() - 1);
						snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "}";
				snapshot += "]"
					    + "},"
					    + "\"after\":{"
					      + "\"total\":" + (product.getTotal().longValue() + item.getChangedQuantity().longValue()) + ","
					      + "\"positions\":[";
						boolean existPosition = false;
						for (int i = 0, len = product.getPositions().size() - 1; i < len; i++) {
							position = product.getPositions().get(i);
							if (position.getId().longValue() == item.getPosition().getId().longValue()) {
								position.setTotal(position.getTotal().longValue() + item.getChangedQuantity().longValue());
								product.setTotal(product.getTotal().longValue() + item.getChangedQuantity().longValue());
								snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "},";
								existPosition = true;
							} else {
								snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "},";
							}
						}
						position = product.getPositions().get(product.getPositions().size() - 1);
						boolean hasEle = false;
						if (position.getId().longValue() == item.getPosition().getId().longValue()) {
							existPosition = true;
							position.setTotal(position.getTotal().longValue() + item.getChangedQuantity().longValue());
							product.setTotal(product.getTotal().longValue() + item.getChangedQuantity().longValue());
							snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "}";
						} else {
							hasEle = true;
							snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "}";
						}
						if (!existPosition) {
							if (hasEle) {
								snapshot += ",";
							}
							snapshot += "{\"" + item.getPosition().getName() + "\":" + item.getChangedQuantity() + "}";
							
							product.setTotal(product.getTotal().longValue() + item.getChangedQuantity().longValue());
							item.getPosition().setTotal(item.getChangedQuantity().longValue());
							product.getPositions().add(item.getPosition());
							
						}
				snapshot += "]"
						+ "}"
					  + "}";
						
						item.setInventorySnapshot(snapshot);
						break;
					}
				}
				// 如果入库的商品不存在库存
				if (!existProduct) { 
					
					String snapshot = 
					"{"
					+ "\"before\": null,"
					+ "\"after\":{"
					  + "\"total\":" + item.getChangedQuantity().longValue() + ","
					  + "\"positions\":[{"
					    + "\"" + item.getPosition().getName() + "\":" + item.getChangedQuantity().longValue()
					  + "}]"
					+ "}"
				  + "}";
					
					item.setInventorySnapshot(snapshot);
					
					Product product = new Product();
					product.setId(item.getProduct().getId().longValue());
					product.setTotal(item.getChangedQuantity().longValue());
					
					WarehousePosition position = new WarehousePosition();
					position.setId(item.getPosition().getId().longValue());
					position.setName(item.getPosition().getName());
					position.setTotal(item.getChangedQuantity().longValue());
					
					product.getPositions().add(position);
					products.add(product);
					
				}
			}
			
			batchRepository.save(batch);

			batch.getItems().forEach(item -> {

				Inventory inventory = new Inventory();
				inventory.setProduct(item.getProduct());
				inventory.setWarehouseId(item.getWarehouse().getId());
				inventory.setWarehouse(item.getWarehouse());
				inventory.setPosition(item.getPosition());
				inventory.setInventoryBatchId(batch.getId());
				inventory.setBatch(batch);
				inventory.setQuantity(item.getChangedQuantity());
				inventory.setExpireDate(item.getExpireDate());

				inventoryRepository.save(inventory);
			});

		} else if (batch.getOperate().intValue() == 2) { // 正常或修改出库单
			
			batch.setOperateTime(new Date(System.currentTimeMillis()));
			
			// batch状态为待完成时
			if (batch.getType().intValue() == 1) {

				List<InventoryBatchItem> batchItems = batch.getItems();

				for (InventoryBatchItem item : batchItems) {
					
					item.setCreateTime(batch.getOperateTime());
					item.setBatchType(batch.getType());
					item.setBatchOperate(batch.getOperate());

				}
			}
			
			if (batch.getType().intValue() == 2) { // 当出库单状态为已完成的时候，修改库存
				
				batch.setOutInventoryTime(new Date(System.currentTimeMillis()));
				Inventory inventoryQuery = new Inventory();
				inventoryQuery.setWarehouseId(batch.getWarehouse().getId());
				
				List<Inventory> inventories = inventoryService.getInventories(inventoryQuery, new Sort(Sort.Direction.ASC, "productId", "inventoryBatchId"));
				List<Product> products = refreshInventoryWhenOneWarehouse(inventories);

				List<InventoryBatchItem> batchItems = batch.getItems();

				for (InventoryBatchItem item : batchItems) {

					item.setBatchType(batch.getType());
					item.setLastTime(batch.getOutInventoryTime());
					item.setExecuteOperator(batch.getExecuteOperator());
					
					for (Product product: products) {
						// 匹配到商品
						if (product.getId().longValue() == item.getProduct().getId().longValue()) {
							String snapshot = 
							"{"
						    + "\"before\":{"
							  + "\"total\":" + product.getTotal().longValue() + ","
							  + "\"positions\":[";
							for (int i = 0, len = product.getPositions().size() - 1; i < len; i++) {
								WarehousePosition position = product.getPositions().get(i);
								snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "},";
							}
							WarehousePosition position = product.getPositions().get(product.getPositions().size() - 1);
							snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "}";
					snapshot += "]"
						    + "},"
						    + "\"after\":{"
						      + "\"total\":" + (product.getTotal().longValue() + item.getActualQuantity().longValue()) + ","
						      + "\"positions\":[";
							for (int i = 0, len = product.getPositions().size() - 1; i < len; i++) {
								position = product.getPositions().get(i);
								if (position.getId().longValue() == item.getPosition().getId().longValue()) {
									position.setTotal(position.getTotal().longValue() + item.getActualQuantity().longValue());
									product.setTotal(product.getTotal().longValue() + item.getActualQuantity().longValue());
									snapshot += "{\"" + position.getName() + "\":" + position.getTotal().longValue() + "},";
								} else {
									snapshot += "{\"" + position.getName() + "\":" + position.getTotal().longValue() + "},";
								}
							}
							position = product.getPositions().get(product.getPositions().size() - 1);
							if (position.getId().longValue() == item.getPosition().getId().longValue()) {
								position.setTotal(position.getTotal().longValue() + item.getChangedQuantity().longValue());
								product.setTotal(product.getTotal().longValue() + item.getChangedQuantity().longValue());
								snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "}";
							} else {
								snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "}";
							}
							
					snapshot += "]"
							+ "}"
						  + "}";
							
							item.setInventorySnapshot(snapshot);
						}
					}

				}
			}
			
			batchRepository.save(batch);
			
			if (batch.getType().intValue() == 2) { // 当出库单状态为已完成的时候，修改库存
				
				List<InventoryBatchItem> batchItems = batch.getItems();
				
				batchItems.forEach(item -> {
					if (item.getProduct().getId() != null && item.getWarehouse().getId() != null && item.getOutBatch().getId() != null) {
						inventoryRepository.updateInventoryByProductIdAndWarehouseIdAndPositionIdAndBatchId(item.getActualQuantity(), item.getProduct().getId(), item.getWarehouse().getId(), item.getPosition().getId(), item.getOutBatch().getId());
					} else {
						System.out.println("没有修改库存的item:" + item.getId());
					}
				});
				
				// 最后每次出库，都删除库存为0的记录
				inventoryRepository.deleteByQuantity();
				
			}
			
		} else if (batch.getOperate() == 3) { // 调整入库

		} else if (batch.getOperate() == 4) { // 调整出库

		}

		return batch;
	}
	
	@Transactional
	public InventoryBatch completeOutInventorySheetAndCalculateInventory (InventoryBatch batch) {
		return batch;
	}
	
	@Transactional
	public void deleteBatch(Long id) {
		batchRepository.delete(id);
	}

	public InventoryBatch getBatch(Long id) {
		return batchRepository.findOne(id);
	}

	public List<InventoryBatch> getBatches(InventoryBatch batch, Sort sort) {
		return batchRepository.findAll(getBatchSpecification(batch), sort);
	}

	public Page<InventoryBatch> getPagedBatches(InventoryBatch batch, Pageable pageable) {
		return batchRepository.findAll(getBatchSpecification(batch), pageable);
	}
	
	private Specification<InventoryBatch> getBatchSpecification(InventoryBatch batch) {
		
		return (root, query, cb) -> {
			
			List<Predicate> predicates = new ArrayList<>();
			
			if (batch.getId() != null) {
				predicates.add(cb.equal(root.get("id"), batch.getId()));
			}
			if (batch.getWarehouseId() != null) {
				predicates.add(cb.equal(root.get("warehouseId"), batch.getWarehouseId()));
			}
			if (batch.getWarehouseIds() != null && batch.getWarehouseIds().length > 0) {
				predicates.add(root.get("warehouseId").in((Object[]) batch.getWarehouseIds()));
			}
			if (batch.getOperateTimeStart() != null && batch.getOperateTimeEnd() != null) {
				predicates.add(cb.between(root.get("operateTime"), batch.getOperateTimeStart(), batch.getOperateTimeEnd()));
			} else if (batch.getOperateTimeStart() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("operateTime"), batch.getOperateTimeStart()));
			} else if (batch.getOperateTimeEnd() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("operateTime"), batch.getOperateTimeEnd()));
			}
			if (batch.getOutInventoryTimeStart() != null && batch.getOutInventoryTimeEnd() != null) {
				predicates.add(cb.between(root.get("outInventoryTime"), batch.getOutInventoryTimeStart(), batch.getOutInventoryTimeEnd()));
			} else if (batch.getOutInventoryTimeStart() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("outInventoryTime"), batch.getOutInventoryTimeStart()));
			} else if (batch.getOutInventoryTimeEnd() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("outInventoryTime"), batch.getOutInventoryTimeEnd()));
			}
			if (batch.getType() != null) {
				predicates.add(cb.equal(root.get("type"), batch.getType()));
			}
			if (batch.getOperate() != null) {
				predicates.add(cb.equal(root.get("operate"), batch.getOperate()));
			}
			
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
	
	// 计算库存中每个商品的库存
	public List<Product> refreshInventory(List<Inventory> inventories) {
		List<Product> products = new ArrayList<>();
		for (Inventory inventory: inventories) {
			boolean existProduct = false;
			for (Product product: products) {
				if (product.getSku().equals(inventory.getProduct().getSku())) {
					existProduct = true;
					boolean existWarehouse = false;
					for (Warehouse warehouse: product.getWarehouses()) {
						if (warehouse.getId().longValue() == inventory.getWarehouseId().longValue()) {
							warehouse.setTotal(warehouse.getTotal().longValue() + inventory.getQuantity().longValue());
							existWarehouse = true;
							break;
						}
					}
					if (!existWarehouse) {
						Warehouse warehouse = new Warehouse();
						warehouse.setId(inventory.getWarehouseId().longValue());
						warehouse.setTotal(inventory.getQuantity().longValue());
						product.getWarehouses().add(warehouse);
					}
					break;
				}
			}
			if (!existProduct) {
				Warehouse warehouse = new Warehouse();
				warehouse.setId(inventory.getWarehouseId().longValue());
				warehouse.setTotal(inventory.getQuantity().longValue());
				inventory.getProduct().getWarehouses().add(warehouse);
				products.add(inventory.getProduct());
			}
		}
		return products;
	}
	
	// 当只有一个仓库的时候，计算库存中商品的库存
	public List<Product> refreshInventoryWhenOneWarehouse(List<Inventory> inventories) {
		List<Product> products = new ArrayList<>();
		System.out.println("refreshInventoryWhenOneWarehouse(): " + inventories.size());
		for (Inventory inventory: inventories) {
			boolean existInventory = false;
			for (Product product: products) {
				if (product.getId().longValue() == inventory.getProduct().getId().longValue()) {
					//System.out.println("已在产品数组中匹配到相同的产品");
					existInventory = true;
					boolean existPosition = false;
					for (WarehousePosition position: product.getPositions()) {
						if (position.getId().longValue() == inventory.getPosition().getId().longValue()) {
							existPosition = true;
							boolean existPositionBatch = false;
							for (InventoryBatch batch: position.getBatches()) {
								if (batch.getId().longValue() == inventory.getInventoryBatchId().longValue()) {
									existPositionBatch = true;
									batch.setTotal(batch.getTotal().longValue() + inventory.getQuantity().longValue());
									break;
								}
							}
							if (!existPositionBatch) {
								InventoryBatch batch = new InventoryBatch();
								batch.setId(inventory.getInventoryBatchId());
								batch.setTotal(inventory.getQuantity().longValue());
								position.getBatches().add(batch);
							}
							position.setTotal(position.getTotal().longValue() + inventory.getQuantity().longValue());
							break;
						}
					}
					if (!existPosition) {
						WarehousePosition position = new WarehousePosition();
						BeanUtils.copyProperties(inventory.getPosition(), position);
						
						position.setTotal(inventory.getQuantity().longValue());
						InventoryBatch batch = new InventoryBatch();
						batch.setId(inventory.getInventoryBatchId().longValue());
						batch.setTotal(inventory.getQuantity().longValue());
						position.getBatches().add(batch);
						product.getPositions().add(position);
					}
					
//					InventoryProductDetailDTO detail = new InventoryProductDetailDTO();
//					detail.setPosition(inventory.getPosition());
//					detail.setQuantity(inventory.getQuantity().longValue());
//					detail.setExpireDate(inventory.getExpireDate());
//					detail.setBatchId(inventory.getInventoryBatchId());
					product.setTotal(product.getTotal().longValue() + inventory.getQuantity().longValue());
					boolean existBatch = false;
					for (InventoryBatch batch: product.getBatches()) {
						if (batch.getId().longValue() == inventory.getInventoryBatchId().longValue()) {
							existBatch = true;
							batch.setTotal(batch.getTotal().longValue() + inventory.getQuantity().longValue());
							break;
						}
					}
					if (!existBatch) {
						InventoryBatch batch = new InventoryBatch();
						batch.setId(inventory.getInventoryBatchId());
						batch.setTotal(inventory.getQuantity().longValue());
						product.getBatches().add(batch);
					}
//					product.getDetails().add(detail);
//					inventory.getProduct().getDetails().add(detail);
					break;
				}
			}
			if (!existInventory) {
				WarehousePosition position = new WarehousePosition();
				BeanUtils.copyProperties(inventory.getPosition(), position);
				position.setBatches(new ArrayList<>());
				position.setTotal(inventory.getQuantity().longValue());
				
				InventoryBatch batch = new InventoryBatch();
				batch.setId(inventory.getInventoryBatchId().longValue());
				batch.setTotal(inventory.getQuantity().longValue());

				position.getBatches().add(batch);
				
				Product product = new Product();
				BeanUtils.copyProperties(inventory.getProduct(), product);
				product.setPositions(new ArrayList<>());
				product.getPositions().add(position);
				
//				InventoryProductDetailDTO detail = new InventoryProductDetailDTO();
//				detail.setPosition(position);
//				detail.setQuantity(inventory.getQuantity().longValue());
//				detail.setExpireDate(inventory.getExpireDate());
//				detail.setBatchId(inventory.getInventoryBatchId().longValue());
//				
//				inventory.getProduct().getDetails().add(detail);
				product.setTotal(inventory.getQuantity().longValue());
				
				batch = new InventoryBatch();
				batch.setId(inventory.getInventoryBatchId());
				batch.setTotal(inventory.getQuantity().longValue());
				
				product.getBatches().add(batch);
				
				products.add(product);
//				System.out.println(inventory.getProduct().getName() 
//						+ "," + inventory.getProduct().getPositions().size()
//						+ "," + inventory.getProduct().getPositions().get(0).getName()
//						+ "," + inventory.getProduct().getPositions().get(0).getTotal());
				
			}
		}
		console(products);
		return products;
	}

	// 创建生成出库单
	@Transactional
	public OperationReviewDTO createOutInventorySheet(OperationReviewDTO review) {
		
		orderService.confirmOrderWhenGenerateOutInventory(review);
		
		System.out.println("differentWarehouseError: " + review.getCheckMap().get("differentWarehouseError"));
		System.out.println("productInventoryNotEnoughError: " + review.getCheckMap().get("productInventoryNotEnoughError"));
		System.out.println("orderExistOutInventorySheetError: " + review.getCheckMap().get("orderExistOutInventorySheetError"));
		
		if (!review.getCheckMap().get("differentWarehouseError") 
				&& (!review.getCheckMap().get("productInventoryNotEnoughError") 
					|| (review.getCheckMap().get("productInventoryNotEnoughError") && review.getIgnoredMap().get("productInventoryNotEnough")))
				&& (!review.getCheckMap().get("orderExistOutInventorySheetError") 
					|| (review.getCheckMap().get("orderExistOutInventorySheetError") && review.getIgnoredMap().get("orderExistOutInventorySheet")))) {
			// 先收集出，需要出库的item都来自那些仓库
			// 在这里正确情况，是只有一个出库的仓库id
			Inventory inventoryQuery = orderService.collectWarehouseIds(review.getOrders());
			
			// 查询库存
			List<Inventory> inventories = inventoryService.getInventories(inventoryQuery, new Sort(Sort.Direction.ASC, "productId", "inventoryBatchId"));
			List<Product> products = refreshInventoryWhenOneWarehouse(inventories);
			Long warehouseId = inventoryQuery.getWarehouseIds().get(0);
			Long userId = Long.parseLong(review.getDataMap().get("userId").toString());
			
			//if (true) return review;
			//console(products);
			
			InventoryBatch inventoryBatch = new InventoryBatch();
			inventoryBatch.getWarehouse().setId(warehouseId);
			inventoryBatch.setOperate(2); // 出库
			inventoryBatch.getUser().setId(userId);
			inventoryBatch.setExecuteOperator(null);
			inventoryBatch.setOperateTime(new Date(System.currentTimeMillis())); // 操作的时间
			inventoryBatch.setType(1); // 待完成
			
			// 开始计算出库细目
			List<Order> orders = review.getOrders();
			for (Order order: orders) {
				if (!order.getIgnoreCheck()) {
					// 每张order在一个仓库下，都和一张出库单绑定
					OrderBatch orderBatch = new OrderBatch();
					orderBatch.setOrderId(order.getId());
					orderBatch.setWarehouseId(warehouseId);
					inventoryBatch.getOrderBatches().add(orderBatch);
					for (OrderItem item: order.getItems()) {
						// 循环库存中的商品
						if (products != null) {
							for (Product product: products) {
								// 一个item匹配到一个库存商品
								if (product.getId().longValue() == item.getProduct().getId().longValue()) {
									long temp = item.getQtyOrdered().longValue();
									boolean done = false;
									// 循环这个库存商品下的批次
									for (InventoryBatch outBatch: product.getBatches()) {
										// 循环这个库存商品下的库位
										boolean exitPositionEach = false;
										for (WarehousePosition position: product.getPositions()) {
											// 循环每一个库位下的批次
											for (InventoryBatch batch: position.getBatches()) {
												long batchTotal = batch.getTotal().longValue();
												long batchId = batch.getId().longValue();
												long outBatchId = outBatch.getId().longValue();
												if (batchId == outBatchId && batchTotal > 0) {
													if (batchTotal - temp < 0) {
														InventoryBatchItem batchItem = new InventoryBatchItem();
														batchItem.getProduct().setId(product.getId());
														batchItem.getWarehouse().setId(warehouseId);
														batchItem.getPosition().setId(position.getId());
														batchItem.getUser().setId(userId);
														batchItem.setExecuteOperator(null);
														batchItem.getOutBatch().setId(batch.getId());
														batchItem.setChangedQuantity(-batchTotal);
														inventoryBatch.getItems().add(batchItem);
														long positionTotal = position.getTotal().longValue();
														position.setTotal(positionTotal - batchTotal);
														temp = temp - batchTotal;
														batch.setTotal(0l);
													} else {
														InventoryBatchItem batchItem = new InventoryBatchItem();
														batchItem.getProduct().setId(product.getId());
														batchItem.getWarehouse().setId(warehouseId);
														batchItem.getPosition().setId(position.getId());
														batchItem.getUser().setId(userId);
														batchItem.setExecuteOperator(null);
														batchItem.getOutBatch().setId(batch.getId());
														batchItem.setChangedQuantity(-temp);
														inventoryBatch.getItems().add(batchItem);
														batch.setTotal(batchTotal - temp);
														long positionTotal = position.getTotal().longValue();
														position.setTotal(positionTotal - temp);
														exitPositionEach = true;
														done = true;
													}
													break;
												} else {
													
												}
											}
											if (exitPositionEach) {
												break;
											}
										}
										boolean nohas = nohasPositionBatchTotal(product.getPositions());
										if (nohas) {
											for (InventoryBatchItem batchItem: inventoryBatch.getItems()) {
												if (batchItem.getProduct().getId().longValue() == item.getProduct().getId().longValue()) {
													//匹配就加上
													batchItem.setChangedQuantity(batchItem.getChangedQuantity() - temp);
													break;
												}
											}
										}
										if (done) {
											break;
										}
									}
									break;
								}
							}
						}
					}
				}
			}
			
			// 执行，数据库，生成出库单数据
			saveInventoryBatch(inventoryBatch);
			
		}
		
		return review;
	}
	
	/*
	 * 判断一个商品所有库位的所有批次的存量都为0的时候
	 */
	
	public boolean nohasPositionBatchTotal(List<WarehousePosition> positions){
		boolean b = true;
		for (WarehousePosition position: positions) {
			for (InventoryBatch batch: position.getBatches()) {
				if (batch.getTotal() > 0) {
					b = false;
					break;
				}
			}
		}
		return b;
	}
	
	public void console(List<Product> products) {
		System.out.print("==============================");
		for (Product product: products) {
			System.out.println("");
			System.out.println("Product: (" + product.getName() + ", " + product.getTotal() + ")");
			System.out.print("Product Positions: ");
			if (product.getPositions() != null) {
				for (WarehousePosition position: product.getPositions()) {
					System.out.print("(" + position.getName() + ", " + position.getTotal() + ")");
					System.out.print("[");
					if (position.getBatches() != null) {
						for (InventoryBatch batch: position.getBatches()) {
							System.out.print("(" + batch.getId() + ", " + batch.getTotal() + ")");
						}
					}
					System.out.print("];");
				}
			}
			System.out.println("");
			System.out.print("Product Batches: ");
			if (product.getBatches() != null) {
				for (InventoryBatch batch: product.getBatches()) {
					System.out.print("(" + batch.getId() + ", " + batch.getTotal() + ")");
				}
			}
		}
		System.out.println("");
		System.out.println("==============================");
	}
	
	
	/*
	 * 计算一个出库单下面的所有item,把他转变成一个商品集合
	 */
	
	public List<Product> refreshBatchItems(InventoryBatch batch) {
		List<Product> products = new ArrayList<>();
		List<InventoryBatchItem> items = batch.getItems();
		batch.setTotal(0L);
		for (InventoryBatchItem item: items) {
			boolean existProduct = false;
			batch.setTotal(batch.getTotal().longValue() + item.getChangedQuantity().longValue());
			
			// 循环商品
			for (Product product: products) {
				if (product.getId().longValue() == item.getProduct().getId().longValue()) {
					existProduct = true;
					boolean existPosition = false;
					
					// 循环当前商品的库位
					for (WarehousePosition position: product.getPositions()) {
						if (position.getId().longValue() == item.getPosition().getId().longValue()) {
							existPosition = true;
							position.setTotal(position.getTotal().longValue() + item.getChangedQuantity().longValue());
							break;
						}
					}
					
					// 如果某库位不在集合中
					if (!existPosition) {
						
						// 创建新库位，复制item的position
						WarehousePosition position = new WarehousePosition();
						BeanUtils.copyProperties(item.getPosition(), position);
						
						// 设置当前库位的应出库数量
						position.setTotal(item.getChangedQuantity().longValue());
						
						// 把库位加入到商品库位集合
						product.getPositions().add(position);
						
					}
					break;
				}
			} 
			
			// 如果某商品不在集合中
			if (!existProduct) {
				
				// 创建新的商品，复制item的product
				Product product = new Product();
				BeanUtils.copyProperties(item.getProduct(), product);
				product.setPositions(new ArrayList<>());
				
				// 创建新库位，复制item的position
				WarehousePosition position = new WarehousePosition();
				BeanUtils.copyProperties(item.getPosition(), position);
				
				// 设置当前库位的应出库数量
				position.setTotal(item.getChangedQuantity().longValue());
				
				// 把库位加入到商品库位集合
				product.getPositions().add(position);
				
				// 把商品加入到商品集合
				products.add(product);
				
			}
			
		}
		
		// 设置查询库存的条件
		Inventory inventoryQuery = new Inventory();
		inventoryQuery.setProductIds(new ArrayList<>());
		inventoryQuery.setWarehouseId(batch.getWarehouse().getId().longValue());
		
		// 再次循环已经整理出来的商品集合
		for (Product product: products) {
			inventoryQuery.getProductIds().add(product.getId());
		}
		
		// 查询出指定仓库和指定商品的库存
		List<Inventory> inventories = inventoryService.getInventories(inventoryQuery, new Sort(Sort.Direction.ASC, "productId", "inventoryBatchId"));
		List<Product> inventoryProducts = refreshInventoryWhenOneWarehouse(inventories);
		
		// 计算出库商品出库后应剩余库存
		for (Product product: products) {
			for (Product inventoryProduct: inventoryProducts) {
				if (inventoryProduct.getId().longValue() == product.getId().longValue()) {
					for (WarehousePosition position: product.getPositions()) {
						for (WarehousePosition inventoryPosition: inventoryProduct.getPositions()) {
							if (inventoryPosition.getId().longValue() == position.getId().longValue()) {
								// 如果匹配到，这个库位出库后应剩余库存 = 当前库位库存 + 一个负数这个库位上应该出库的数量
								System.out.println("正常库存：" + inventoryPosition.getTotal().longValue() + ", 应出数量：" + position.getTotal().longValue());
								position.setStock(inventoryPosition.getTotal().longValue() + position.getTotal().longValue());
							}
						}
					}
				}
			}
		}
		
		return products;
	}
}
