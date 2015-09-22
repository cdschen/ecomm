package com.sooeez.ecomm.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.sooeez.ecomm.domain.Inventory;
import com.sooeez.ecomm.domain.InventoryBatch;
import com.sooeez.ecomm.domain.InventoryBatchItem;
import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.domain.Order;
import com.sooeez.ecomm.domain.OrderBatch;
import com.sooeez.ecomm.domain.OrderItem;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.domain.WarehousePosition;
import com.sooeez.ecomm.dto.InventoryProductDetailDTO;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.dto.ProductDTO;
import com.sooeez.ecomm.repository.InventoryBatchRepository;
import com.sooeez.ecomm.repository.InventoryRepository;
import com.sooeez.ecomm.repository.WarehousePositionRepository;
import com.sooeez.ecomm.repository.WarehouseRepository;

@Service
public class InventoryService {

	// Repository
	@Autowired private WarehouseRepository warehouseRepository;
	@Autowired private WarehousePositionRepository warehousePositionRepository;
	@Autowired private InventoryRepository inventoryRepository;
	@Autowired private InventoryBatchRepository inventoryBatchRepository;
	
	// Service
	@Autowired private OrderService orderService;

	/*
	 * Warehouse
	 */

	@Transactional
	public Warehouse saveWarehouse(Warehouse warehouse) {
		return this.warehouseRepository.save(warehouse);
	}

	@Transactional
	public void deleteWarehouse(Long id) {
		this.warehouseRepository.delete(id);
	}

	public Warehouse getWarehouse(Long id) {
		return this.warehouseRepository.findOne(id);
	}

	public List<Warehouse> getWarehouses(Warehouse warehouse, Sort sort) {
		return this.warehouseRepository.findAll(getWarehouseSpecification(warehouse), sort);
	}

	public Page<Warehouse> getPagedWarehouses(Warehouse warehouse, Pageable pageable) {
		return this.warehouseRepository.findAll(getWarehouseSpecification(warehouse), pageable);
	}
	
