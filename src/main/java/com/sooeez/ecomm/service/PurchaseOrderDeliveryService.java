package com.sooeez.ecomm.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.PurchaseOrder;
import com.sooeez.ecomm.domain.PurchaseOrderDelivery;
import com.sooeez.ecomm.domain.PurchaseOrderDeliveryItem;
import com.sooeez.ecomm.domain.PurchaseOrderItem;
import com.sooeez.ecomm.domain.User;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.repository.PurchaseOrderDeliveryItemRepository;
import com.sooeez.ecomm.repository.PurchaseOrderDeliveryRepository;
import com.sooeez.ecomm.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderDeliveryService {

	@Autowired PurchaseOrderDeliveryRepository purchaseOrderDeliveryRepository;

	@Autowired PurchaseOrderRepository purchaseOrderRepository;
	
	@Autowired PurchaseOrderDeliveryItemRepository purchaseOrderDeliveryItemRepository;
	
	// Service
	@PersistenceContext private EntityManager em;
	
	/*
	 * Tag
	 */
	
	public PurchaseOrderDelivery savePurchaseOrderDelivery(PurchaseOrderDelivery purchaseOrderDelivery) {
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
			
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	/* 设置是否通过 */
	public void setConfirmable(OperationReviewDTO review)
	{
		/* 如果验证全都通过 */
		if( ! review.getCheckMap().get("emptyPurchaseUnitPriceError") &&
			! review.getCheckMap().get("emptyReceiveQtyError") &&
			! review.getCheckMap().get("differentReceiveQtyError") )
		{
			review.setConfirmable( true );
		}
		else
		{
			/* 否则有不通过的，则再看看有没有不通过但是已取消的验证 */
			boolean isEmptyPurchaseUnitPriceError = false;
			boolean isEmptyReceiveQtyError = false;
			boolean isDifferentReceiveQtyError = false;
			
			/* 不在同一仓库，并且没有取消验证 */
			if( review.getCheckMap().get("emptyPurchaseUnitPriceError") && ! review.getIgnoredMap().get("emptyPurchaseUnitPriceError") )
			{
				isEmptyPurchaseUnitPriceError = true;
			}
			/* 发货方式不同，并且没有取消验证 */
			if( review.getCheckMap().get("emptyReceiveQtyError")  && ! review.getIgnoredMap().get("emptyReceiveQtyError") )
			{
				isEmptyReceiveQtyError = true;
			}/* 没有指定快递公司或填写起始快递单号，并且没有取消验证 */
			if( review.getCheckMap().get("differentReceiveQtyError") && ! review.getIgnoredMap().get("differentReceiveQtyError") )
			{
				isDifferentReceiveQtyError = true;
			}
			
			/* 如果有一个验证不通过 */
			if( isEmptyPurchaseUnitPriceError ||
					isEmptyReceiveQtyError ||
					isDifferentReceiveQtyError )
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
							if( purchaseOrderItem.getRealPurchaseUnitPrice() == null ||
								purchaseOrderItem.getRealPurchaseUnitPrice().compareTo(BigDecimal.ZERO) == -1 ||
								purchaseOrderItem.getRealPurchaseUnitPrice().compareTo(BigDecimal.ZERO) == 0 )
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
							if( purchaseOrderItem.getPendingQty() == null || purchaseOrderItem.getRealReceivedQty() == null ||
								purchaseOrderItem.getPendingQty() != purchaseOrderItem.getRealReceivedQty() )
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
		
		/* 如果有可生成发货单的订单 */
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
					if( ( purchaseOrderItem.getPendingQty() != null && purchaseOrderItem.getPendingQty() > 0 ) ||
						( purchaseOrderItem.getRealReceivedQty() != null && purchaseOrderItem.getRealReceivedQty() > 0 ) ||
						( purchaseOrderItem.getCreditQty() != null && purchaseOrderItem.getCreditQty() > 0 ) ||
						( purchaseOrderItem.getBackOrderQty() != null && purchaseOrderItem.getBackOrderQty() > 0 ) )
					{
						Product insertableProduct = new Product();
//						PurchaseOrderItem insertablePurchaseOrderItem = new PurchaseOrderItem();
						PurchaseOrderDeliveryItem insertablePurchaseOrderDeliveryItem = new PurchaseOrderDeliveryItem();

						/* 保存数据到可插入的［收货单详情］ */
						insertableProduct.setId( purchaseOrderItem.getProduct().getId() );
//						insertablePurchaseOrderItem.setId( purchaseOrderItem.getId() );
						insertablePurchaseOrderDeliveryItem.setProduct( insertableProduct );
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
						
						System.out.println("totalDeliveredQty: " + totalDeliveredQty);
						System.out.println("totalCreditQty: " + totalCreditQty);
						System.out.println("totalInvoiceAmount: " + totalInvoiceAmount);
						System.out.println("totalDeliveredAmount: " + totalDeliveredAmount);
						System.out.println("totalCreditAmount: " + totalCreditAmount);
					}
				}
				
				/* 如果可插入的［收货单详情］有数据，则将［收货单详情］附在［收货单］上，并将收货单加入可插入［收货单］集合中 */
				if( insertablePurchaseOrderDeliveryItems.size() > 0 )
				{
					User insertableReceiveUser = new User();
					PurchaseOrderDelivery insertablePurchaseOrderDelivery = new PurchaseOrderDelivery();

					/* 保存数据到可插入的［收货单］ */
					insertableReceiveUser.setId( finalReceiveUserId );
					insertablePurchaseOrderDelivery.setPurchaseOrderId( purchaseOrder.getId() );
					insertablePurchaseOrderDelivery.setReceiveTime( new Date() );
					insertablePurchaseOrderDelivery.setReceiveUser( insertableReceiveUser );
					insertablePurchaseOrderDelivery.setItems( new ArrayList<PurchaseOrderDeliveryItem>() );
					insertablePurchaseOrderDelivery.getItems().addAll( insertablePurchaseOrderDeliveryItems );
					
//					for( PurchaseOrderDeliveryItem purchaseOrderDeliveryItem : insertablePurchaseOrderDelivery.getItems() )
//					{
//						System.out.println( "purchaseOrderDeliveryItem.getPurchaseOrderItem().getId(): " + purchaseOrderDeliveryItem.getPurchaseOrderItem().getId() );
//					}

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
	
}
