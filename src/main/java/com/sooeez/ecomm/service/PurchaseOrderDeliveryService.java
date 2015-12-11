package com.sooeez.ecomm.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.OrderItem;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.PurchaseOrder;
import com.sooeez.ecomm.domain.PurchaseOrderDelivery;
import com.sooeez.ecomm.domain.PurchaseOrderDeliveryItem;
import com.sooeez.ecomm.domain.PurchaseOrderItem;
import com.sooeez.ecomm.domain.Shipment;
import com.sooeez.ecomm.domain.ShipmentItem;
import com.sooeez.ecomm.domain.Supplier;
import com.sooeez.ecomm.domain.SupplierProduct;
import com.sooeez.ecomm.domain.User;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.repository.PurchaseOrderDeliveryItemRepository;
import com.sooeez.ecomm.repository.PurchaseOrderDeliveryRepository;
import com.sooeez.ecomm.repository.PurchaseOrderRepository;
import com.sooeez.ecomm.repository.SupplierProductRepository;

@Service
public class PurchaseOrderDeliveryService {

	@Autowired PurchaseOrderDeliveryRepository purchaseOrderDeliveryRepository;

	@Autowired PurchaseOrderRepository purchaseOrderRepository;
	
	@Autowired PurchaseOrderDeliveryItemRepository purchaseOrderDeliveryItemRepository;
	
	@Autowired SupplierProductRepository supplierProductRepository;
	
	// Service
	@PersistenceContext private EntityManager em;
	
	/*
	 * Tag
	 */
	
	public PurchaseOrderDelivery savePurchaseOrderDelivery(PurchaseOrderDelivery purchaseOrderDelivery)
	{
		/* 如果编号为空，则时插入操作
		 */
		if( purchaseOrderDelivery.getId() == null )
		{
			purchaseOrderDelivery.setReceiveTime( new Date() );
			purchaseOrderDelivery.setStatus( 1 );	// 初始状态为：待入库
		}
		
		/* 如果有收货详情，则累加采购单需要更新的收货数据，并更新采购单
		 */
		if( purchaseOrderDelivery.getItems() != null && purchaseOrderDelivery.getItems().size() > 0 )
		{
			/* 获取采购单
			 */
			PurchaseOrder purchaseOrder = this.purchaseOrderRepository.findOne( purchaseOrderDelivery.getPurchaseOrderId() );
			purchaseOrder.setLastUpdate( new Date() );
			
			/* 初始化累加所用的［总收货数量］与［总收货金额］
			 */
			BigDecimal totalDeliveredAmount = purchaseOrder.getTotalDeliveredAmount() != null ? purchaseOrder.getTotalDeliveredAmount() : new BigDecimal( 0 );
			Long totalDeliveredQty = purchaseOrder.getTotalDeliveredQty() != null ? purchaseOrder.getTotalDeliveredQty() : 0L;
			
			/* 将收货单详情［实际收货单价］和［收货数量］累加至［总收货数量］和［总收货金额］中
			 */
			for(PurchaseOrderDeliveryItem item : purchaseOrderDelivery.getItems() )
			{
				if( item.getSupplierProduct() != null && ! item.getSupplierProduct().getSupplierProductCode().trim().equals("") )
				{
					String supplierProductSQL = "SELECT * FROM t_supplier_product " +
											 "WHERE supplier_product_code = ?1";
					Query supplierProductQuery = em.createNativeQuery( supplierProductSQL, SupplierProduct.class );
					supplierProductQuery.setParameter( 1, item.getSupplierProduct().getSupplierProductCode() );
					
					/** 如果不存在该［供应商编码］的［供应商产品］
					 */
					if( supplierProductQuery.getResultList().size() < 1 )
					{
						/** 获取该收货单对应的供应商编号
						 */
						User creator = new User();
						creator.setId( purchaseOrderDelivery.getReceiveUser().getId() );
						Supplier supplier = new Supplier();
						supplier.setId( purchaseOrder.getSupplier().getId() );
						item.getSupplierProduct().setSupplier( supplier );
						item.getSupplierProduct().setCreator( creator );
						
						this.supplierProductRepository.save( item.getSupplierProduct() );
					}
					
				}
				
				BigDecimal realPurchaseUnitPrice = item.getRealPurchaseUnitPrice() != null ? item.getRealPurchaseUnitPrice() : new BigDecimal( 0 );
				Long receiveQty = item.getReceiveQty() != null ? item.getReceiveQty() : 0L;
				
				totalDeliveredAmount = totalDeliveredAmount.add( realPurchaseUnitPrice.multiply( new BigDecimal( receiveQty ) ) );
				totalDeliveredQty += receiveQty;
			}

			purchaseOrder.setTotalDeliveredAmount( totalDeliveredAmount );
			purchaseOrder.setTotalDeliveredQty( totalDeliveredQty );

			if
			(
				/* 如果［采购总数量］与［收货总数量］完全匹配，则更新［采购单状态］为：［已收货］
				 */
				purchaseOrder.getTotalPurchasedQty() != null && purchaseOrder.getTotalDeliveredQty() != null &&
				purchaseOrder.getTotalPurchasedQty().equals( purchaseOrder.getTotalDeliveredQty() )
			)
			{
				/* 设置为状态为：已收货 */
				purchaseOrder.setStatus( 2 );
			}
			
			this.purchaseOrderRepository.save( purchaseOrder );
		}
		
		return this.purchaseOrderDeliveryRepository.save(purchaseOrderDelivery);
	}
	