	private Specification<Warehouse> getWarehouseSpecification(Warehouse warehouse) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("deleted"), warehouse.getDeleted() != null && warehouse.getDeleted() == true ? true : false));
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	/*
	 * WarehousePosition
	 */

	@Transactional
	public WarehousePosition saveWarehousePosition(WarehousePosition warehousePosition) {
		return this.warehousePositionRepository.save(warehousePosition);
	}

	@Transactional
	public List<WarehousePosition> saveWarehousePositions(List<WarehousePosition> positions) {
		return this.warehousePositionRepository.save(positions);
	}

	@Transactional
	public void deleteWarehousePosition(Long id) {
		this.warehousePositionRepository.delete(id);
	}

	public WarehousePosition getWarehousePosition(Long id) {
		return this.warehousePositionRepository.findOne(id);
	}

	public List<WarehousePosition> getWarehousePositions() {
		return this.warehousePositionRepository.findAll();
	}

	public Page<WarehousePosition> getPagedWarehousePositions(Pageable pageable) {
		return this.warehousePositionRepository.findAll(pageable);
	}

	/*
	 * Inventory
	 */

	@Transactional
	public Inventory saveInventory(Inventory inventory) {
		return this.inventoryRepository.save(inventory);
	}

	@Transactional
	public void deleteInventory(Long id) {
		this.inventoryRepository.delete(id);
	}

	public Inventory getInventory(Long id) {
		return this.inventoryRepository.findOne(id);
	}

	public List<Inventory> getInventories(Inventory inventory, Sort sort) {
		return this.inventoryRepository.findAll(getInventorySpecification(inventory), sort);
	}

	public Page<Inventory> getPagedInventories(Pageable pageable) {
		return this.inventoryRepository.findAll(pageable);
	}
	
	private Specification<Inventory> getInventorySpecification(Inventory inventory) {
		
		return (root, query, cb) -> {
			
			List<Predicate> predicates = new ArrayList<>();
			
			if (inventory.getId() != null) {
				predicates.add(cb.equal(root.get("id"), inventory.getId()));
			}
			if (inventory.getWarehouseId() != null) {
				predicates.add(cb.equal(root.get("warehouseId"), inventory.getWarehouseId()));
			}
			if (inventory.getWarehouseIds() != null && inventory.getWarehouseIds().size() > 0) {
				predicates.add(cb.in(root.get("warehouseId")).value(inventory.getWarehouseIds()));
			}
			
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	/*
	 * InventoryBatch
	 */

	@Transactional
	public InventoryBatch saveInventoryBatch(InventoryBatch batch) {
		
		batch.setOperateTime(new Date(System.currentTimeMillis()));

		if (batch.getOperate() == 1) { // 正常入库
			
			this.inventoryBatchRepository.save(batch);

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

		} else if (batch.getOperate() == 2) { // 正常出库
			
			this.inventoryBatchRepository.save(batch);


		} else if (batch.getOperate() == 3) { // 调整入库

		} else if (batch.getOperate() == 4) { // 调整出库

		}

		return batch;
	}

	@Transactional
	public void deleteInventoryBatch(Long id) {
		this.inventoryBatchRepository.delete(id);
	}

	public InventoryBatch getInventoryBatch(Long id) {
		return this.inventoryBatchRepository.findOne(id);
	}

	public List<InventoryBatch> getInventoryBatches(InventoryBatch batch, Sort sort) {
		return this.inventoryBatchRepository.findAll(getInventoryBatchSpecification(batch), sort);
	}

	public Page<InventoryBatch> getPagedInventoryBatches(InventoryBatch batch, Pageable pageable) {
		return this.inventoryBatchRepository.findAll(getInventoryBatchSpecification(batch), pageable);
	}
	
	private Specification<InventoryBatch> getInventoryBatchSpecification(InventoryBatch batch) {
		
		return (root, query, cb) -> {
			
			List<Predicate> predicates = new ArrayList<>();
			
			if (batch.getId() != null) {
				predicates.add(cb.equal(root.get("id"), batch.getId()));
			}
			if (batch.getWarehouseId() != null) {
				predicates.add(cb.equal(root.get("warehouseId"), batch.getWarehouseId()));
			}
			if (batch.getOperateTimeStart() != null && batch.getOperateTimeEnd() != null) {
				predicates.add(cb.between(root.get("operateTime"), batch.getOperateTimeStart(), batch.getOperateTimeEnd()));
			}
			if (batch.getType() != null) {
				predicates.add(cb.equal(root.get("type"), batch.getType()));
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
					if (inventory.getPosition() != null) {
						boolean existPosition = false;
						for (WarehousePosition position: product.getPositions()) {
							if (position.getId().longValue() == inventory.getInventoryBatchId().longValue()) {
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
				if (inventory.getPosition() != null) {
					inventory.getPosition().setTotal(inventory.getQuantity().longValue());
					InventoryBatch batch = new InventoryBatch();
					batch.setId(inventory.getInventoryBatchId());
					batch.setTotal(inventory.getQuantity().longValue());
					inventory.getProduct().getPositions().add(inventory.getPosition());
				}
				InventoryProductDetailDTO detail = new InventoryProductDetailDTO();
				detail.setPosition(inventory.getPosition());
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
			}
		}
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
			List<Inventory> inventories = this.getInventories(inventoryQuery, new Sort(Sort.Direction.ASC, "productId", "inventoryBatchId"));
			List<Product> products = this.refreshInventoryWhenOneWarehouse(inventories);
			Long warehouseId = inventoryQuery.getWarehouseIds().get(0);
			Long userId = Long.parseLong(review.getDataMap().get("userId").toString());
			
			this.console(products);
			
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
															batchItem.setChangedQuantity(batch.getTotal().longValue());
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
															batchItem.setChangedQuantity(temp);
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
												batchItem.setChangedQuantity(batch.getTotal().longValue());
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
												batchItem.setChangedQuantity(temp);
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
