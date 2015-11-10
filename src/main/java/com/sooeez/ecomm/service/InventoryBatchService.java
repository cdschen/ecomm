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
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.domain.WarehousePosition;
import com.sooeez.ecomm.dto.InventoryProductDetailDTO;
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
		for (InventoryBatch batch : batches) {
			saveInventoryBatch(batch);
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
			List<Product> products = this.refreshInventoryWhenOneWarehouse(inventories);
			
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
						WarehousePosition position = product.getPositions().get(product.getPositions().size()-1);
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
								snapshot += "{\"" + position.getName() + "\":" + (position.getTotal() + item.getChangedQuantity().longValue()) + "},";
								existPosition = true;
							} else {
								snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "},";
							}
						}
						position = product.getPositions().get(product.getPositions().size()-1);
						boolean hasEle = false;
						if (position.getId().longValue() == item.getPosition().getId().longValue()) {
							existPosition = true;
							snapshot += "{\"" + position.getName() + "\":" + (position.getTotal() + item.getChangedQuantity().longValue()) + "}";
						} else {
							hasEle = true;
							snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "}";
						}
						if (!existPosition) {
							if (hasEle) {
								snapshot += ",";
							}
							snapshot += "{\"" + item.getPosition().getName() + "\":" + item.getChangedQuantity() + "}";
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
					    + "\"" + item.getPosition().getName() + "\":" + item.getChangedQuantity()
					  + "}]"
					+ "}"
				  + "}";
					item.setInventorySnapshot(snapshot);
				}
			}
			
			batchRepository.save(batch);
			
			/* 如果时从收货单入库，则更新收货单状态为：已入库 */
			if( batch.getReceiveId() != null )
			{
				this.purchaseOrderDeliveryRepository.updateStatus( 2 ,  batch.getReceiveId() );
			}

			batch.getItems().forEach(item -> {

				Inventory inventory = new Inventory();
				inventory.setProduct(item.getProduct());
				inventory.setWarehouseId(item.getWarehouse().getId());
				inventory.setPosition(item.getPosition());
				inventory.setInventoryBatchId(batch.getId());
				inventory.setQuantity(item.getChangedQuantity());
				inventory.setExpireDate(item.getExpireDate());

				this.inventoryRepository.save(inventory);
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
				List<Product> products = this.refreshInventoryWhenOneWarehouse(inventories);

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
							WarehousePosition position = product.getPositions().get(product.getPositions().size()-1);
							snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "}";
					snapshot += "]"
						    + "},"
						    + "\"after\":{"
						      + "\"total\":" + (product.getTotal().longValue() + item.getActualQuantity().longValue()) + ","
						      + "\"positions\":[";
							for (int i = 0, len = product.getPositions().size() - 1; i < len; i++) {
								position = product.getPositions().get(i);
								if (position.getId().longValue() == item.getPosition().getId().longValue()) {
									snapshot += "{\"" + position.getName() + "\":" + (position.getTotal() + item.getActualQuantity().longValue()) + "},";
								} else {
									snapshot += "{\"" + position.getName() + "\":" + position.getTotal() + "},";
								}
							}
							position = product.getPositions().get(product.getPositions().size()-1);
							if (position.getId().longValue() == item.getPosition().getId().longValue()) {
								snapshot += "{\"" + position.getName() + "\":" + (position.getTotal() + item.getChangedQuantity().longValue()) + "}";
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
						if (item.getPosition() != null && item.getPosition().getId() != null) {
							this.inventoryRepository.updateInventoryByProductIdAndWarehouseIdAndPositionIdAndBatchId(item.getActualQuantity(), item.getProduct().getId(), item.getWarehouse().getId(), item.getPosition().getId(), item.getOutBatch().getId());
						} else {
							this.inventoryRepository.updateInventoryByProductIdAndWarehouseIdAndBatchId(item.getActualQuantity(), item.getProduct().getId(), item.getWarehouse().getId(), item.getOutBatch().getId());
						}
					} else {
						System.out.println("没有修改库存的item:" + item.getId());
					}
				});
				
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
						if (warehouse.getId() == inventory.getWarehouseId()) {
							warehouse.setTotal(warehouse.getTotal().longValue() + inventory.getQuantity().longValue());
							existWarehouse = true;
							break;
						}
					}
					if (!existWarehouse) {
						Warehouse warehouse = new Warehouse();
						warehouse.setId(inventory.getWarehouseId());
						warehouse.setTotal(inventory.getQuantity());
						product.getWarehouses().add(warehouse);
					}
					break;
				}
			}
			if (!existProduct) {
				Warehouse warehouse = new Warehouse();
				warehouse.setId(inventory.getWarehouseId());
				warehouse.setTotal(inventory.getQuantity());
				inventory.getProduct().getWarehouses().add(warehouse);
				products.add(inventory.getProduct());
			}
		}
		return products;
	}
	
	// 当只有一个仓库的时候，计算库存中商品的库存
	public List<Product> refreshInventoryWhenOneWarehouse(List<Inventory> inventories) {
		List<Product> products = new ArrayList<>();
		for (Inventory inventory: inventories) {
			boolean existInventory = false;
			for (Product product: products) {
				if (product.getSku().equals(inventory.getProduct().getSku())) {
					System.out.println("已在产品数组中匹配到相同的产品");
					if (inventory.getPosition() != null) {
						boolean existPosition = false;
						for (WarehousePosition position: product.getPositions()) {
							if (position.getId().longValue() == inventory.getPosition().getId().longValue()) {
								boolean existPositionBatch = false;
								for (InventoryBatch batch: position.getBatches()) {
									if (batch.getId().longValue() == inventory.getInventoryBatchId().longValue()) {
										batch.setTotal(batch.getTotal().longValue() + inventory.getQuantity().longValue());
										existPositionBatch = true;
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
								existPosition = true;
								break;
							}
						}
						if (!existPosition) {
							inventory.getPosition().setTotal(inventory.getQuantity().longValue());
							InventoryBatch batch = new InventoryBatch();
							batch.setId(inventory.getInventoryBatchId());
							batch.setTotal(inventory.getQuantity().longValue());
							inventory.getPosition().getBatches().add(batch);
							product.getPositions().add(inventory.getPosition());
						}
					}
					InventoryProductDetailDTO detail = new InventoryProductDetailDTO();
					detail.setPosition(inventory.getPosition());
					detail.setQuantity(inventory.getQuantity().longValue());
					detail.setExpireDate(inventory.getExpireDate());
					detail.setBatchId(inventory.getInventoryBatchId());
					product.setTotal(product.getTotal().longValue() + inventory.getQuantity().longValue());
					boolean existBatch = false;
					for (InventoryBatch batch: product.getBatches()) {
						if (batch.getId().longValue() == inventory.getInventoryBatchId()) {
							batch.setTotal(batch.getTotal().longValue() + inventory.getQuantity().longValue());
							existBatch = true;
							break;
						}
					}
					if (!existBatch) {
						InventoryBatch batch = new InventoryBatch();
						batch.setId(inventory.getInventoryBatchId());
						batch.setTotal(inventory.getQuantity().longValue());
						product.getBatches().add(batch);
					}
					product.getDetails().add(detail);
					inventory.getProduct().getDetails().add(detail);
					existInventory = true;
					break;
				}
			}
			if (!existInventory) {
				// 判断inventory记录有没有库位
				WarehousePosition position = null;
				if (inventory.getPosition() != null) {
					position = new WarehousePosition();
					BeanUtils.copyProperties(inventory.getPosition(), position);
					position.setBatches(new ArrayList<>());
					
					position.setTotal(inventory.getQuantity().longValue());
					InventoryBatch batch = new InventoryBatch();
					batch.setId(inventory.getInventoryBatchId());
					batch.setTotal(inventory.getQuantity().longValue());
					position.getBatches().add(batch);
					inventory.getProduct().getPositions().add(position);
				}
				InventoryProductDetailDTO detail = new InventoryProductDetailDTO();
				detail.setPosition(position);
				detail.setQuantity(inventory.getQuantity().longValue());
				detail.setExpireDate(inventory.getExpireDate());
				detail.setBatchId(inventory.getInventoryBatchId());
				inventory.getProduct().getDetails().add(detail);
				inventory.getProduct().setTotal(inventory.getQuantity().longValue());
				InventoryBatch batch = new InventoryBatch();
				batch.setId(inventory.getInventoryBatchId());
				batch.setTotal(inventory.getQuantity().longValue());
				inventory.getProduct().getBatches().add(batch);
				products.add(inventory.getProduct());
//				System.out.println(inventory.getProduct().getName() 
//						+ "," + inventory.getProduct().getPositions().size()
//						+ "," + inventory.getProduct().getPositions().get(0).getName()
//						+ "," + inventory.getProduct().getPositions().get(0).getTotal());
				
			}
		}
		this.console(products);
		return products;
	}

	// 创建生成出库单
	@Transactional
	public OperationReviewDTO createOutInventorySheet(OperationReviewDTO review) {
		
		this.orderService.confirmOrderWhenGenerateOutInventory(review);
		
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
			Inventory inventoryQuery = new Inventory();
			inventoryQuery = this.orderService.collectWarehouseIds(review.getOrders());
			// 查询库存
			List<Inventory> inventories = inventoryService.getInventories(inventoryQuery, new Sort(Sort.Direction.ASC, "productId", "inventoryBatchId"));
			List<Product> products = this.refreshInventoryWhenOneWarehouse(inventories);
			Long warehouseId = inventoryQuery.getWarehouseIds().get(0);
			Long userId = Long.parseLong(review.getDataMap().get("userId").toString());
			
			//if (true) return review;
			//this.console(products);
			
			InventoryBatch inventoryBatch = new InventoryBatch();
			inventoryBatch.getWarehouse().setId(warehouseId);
			inventoryBatch.setOperate(2); // 出库
			inventoryBatch.getUser().setId(userId);
			inventoryBatch.setExecuteOperator(null);
			inventoryBatch.setOperateTime(new Date()); // 操作的时间
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
								if (product.getSku().equals(item.getProduct().getSku())) {
									/* 
									 * 很有可能，当前这个仓库没有库位 
									 * */
									// 判断当前产品是否有库位
									if (product.getPositions() != null && product.getPositions().size() > 0) {
										long temp = item.getQtyOrdered().longValue();
										boolean done = false;
										// 循环这个库存商品下的批次
										for (InventoryBatch outBatch: product.getBatches()) {
											
											// 循环这个库存商品下的库位
											boolean exitPositionEach = false;
											for (WarehousePosition position: product.getPositions()) {
												// 循环每一个库位下的批次
												for (InventoryBatch batch: position.getBatches()) {
													
													if (batch.getId().longValue() == outBatch.getId().longValue()) {
														if (batch.getTotal().longValue() - temp < 0) {
															
															System.out.println("batch item C");
															
															InventoryBatchItem batchItem = new InventoryBatchItem();
															batchItem.getProduct().setId(product.getId());
															batchItem.getWarehouse().setId(warehouseId);
															batchItem.getPosition().setId(position.getId());
															batchItem.getUser().setId(userId);
															batchItem.setExecuteOperator(null);
															batchItem.getOutBatch().setId(batch.getId());
															batchItem.setChangedQuantity(-batch.getTotal().longValue());
															inventoryBatch.getItems().add(batchItem);
															
															position.setTotal(position.getTotal().longValue() - batch.getTotal().longValue());
															temp = temp - batch.getTotal().longValue();
															batch.setTotal(0l);
														} else {
															
															System.out.println("batch item D");
															
															InventoryBatchItem batchItem = new InventoryBatchItem();
															batchItem.getProduct().setId(product.getId());
															batchItem.getWarehouse().setId(warehouseId);
															batchItem.getPosition().setId(position.getId());
															batchItem.getUser().setId(userId);
															batchItem.setExecuteOperator(null);
															batchItem.getOutBatch().setId(batch.getId());
															batchItem.setChangedQuantity(-temp);
															inventoryBatch.getItems().add(batchItem);
															
															batch.setTotal(batch.getTotal().longValue() - temp);
															position.setTotal(position.getTotal().longValue() - temp);
															exitPositionEach = true;
															done = true;
														}
														break;
													}
												}
												if (exitPositionEach) {
													break;
												}
											}
											if (done) {
												break;
											}
										}
										break;
									} else {
										long temp = item.getQtyOrdered().longValue();
										product.setTotal(product.getTotal().longValue() - temp);
										// 循环库存商品下的批次
										for (InventoryBatch batch: product.getBatches()) {
											if (batch.getTotal().longValue() - temp < 0) {
												
												System.out.println("batch item A");
												
												InventoryBatchItem batchItem = new InventoryBatchItem();
												batchItem.getProduct().setId(product.getId());
												batchItem.getWarehouse().setId(warehouseId);
												batchItem.setPosition(null);
												batchItem.getUser().setId(userId);
												batchItem.setExecuteOperator(null);
												batchItem.getOutBatch().setId(batch.getId());
												batchItem.setChangedQuantity(-batch.getTotal().longValue());
												inventoryBatch.getItems().add(batchItem);
												
												temp = temp - batch.getTotal().longValue();
												batch.setTotal(0l);
												
											} else {
												
												System.out.println("batch item B");
												
												InventoryBatchItem batchItem = new InventoryBatchItem();
												batchItem.getProduct().setId(product.getId());
												batchItem.getWarehouse().setId(warehouseId);
												batchItem.setPosition(null);
												batchItem.getUser().setId(userId);
												batchItem.setExecuteOperator(null);
												batchItem.getOutBatch().setId(batch.getId());
												batchItem.setChangedQuantity(-temp);
												inventoryBatch.getItems().add(batchItem);
												
												batch.setTotal(batch.getTotal().longValue() - temp);
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			// 执行，数据库，生成出库单数据
			this.saveInventoryBatch(inventoryBatch);
			
		}
		
		return review;
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
}