	public void deletePurchaseOrderDelivery(Long id) {
		this.purchaseOrderDeliveryRepository.delete(id);
	}
	
	public PurchaseOrderDelivery getPurchaseOrderDelivery(Long id) {
		return this.purchaseOrderDeliveryRepository.findOne(id);
	}
	
	public List<PurchaseOrderDelivery> getPurchaseOrderDeliverys(PurchaseOrderDelivery purchaseOrderDelivery, Sort sort) {
		return this.purchaseOrderDeliveryRepository.findAll(getPurchaseOrderDeliverySpecification(purchaseOrderDelivery), sort);
	}

	public Page<PurchaseOrderDelivery> getPagedPurchaseOrderDeliverys(PurchaseOrderDelivery purchaseOrderDelivery, Pageable pageable) {
		return this.purchaseOrderDeliveryRepository.findAll(getPurchaseOrderDeliverySpecification(purchaseOrderDelivery), pageable);
	}
	
	private Specification<PurchaseOrderDelivery> getPurchaseOrderDeliverySpecification(PurchaseOrderDelivery purchaseOrderDelivery) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (purchaseOrderDelivery.getQueryPurchaseOrderDeliveryId() != null) {
				predicates.add( cb.equal( root.get("id"), purchaseOrderDelivery.getQueryPurchaseOrderDeliveryId() ) );
			}
			if (purchaseOrderDelivery.getQueryPurchaseOrderId() != null) {
				predicates.add( cb.equal( root.get("purchaseOrderId"), purchaseOrderDelivery.getQueryPurchaseOrderId() ) );
			}
			if (purchaseOrderDelivery.getQueryReceiveTimeStart() != null && purchaseOrderDelivery.getQueryReceiveTimeEnd() != null) {
				try {
					predicates.add(cb.between(root.get("receiveTime"),
							new SimpleDateFormat("yyyy-MM-dd").parse(purchaseOrderDelivery.getQueryReceiveTimeStart()),
							new SimpleDateFormat("yyyy-MM-dd").parse(purchaseOrderDelivery.getQueryReceiveTimeEnd())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (purchaseOrderDelivery.getQueryReceiveTimeStart() != null) {
				try {
					predicates.add(cb.greaterThanOrEqualTo(root.get("receiveTime"), 
							new SimpleDateFormat("yyyy-MM-dd").parse(purchaseOrderDelivery.getQueryReceiveTimeStart())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (purchaseOrderDelivery.getQueryReceiveTimeEnd() != null) {
				try {
					predicates.add(cb.lessThanOrEqualTo(root.get("receiveTime"), 
							new SimpleDateFormat("yyyy-MM-dd").parse(purchaseOrderDelivery.getQueryReceiveTimeEnd())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	/* 设置是否通过 */
	public void setConfirmable(OperationReviewDTO review)
	{
		/* 如果验证全都通过 */
		if
		(
			! review.getCheckMap().get("emptyPurchaseUnitPriceError") &&
			! review.getCheckMap().get("emptyReceiveQtyError") &&
			! review.getCheckMap().get("differentReceiveQtyError") &&
			! review.getCheckMap().get("isStatusObsoleteError")
		)
		{
			review.setConfirmable( true );
		}
		else
		{
			/* 否则有不通过的，则再看看有没有不通过但是已取消的验证 */
			boolean isEmptyPurchaseUnitPriceError = false;
			boolean isEmptyReceiveQtyError = false;
			boolean isDifferentReceiveQtyError = false;
			boolean isStatusObsoleteError = false;
			
			/* 不在同一仓库，并且没有取消验证 */
			if( review.getCheckMap().get("emptyPurchaseUnitPriceError") && ! review.getIgnoredMap().get("emptyPurchaseUnitPriceError") )
			{
				isEmptyPurchaseUnitPriceError = true;
			}
			/* 发货方式不同，并且没有取消验证 */
			if( review.getCheckMap().get("emptyReceiveQtyError")  && ! review.getIgnoredMap().get("emptyReceiveQtyError") )
			{
				isEmptyReceiveQtyError = true;
			}
			/* 没有指定快递公司或填写起始快递单号，并且没有取消验证 */
			if( review.getCheckMap().get("differentReceiveQtyError") && ! review.getIgnoredMap().get("differentReceiveQtyError") )
			{
				isDifferentReceiveQtyError = true;
			}
			/* 存在处于作废状态的采购单 */
			if( review.getCheckMap().get("isStatusObsoleteError") && ! review.getIgnoredMap().get("isStatusObsoleteError") )
			{
				isStatusObsoleteError = true;
			}
			
			/* 如果有一个验证不通过 */
			if
			(
				isEmptyPurchaseUnitPriceError ||
				isEmptyReceiveQtyError ||
				isDifferentReceiveQtyError ||
				isStatusObsoleteError
			)
			{
				review.setConfirmable( false );
			}
			else
			{
				/* 否则全都通过 */
				review.setConfirmable( true );
			}
			
		}
	}
	
	/* 初始化复核操作数据 */
	public void initDataForOperationReview(OperationReviewDTO review)
	{
		List<PurchaseOrder> purchaseOrders = review.getPurchaseOrders();
		for( PurchaseOrder purchaseOrder : purchaseOrders )
		{
			if( purchaseOrder.getItems() != null && purchaseOrder.getItems().size() > 0 )
			{
				for( PurchaseOrderItem purchaseOrderItem : purchaseOrder.getItems() )
				{
					if( purchaseOrderItem != null && purchaseOrderItem.getId() != null )
					{
						/* 1. 获取各项数据 */
						/* 1.0 获取采购数量 */
						Long purchaseQty = purchaseOrderItem.getPurchaseQty() != null ? purchaseOrderItem.getPurchaseQty() : 0L;
						/* 1.1. 待收货数量 = 采购总数量 - 实际收货数量 - Credit数量。 */
						Long pendingQty = 0L;
						/* 1.2. 实际收货数量 ＝ purchaseOrderDeliveryItem.receiveQty */
						Long realReceivedQty = 0L;
						/* 1.3. Credit数量 = 待收货数量 - 实际收货数量 */
						Long creditQty = purchaseOrderItem.getCreditQty() != null ? purchaseOrderItem.getCreditQty() : 0L;
						/* 1.4. Back Order数量 = 待收货 － 实际收货数量 － Credit数量 */
						Long backOrderQty = purchaseOrderItem.getBackOrderQty() != null ? purchaseOrderItem.getBackOrderQty() : 0L;
						
						/* 2. 获得收货单详情信息 */
						String sql = "SELECT * FROM t_purchase_order_delivery_item " +
									 "WHERE purchase_order_item_id = ?1";
						Query query =  em.createNativeQuery( sql, PurchaseOrderDeliveryItem.class );
						query.setParameter( 1, purchaseOrderItem.getId() );

						/* 3. 收货单详情是否存在于数据库中 */
						if( ! query.getResultList().isEmpty() )
						{
							@SuppressWarnings("unchecked")
							List<PurchaseOrderDeliveryItem> purchaseOrderDeliveryItems = query.getResultList();
							for( PurchaseOrderDeliveryItem purchaseOrderDeliveryItem : purchaseOrderDeliveryItems )
							{
								/* 4. 累计各项数据 */
								/* 4.1 累计［实际收货数量］ */
								if( purchaseOrderDeliveryItem.getReceiveQty() != null )
								{
									realReceivedQty += purchaseOrderDeliveryItem.getReceiveQty();
								}
							}
						}
						
						/* 5. 处理数据 */
						/* 5.1 处理［待收货数量］ */
						pendingQty = purchaseQty - realReceivedQty;
						/* 5.1.1 如果［待收货数量］小于 0，则赋值为 0 */
						pendingQty = pendingQty >= 0 ? pendingQty : 0; 

						/* 5.2 处理［实际收货数量］ */
						Long finalRealReceivedQty = purchaseOrderItem.getRealReceivedQty() != null ? purchaseOrderItem.getRealReceivedQty() : pendingQty;
						/* 5.3 处理［实际收货数量］ */
						BigDecimal finalRealPurchaseUnitPrice = purchaseOrderItem.getRealPurchaseUnitPrice() != null ? purchaseOrderItem.getRealPurchaseUnitPrice() : purchaseOrderItem.getEstimatePurchaseUnitPrice();
						
						/* 6. 封装数据 */
						/* 6.1 封装［待收货数量］ */
						purchaseOrderItem.setPendingQty( pendingQty );
						/* 6.2 封装［实际收货数量］ */
						purchaseOrderItem.setRealReceivedQty( finalRealReceivedQty );
						/* 6.3 封装［实际采购单价］ */
						purchaseOrderItem.setRealPurchaseUnitPrice( finalRealPurchaseUnitPrice );
						/* 6.4 封装［Credit数量］ */
						purchaseOrderItem.setCreditQty( creditQty );
						/* 6.5 封装［Back Order数量］ */
						purchaseOrderItem.setBackOrderQty( backOrderQty );
					}
				}
			}
		}
	}

	/* 验证 1 ： 商品需要设定采购单价（可以忽略验证） */
	public void confirmEmptyPurchaseUnitPrice( OperationReviewDTO review )
	{
		List<PurchaseOrder> purchaseOrders = review.getPurchaseOrders();
		Boolean isAnyError = false;
		for( PurchaseOrder purchaseOrder : purchaseOrders )
		{
			/* 如果没有取消［采购单］的验证 */
			if( ! purchaseOrder.getIgnoreCheck() )
			{
				if( purchaseOrder.getItems() != null && purchaseOrder.getItems().size() > 0 )
				{
					for( PurchaseOrderItem purchaseOrderItem : purchaseOrder.getItems() )
					{
						/* 如果没有取消［采购单详情］的验证 */
						if( ! purchaseOrderItem.getIgnoreCheck() )
						{
							/* 如果［实际采购单价］为空或者［实际采购单价］小于或者等于 0，则采购单价为空 */
							if
							(
								purchaseOrderItem.getRealPurchaseUnitPrice() == null ||
								purchaseOrderItem.getRealPurchaseUnitPrice().compareTo(BigDecimal.ZERO) == -1 ||
								purchaseOrderItem.getRealPurchaseUnitPrice().compareTo(BigDecimal.ZERO) == 0
							)
							{
								purchaseOrderItem.getCheckMap().put("emptyPurchaseUnitPriceError", true);
								isAnyError = true;
							}
							else
							{
								purchaseOrderItem.getCheckMap().put("emptyPurchaseUnitPriceError", false);
							}
						}
					}
				}
			}
		}
		review.getCheckMap().put("emptyPurchaseUnitPriceError", isAnyError);
	}

	/* 验证 2 ： 商品需要设定收货数量（可以忽略验证） */
	public void confirmEmptyReceiveQty( OperationReviewDTO review )
	{
		List<PurchaseOrder> purchaseOrders = review.getPurchaseOrders();
		Boolean isAnyError = false;
		for( PurchaseOrder purchaseOrder : purchaseOrders )
		{
			/* 如果没有取消［采购单］的验证 */
			if( ! purchaseOrder.getIgnoreCheck() )
			{
				if( purchaseOrder.getItems() != null && purchaseOrder.getItems().size() > 0 )
				{
					for( PurchaseOrderItem purchaseOrderItem : purchaseOrder.getItems() )
					{
						/* 如果没有取消［采购单详情］的验证 */
						if( ! purchaseOrderItem.getIgnoreCheck() )
						{
							if( purchaseOrderItem.getRealReceivedQty() == null || purchaseOrderItem.getRealReceivedQty() <= 0 )
							{
								purchaseOrderItem.getCheckMap().put("emptyReceiveQtyError", true);
								isAnyError = true;
							}
							else
							{
								purchaseOrderItem.getCheckMap().put("emptyReceiveQtyError", false);
							}
						}
					}
				}
			}
		}
		review.getCheckMap().put("emptyReceiveQtyError", isAnyError);
	}

	/* 验证 3 ： 商品实际收货数量匹配（可以忽略验证） */
	public void confirmDifferentReceiveQty( OperationReviewDTO review )
	{
		List<PurchaseOrder> purchaseOrders = review.getPurchaseOrders();
		Boolean isAnyError = false;
		for( PurchaseOrder purchaseOrder : purchaseOrders )
		{
			/* 如果没有取消［采购单］的验证 */
			if( ! purchaseOrder.getIgnoreCheck() )
			{
				if( purchaseOrder.getItems() != null && purchaseOrder.getItems().size() > 0 )
				{
					for( PurchaseOrderItem purchaseOrderItem : purchaseOrder.getItems() )
					{
						/* 如果没有取消［采购单详情］的验证 */
						if( ! purchaseOrderItem.getIgnoreCheck() )
						{
							/* 如果［待收货数量］为空或者［实际收货数量］为空或者［待收货数量］和［实际收货数量］不相等，则商品实际收货数量不匹配 */
							if
							(
								purchaseOrderItem.getPendingQty() == null || purchaseOrderItem.getRealReceivedQty() == null ||
								purchaseOrderItem.getPendingQty() != purchaseOrderItem.getRealReceivedQty()
							)
							{
								purchaseOrderItem.getCheckMap().put("differentReceiveQtyError", true);
								isAnyError = true;
							}
							else
							{
								purchaseOrderItem.getCheckMap().put("differentReceiveQtyError", false);
							}
						}
					}
				}
			}
		}
		review.getCheckMap().put("differentReceiveQtyError", isAnyError);
	}

	/* 验证 4 ： 采购单不能处于作废状态 */
	public void confirmIsStatusObsolete( OperationReviewDTO review )
	{
		List<PurchaseOrder> purchaseOrders = review.getPurchaseOrders();
		Boolean isAnyError = false;
		for( PurchaseOrder purchaseOrder : purchaseOrders )
		{
			/* 如果没有取消［采购单］的验证 */
			if( ! purchaseOrder.getIgnoreCheck() )
			{
				/* 如果处于［作废］状态 */
				if( purchaseOrder.getStatus().equals( 3 ) )
				{
					if( purchaseOrder.getItems() != null )
					{
						for( PurchaseOrderItem purchaseOrderItem : purchaseOrder.getItems() )
						{
							purchaseOrderItem.getCheckMap().put("isStatusObsoleteError", true);
						}
					}
					purchaseOrder.getCheckMap().put("isStatusObsoleteError", true);
					isAnyError = true;
				}
				else
				{
					if( purchaseOrder.getItems() != null )
					{
						for( PurchaseOrderItem purchaseOrderItem : purchaseOrder.getItems() )
						{
							purchaseOrderItem.getCheckMap().put("isStatusObsoleteError", false);
						}
					}
					purchaseOrder.getCheckMap().put("isStatusObsoleteError", false);
				}
			}
		}
		review.getCheckMap().put("isStatusObsoleteError", isAnyError);
	}

	/* 如果验证全都通过，并且操作类型是 CONFIRM 则执行创建操作 */
	public void executePurchseOrderDeliveryGeneration(OperationReviewDTO review)
	{
		/* 假设有可生成收货单的采购单 */
		review.getResultMap().put("isEmptyFinalPurchaseOrders", false);

		List<PurchaseOrder> finalPurchaseOrders = new ArrayList<PurchaseOrder>();
		List<PurchaseOrder> purchaseOrders = review.getPurchaseOrders();
		for( PurchaseOrder purchaseOrder : purchaseOrders )
		{
			/* 如果订单没有被移出，则添加到最终订单列表中 */
			if( ! purchaseOrder.getIgnoreCheck() )
			{
				finalPurchaseOrders.add( purchaseOrder );
			}
		}
		
		/* 如果有可生成收货单的采购单 */
		if( finalPurchaseOrders.size() > 0 )
		{
			/* 准备可插入的［收货单］集合 */
			List<PurchaseOrderDelivery> insertablePurchaseOrderDeliveries = new ArrayList<PurchaseOrderDelivery>();
			List<PurchaseOrder> updatablePurchaseOrders = new ArrayList<PurchaseOrder>();
			
			/* 取得收货人信息 */
			Number receiveUserId = (Number) review.getDataMap().get("receiveUserId");
			Long finalReceiveUserId = receiveUserId.longValue();
			
			/* 获取所有的［采购单］ */
			for( PurchaseOrder purchaseOrder : finalPurchaseOrders )
			{
				/* 设置为状态为：已收货 */
				purchaseOrder.setStatus( 2 );
				this.purchaseOrderRepository.save( purchaseOrder );
				
				Long totalDeliveredQty = 0L;
				Long totalCreditQty = 0L;
				BigDecimal totalInvoiceAmount = new BigDecimal( 0 );
				BigDecimal totalDeliveredAmount = new BigDecimal( 0 );
				BigDecimal totalCreditAmount = new BigDecimal( 0 );
				
				/* 准备可插入的［收货单详情］集合 */
				List<PurchaseOrderDeliveryItem> insertablePurchaseOrderDeliveryItems = new ArrayList<PurchaseOrderDeliveryItem>();
				for( PurchaseOrderItem purchaseOrderItem : purchaseOrder.getItems() )
				{
					/* 待收货数量,实际收货数量,Credit数量,等back order数量任意一个不等于空且大于0，则该［收货单］可插入 */
					if
					(
						( purchaseOrderItem.getPendingQty() != null && purchaseOrderItem.getPendingQty() > 0 ) ||
						( purchaseOrderItem.getRealReceivedQty() != null && purchaseOrderItem.getRealReceivedQty() > 0 ) ||
						( purchaseOrderItem.getCreditQty() != null && purchaseOrderItem.getCreditQty() > 0 ) ||
						( purchaseOrderItem.getBackOrderQty() != null && purchaseOrderItem.getBackOrderQty() > 0 )
					)
					{
						SupplierProduct insertableSupplierProduct = new SupplierProduct();
//						PurchaseOrderItem insertablePurchaseOrderItem = new PurchaseOrderItem();
						PurchaseOrderDeliveryItem insertablePurchaseOrderDeliveryItem = new PurchaseOrderDeliveryItem();

						/* 保存数据到可插入的［收货单详情］ */
						insertableSupplierProduct.setId( purchaseOrderItem.getSupplierProduct().getId() );
//						insertablePurchaseOrderItem.setId( purchaseOrderItem.getId() );
						insertablePurchaseOrderDeliveryItem.setSupplierProduct( insertableSupplierProduct );
//						insertablePurchaseOrderDeliveryItem.setPurchaseOrderItem( insertablePurchaseOrderItem );
						insertablePurchaseOrderDeliveryItem.setPurchaseOrderItemId( purchaseOrderItem.getId() );
						insertablePurchaseOrderDeliveryItem.setRealPurchaseUnitPrice( purchaseOrderItem.getRealPurchaseUnitPrice() );
						insertablePurchaseOrderDeliveryItem.setReceiveQty( purchaseOrderItem.getRealReceivedQty() != null ? purchaseOrderItem.getRealReceivedQty() : 0 );
						insertablePurchaseOrderDeliveryItem.setCreditQty( purchaseOrderItem.getCreditQty() != null ? purchaseOrderItem.getCreditQty() : 0 );
						
						/* 将可插入的［收货单详情］加入可插入的［收货单详情］集合中 */
						insertablePurchaseOrderDeliveryItems.add( insertablePurchaseOrderDeliveryItem );
						

						/* 追加数据到可更新的［收货单］ */
						totalDeliveredQty += purchaseOrderItem.getRealReceivedQty();
						totalCreditQty += purchaseOrderItem.getCreditQty();
						totalInvoiceAmount = totalInvoiceAmount.add( purchaseOrderItem.getRealPurchaseUnitPrice().multiply( new BigDecimal( purchaseOrderItem.getRealReceivedQty() ) ) );
						totalDeliveredAmount = totalDeliveredAmount.add( purchaseOrderItem.getRealPurchaseUnitPrice().multiply( new BigDecimal( purchaseOrderItem.getRealReceivedQty() ) ) );
						totalCreditAmount = totalCreditAmount.add( purchaseOrderItem.getRealPurchaseUnitPrice().multiply( new BigDecimal( purchaseOrderItem.getCreditQty() ) ) );
					}
				}
				
				/* 如果可插入的［收货单详情］有数据，则将［收货单详情］附在［收货单］上，并将收货单加入可插入［收货单］集合中 */
				if( insertablePurchaseOrderDeliveryItems.size() > 0 )
				{
					User insertableReceiveUser = new User();
					PurchaseOrderDelivery insertablePurchaseOrderDelivery = new PurchaseOrderDelivery();
					
					/* 设置为状态为：未入库 */
					insertablePurchaseOrderDelivery.setStatus( 1 );

					/* 保存数据到可插入的［收货单］ */
					insertableReceiveUser.setId( finalReceiveUserId );
					insertablePurchaseOrderDelivery.setPurchaseOrderId( purchaseOrder.getId() );
					insertablePurchaseOrderDelivery.setReceiveTime( new Date() );
					insertablePurchaseOrderDelivery.setReceiveUser( insertableReceiveUser );
					insertablePurchaseOrderDelivery.setItems( insertablePurchaseOrderDeliveryItems );

					/* 将可插入的［收货单］加入可插入的［收货单］集合中 */
					insertablePurchaseOrderDeliveries.add( insertablePurchaseOrderDelivery );
					

					PurchaseOrder updatablePurchaseOrder = this.purchaseOrderRepository.findOne( purchaseOrder.getId() );
					
					/* 如果发票总金额为空，初始化为 0 */
					if( updatablePurchaseOrder.getTotalInvoiceAmount() == null )
					{
						updatablePurchaseOrder.setTotalInvoiceAmount( new BigDecimal( 0 ) );
					}
					/* 如果收货总金额为空，初始化为 0 */
					if( updatablePurchaseOrder.getTotalDeliveredAmount() == null )
					{
						updatablePurchaseOrder.setTotalDeliveredAmount( new BigDecimal( 0 ) );
					}
					/* 如果Credit总金额为空，初始化为 0 */
					if( updatablePurchaseOrder.getTotalCreditAmount() == null )
					{
						updatablePurchaseOrder.setTotalCreditAmount( new BigDecimal( 0 ) );
					}
					
					/* 保存数据到可更新的［采购单］ */
					updatablePurchaseOrder.setId( purchaseOrder.getId() );
					updatablePurchaseOrder.setTotalDeliveredQty( updatablePurchaseOrder.getTotalDeliveredQty() + totalDeliveredQty );
					updatablePurchaseOrder.setTotalCreditQty( updatablePurchaseOrder.getTotalCreditQty() + totalCreditQty );
					updatablePurchaseOrder.setTotalInvoiceAmount( updatablePurchaseOrder.getTotalInvoiceAmount().add( totalInvoiceAmount ) );
					updatablePurchaseOrder.setTotalDeliveredAmount( updatablePurchaseOrder.getTotalDeliveredAmount().add( totalDeliveredAmount ) );
					updatablePurchaseOrder.setTotalCreditAmount( updatablePurchaseOrder.getTotalCreditAmount().add( totalCreditAmount ) );
					updatablePurchaseOrder.getPurchaseOrderDeliveries().addAll( insertablePurchaseOrderDeliveries );

					/* 将可更新的［采购单］加入可更新的［采购单］集合中 */
					updatablePurchaseOrders.add( updatablePurchaseOrder );
				}
			}

			/* 如果可插入的［收货单］有数据，则将所有［收货单］一次性插入到数据库中 */
			if( updatablePurchaseOrders.size() > 0 )
			{
				this.purchaseOrderRepository.save( updatablePurchaseOrders );
			}
			
			review.getResultMap().put("generatedPurchaseOrderDeliveryCount", insertablePurchaseOrderDeliveries.size());
		}
		else
		{
			/* 如果没有最终采购单 */
			review.getResultMap().put("isEmptyFinalPurchaseOrders", true);
		}
	}

	public OperationReviewDTO confirmOrderWhenGeneratePurchaseOrderDelivery( OperationReviewDTO review )
	{
		/* 初始化复核操作数据 */
		initDataForOperationReview( review );
		
		/* 验证 1 ： 商品需要设定采购单价（可以忽略验证） */
		this.confirmEmptyPurchaseUnitPrice( review );
		
		/* 验证 2 ： 商品需要设定收货数量（可以忽略验证） */
		this.confirmEmptyReceiveQty( review );
		
		/* 验证 3 ： 商品实际收货数量匹配（可以忽略验证） */
		this.confirmDifferentReceiveQty( review );
		
		/* 验证 4 ： 采购单不能处于作废状态 */
		this.confirmIsStatusObsolete( review );
		
		/* 设置可确认性 */
		this.setConfirmable( review );
		
		/* 如果验证全都通过，并且操作类型是 CONFIRM 则执行创建操作 */
		if( review.isConfirmable() &&
			review.getAction().equals(OperationReviewDTO.CONFIRM) )
		{
			/* 执行生成收货单操作 */
			this.executePurchseOrderDeliveryGeneration( review );
		}
		
		return review;
		
	}
	
	// 得到这张收货单已入库的数量
	public Long getEnteredQty(Long receiveId) {
		
		String sqlString = "SELECT sum(changed_quantity) FROM t_inventory_batch_item WHERE inventory_batch_id IN (SELECT id FROM t_inventory_batch WHERE receive_id = " + receiveId + ")";
		System.out.println("sqlString: " + sqlString);
		Object obj = em.createNativeQuery(sqlString).getSingleResult();
		Long count = Long.valueOf(obj == null ? "0" : obj.toString());
		System.out.println("getEnteredQty(): " + count);
		return count;
	}
	
	// 得到这张收货单应入库的数量
	
	public Long getEnterableQty(Long receiveId) {
		String sqlString = "select sum(receive_qty) from t_purchase_order_delivery_item where purchase_order_delivery_id = " + receiveId;
		System.out.println("sqlString: " + sqlString);
		Long count = Long.valueOf(em.createNativeQuery(sqlString).getSingleResult().toString());
		System.out.println("getEnterableQty(): " + count);
		return count;
	}
	
	/*
	 * PurchaseOrderDeliveryItem
	 */
	
	public PurchaseOrderDeliveryItem savePurchaseOrderDeliveryItem(PurchaseOrderDeliveryItem purchaseOrderDeliveryItem) {
		return this.purchaseOrderDeliveryItemRepository.save(purchaseOrderDeliveryItem);
	}

	public void deletePurchaseOrderDeliveryItem(Long id) {
		this.purchaseOrderDeliveryItemRepository.delete(id);
	}

	public PurchaseOrderDeliveryItem getPurchaseOrderDeliveryItem(Long id) {
		return this.purchaseOrderDeliveryItemRepository.findOne(id);
	}

	public List<PurchaseOrderDeliveryItem> getPurchaseOrderDeliveryItems(Sort sort) {
		return this.purchaseOrderDeliveryItemRepository.findAll(sort);
	}

	public Page<PurchaseOrderDeliveryItem> getPagedPurchaseOrderDeliveryItems(Pageable pageable) {
		return this.purchaseOrderDeliveryItemRepository.findAll(pageable);
	}
	
	public void addPurchaseOrderDeliveryToCell( PurchaseOrder purchaseOrder, Workbook workbook, Sheet sheet )
	{
    	Row firstRow = sheet.getRow( 0 );
    	CellReference a1 = new CellReference("A1");
    	Cell orderFromRowCell = firstRow.getCell( a1.getCol() );
    	
    	orderFromRowCell.setCellValue( "Order From: " + ( purchaseOrder.getSupplier() != null ? purchaseOrder.getSupplier().getName() : "" ) );
    	CellReference d1 = new CellReference("D1");
    	Cell estimateReceiveDateRowCell = firstRow.getCell( d1.getCol() );
    	
    	estimateReceiveDateRowCell.setCellValue( "到货日期: " + ( purchaseOrder.getEstimateReceiveDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format( purchaseOrder.getEstimateReceiveDate() ) : "" ) );

    	Font itemContentFont = workbook.createFont();
    	itemContentFont.setColor( HSSFColor.BLACK.index );
    	itemContentFont.setBold( false );
    	itemContentFont.setFontName("宋体");
    	itemContentFont.setFontHeightInPoints( (short) 12 );
    	
    	CellStyle itemContentCommonStyle = workbook.createCellStyle();
    	itemContentCommonStyle.setWrapText( true );
    	itemContentCommonStyle.setFont( itemContentFont );
    	itemContentCommonStyle.setBorderBottom( CellStyle.BORDER_THIN );
    	itemContentCommonStyle.setBorderLeft( CellStyle.BORDER_THIN );
    	itemContentCommonStyle.setBorderRight( CellStyle.BORDER_THIN );
    	itemContentCommonStyle.setBorderTop( CellStyle.BORDER_THIN );
    	
    	/* 添加收货单详情
    	 */
    	List<PurchaseOrderItem> purchaseOrderItems = purchaseOrder.getItems();
    	if( purchaseOrderItems != null && purchaseOrderItems.size() > 0 )
    	{
    		for( int i = 0; i < purchaseOrderItems.size(); i++ )
    		{
    			PurchaseOrderItem item = purchaseOrderItems.get( i );
    			
    			String productName = item.getSupplierProduct() != null && item.getSupplierProduct().getSupplierProductName() != null ? item.getSupplierProduct().getSupplierProductName() : " ";
    			if( productName.trim().equals("") )
    			{
    				productName = item.getSupplierProduct() != null && item.getSupplierProduct().getProduct() != null && item.getSupplierProduct().getProduct().getShortName() != null ? item.getSupplierProduct().getProduct().getShortName() : " ";
        			if( productName.trim().equals("") )
        			{
        				productName = item.getSupplierProduct() != null && item.getSupplierProduct().getProduct() != null && item.getSupplierProduct().getProduct().getName() != null ? item.getSupplierProduct().getProduct().getName() : " ";
        			}
    			}
    			
    			String barcode = item.getSupplierProduct() != null && item.getSupplierProduct().getSupplierProductBarcode() != null ? item.getSupplierProduct().getSupplierProductBarcode() : " ";
    			if( barcode.trim().equals("") )
    			{
    				barcode = item.getSupplierProduct() != null && item.getSupplierProduct().getProduct() != null ? item.getSupplierProduct().getProduct().getBarcode() : " ";
    			}
    			String purchaseQty = item.getPurchaseQty() != null ? item.getPurchaseQty().toString() : "";

    			Row contentRow = sheet.createRow( i + 2 );
    	    	
	        	setCell( contentRow, null, productName, 0 );
	        	setCell( contentRow, null, barcode, 1 );
	        	setCell( contentRow, null, purchaseQty, 2 );
    		}
    	}
	}

	public void addShipmentToCell( Shipment shipment, Sheet sheet, CellStyle contentStyle, Integer rowIndex )
	{
    	/* 如果存在指定发货单 */
    	if( shipment != null )
    	{
    		StringBuffer shippedProductsBuffer = new StringBuffer();
    		
    		List<ShipmentItem> shipmentItems = shipment.getShipmentItems();
    		if( shipmentItems != null && shipmentItems.size() > 0 )
    		{
    			for( int i = 0; i < shipmentItems.size(); i++ )
    			{
    				if( ! shippedProductsBuffer.toString().equals("") )
    				{
    					shippedProductsBuffer.append(", ");
    				}
    				OrderItem orderItem = shipmentItems.get( i ).getOrderItem();
    				if( orderItem != null && orderItem.getProduct() != null )
    				{
    					Product product = orderItem.getProduct();
    					shippedProductsBuffer.append( product.getShortName() );
    					shippedProductsBuffer.append( "*" );
    					shippedProductsBuffer.append( orderItem.getQtyOrdered() );
//    					shippedProductsBuffer.append( "\n" );
    				}
    			}
    		}
    		
    		String[] contents =
    		{
    			shipment.getReceiveName(), shipment.getReceivePhone(), shipment.getReceivePhone(), shipment.getReceiveAddress(),
    			shippedProductsBuffer.toString(), shipment.getOrderId().toString(), shipment.getShipNumber(),
    			shipment.getId().toString(), /* this.getShipStatusByTinyint( shipment.getShipStatus() ) */ "正常" , shipment.getMemo()
    		};
    		
        	Row contentRow = sheet.createRow( rowIndex );
        	for( int i = 0; i < contents.length; i++ )
        	{
            	Cell cell = contentRow.createCell( i );
            	
            	/* 如果是
            	 */
//            	if( i == 0 || i == 3 || i == 4 )
//            	{
                	cell.setCellStyle( contentStyle );
//            	}
            	cell.setCellValue( contents[ i ] );
        	}
    	}
	}
	
	public void setCell( Row row, CellStyle cellStyle, String cellValue, Integer index )
	{
    	Cell cell = row.createCell( index );
    	cell.setCellStyle( cellStyle );
    	cell.setCellValue( cellValue );
	}
	
	
	
}
