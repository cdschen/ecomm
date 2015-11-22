package com.sooeez.ecomm.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.domain.Order;
import com.sooeez.ecomm.domain.OrderItem;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.Shipment;
import com.sooeez.ecomm.domain.ShipmentItem;
import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.domain.User;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.dto.OperationReviewShipmentDTO;
import com.sooeez.ecomm.repository.ObjectProcessRepository;
import com.sooeez.ecomm.repository.OrderRepository;
import com.sooeez.ecomm.repository.ShipmentItemRepository;
import com.sooeez.ecomm.repository.ShipmentRepository;
import com.sooeez.ecomm.repository.ShopRepository;

@Service
public class ShipmentService {

	// Repository
	@Autowired private ShopRepository shopRepository;
	@Autowired private OrderRepository orderRepository;
	@Autowired private ShipmentRepository shipmentRepository;
	@Autowired private ShipmentItemRepository shipmentItemRepository;
	@Autowired private ObjectProcessRepository objectProcessRepository;
	
	// Service
	@Autowired private ProcessService processService;

	@PersistenceContext private EntityManager em;
	
	/*
	 * Shipment
	 */
	
	public List<Shipment> saveShipments( Shipment shipment )
	{
		return this.shipmentRepository.save( shipment.getShipments() );
	}
	
	public Shipment saveShipment(Shipment shipment) {
		return this.shipmentRepository.save(shipment);
	}
	
	public void deleteShipment(Long id) {
		this.shipmentRepository.delete(id);
	}
	
	public Shipment getShipment(Long id) {
		return this.shipmentRepository.findOne(id);
	}
	
	public String getShipStatusByTinyint( Integer shipStatus )
	{
		String strStatus = "";
		
		switch( shipStatus )
		{
			case 1: strStatus = "待打印"; break;
			case 2: strStatus = "已打印"; break;
			case 3: strStatus = "已发出"; break;
			case 4: strStatus = "已签收"; break;
			case 5: strStatus = "配送异常"; break;
			case 6: strStatus = "已作废"; break;
			default :strStatus = "未指定状态";
		}
		
		return strStatus;
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
    			shippedProductsBuffer.toString(),
    			shipment.getOrderId().toString(), "", shipment.getShipNumber(),
    			"", shipment.getId().toString(), /* this.getShipStatusByTinyint( shipment.getShipStatus() ) */ "正常" , shipment.getMemo()
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
	
	@SuppressWarnings("unchecked")
	public List<Shipment> getShipmentsByIds( Long[] ids )
	{
		List<Shipment> shipments = null;
		StringBuffer shipmentIdsBuffer = new StringBuffer();
		for( Long shipmentId : ids )
		{
			if( ! shipmentIdsBuffer.toString().equals("") )
			{
				shipmentIdsBuffer.append(", ");
			}
			shipmentIdsBuffer.append( shipmentId );
		}

		/* 1. 获得发货单集合 */
		String sqlShipments = "SELECT * FROM t_shipment WHERE id IN( " + shipmentIdsBuffer.toString() + " )";
		Query queryShipments = em.createNativeQuery( sqlShipments, Shipment.class );
		shipments = queryShipments.getResultList();
		
		return shipments;
	}
	
	public List<Shipment> getShipments() {
		return this.shipmentRepository.findAll();
	}

	public Page<Shipment> getPagedShipments(Shipment shipment, Pageable pageable)
	{
		Page<Shipment> page = this.shipmentRepository.findAll(getShipmentSpecification(shipment), pageable);
		List<Shipment> shipments = page.getContent();
		for( Shipment finalShipment : shipments )
		{
			/* 根据［订单号］获取［订单］再根据［店铺号］获取［店铺］，并将［店铺］数据存到［店铺属性］上 */
			Order order = this.orderRepository.getOne( finalShipment.getOrderId() );
			Shop shop = this.shopRepository.getOne( order.getShopId() );
			
			finalShipment.setShop( shop );
		}
		return page;
	}
	
	private void addPredicateDateStartEnd(Root<Shipment> root, List<Predicate> predicates, CriteriaBuilder cb, String dateFieldName, String dateStart, String dateEnd)
	{
		if ( dateStart != null && dateEnd != null )
		{
			try
			{
				predicates.add( cb.between( root.get( dateFieldName ), new SimpleDateFormat("yyyy-MM-dd").parse( dateStart ), new SimpleDateFormat("yyyy-MM-dd").parse( dateEnd ) ) );
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		else if ( dateStart != null )
		{
			try
			{
				predicates.add( cb.greaterThanOrEqualTo( root.get( dateFieldName ), new SimpleDateFormat("yyyy-MM-dd").parse( dateStart ) ) );
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		else if ( dateEnd != null)
		{
			try
			{
				predicates.add( cb.lessThanOrEqualTo( root.get( dateFieldName ), new SimpleDateFormat("yyyy-MM-dd").parse( dateEnd ) ) );
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private Specification<Shipment> getShipmentSpecification(Shipment shipment)
	{
		return (root, query, cb) ->
		{
			List<Predicate> predicates = new ArrayList<>();
			if (shipment.getShipWarehouseId() != null)
			{
				predicates.add( cb.equal(root.get("shipWarehouseId"), shipment.getShipWarehouseId() ) );
			}
			if (shipment.getOrderId() != null)
			{
				predicates.add( cb.equal(root.get("orderId"), shipment.getOrderId() ) );
			}
			if (shipment.getCourierId() != null)
			{
				predicates.add( cb.equal( root.get("courierId"), shipment.getCourierId() ) );
			}
			if ( StringUtils.hasText( shipment.getShipNumber() ) )
			{
				predicates.add( cb.like(root.get("shipNumber"), "%" + shipment.getShipNumber() + "%") );
			}
			if ( shipment.getShipStatus() != null )
			{
				predicates.add( cb.equal( root.get("shipStatus"), shipment.getShipStatus() ) );
			}
			
			/* 添加谓词：各起始 － 结束日期 */
			addPredicateDateStartEnd( root, predicates, cb, "createTime", shipment.getCreateTimeStart(), shipment.getCreateTimeEnd() );
			addPredicateDateStartEnd( root, predicates, cb, "lastUpdate", shipment.getLastUpdateStart(), shipment.getLastUpdateEnd() );
			addPredicateDateStartEnd( root, predicates, cb, "pickupTime", shipment.getPickupTimeStart(), shipment.getPickupTimeEnd() );
			addPredicateDateStartEnd( root, predicates, cb, "signupTime", shipment.getSignupTimeStart(), shipment.getSignupTimeEnd() );
			
			if (shipment.getShopId() != null)
			{
				Subquery<Order> orderSubquery = query.subquery( Order.class );
				Root<Order> orderRoot = orderSubquery.from( Order.class );
				orderSubquery.select( orderRoot.get("id") );
				orderSubquery.where( orderRoot.get("shopId").in( shipment.getShopId() ) );
				predicates.add( cb.in(root.get("orderId")).value( orderSubquery ) );
			}
			
			return cb.and( predicates.toArray( new Predicate[predicates.size()] ) );
		};
	}

	/* 设置是否通过 */
	public void setConfirmable(OperationReviewShipmentDTO review)
	{
		/* 如果验证全都通过 */
		if( ! review.getCheckMap().get("emptyCourierError") &&
			! review.getCheckMap().get("emptyShipNumberError") &&
			! review.getCheckMap().get("emptyReceiveNameError") &&
			! review.getCheckMap().get("emptyReceivePhoneError") &&
			! review.getCheckMap().get("emptyReceiveAddressError") )
		{
			review.setConfirmable( true );
		}
		else
		{
			/* 否则有不通过的，则再看看有没有不通过但是已取消的验证 */
			boolean isEmptyCourierError = false;
			boolean isEmptyShipNumberError = false;
			boolean isEmptyReceiveNameError = false;
			boolean isEmptyReceivePhoneError = false;
			boolean isEmptyReceiveAddressError = false;
			
			/* 发货单没有指定快递公司，并且没有取消验证 */
			if( review.getCheckMap().get("emptyCourierError") && ! review.getIgnoredMap().get("emptyCourierError") )
			{
				isEmptyCourierError = true;
			}
			/* 发货单没有指定快递单号，并且没有取消验证 */
			if( review.getCheckMap().get("emptyShipNumberError")  && ! review.getIgnoredMap().get("emptyShipNumberError") )
			{
				isEmptyShipNumberError = true;
			}
			/* 发货单没有填写收件人姓名，并且没有取消验证 */
			if( review.getCheckMap().get("emptyReceiveNameError") && ! review.getIgnoredMap().get("emptyReceiveNameError") )
			{
				isEmptyReceiveNameError = true;
			}
			/* 发货单没有填写收件人电话，并且没有取消验证 */
			if( review.getCheckMap().get("emptyReceivePhoneError") && ! review.getIgnoredMap().get("emptyReceivePhoneError") )
			{
				isEmptyReceivePhoneError = true;
			}
			/* 发货单没有填写收件人地址，并且没有取消验证 */
			if( review.getCheckMap().get("emptyReceiveAddressError") && ! review.getIgnoredMap().get("emptyReceiveAddressError") )
			{
				isEmptyReceiveAddressError = true;
			}
			
			/* 如果有一个验证不通过 */
			if( isEmptyCourierError ||
					isEmptyShipNumberError ||
					isEmptyReceiveNameError ||
					isEmptyReceivePhoneError ||
				isEmptyReceiveAddressError )
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

	/* 如果验证全都通过，并且操作类型是 CONFIRM 则执行完成操作 */
	@Transactional
	public void executeShipmentCompletion(OperationReviewShipmentDTO review)
	{
		/* 假设有可生成发货单的订单 */
		review.getResultMap().put("isEmptyFinalShipment", false);
		
		Shipment shipment = review.getShipment();
		
		/* 如果有可完成的发货单 */
		if( shipment != null )
		{
			/* 更新订单状态至：店铺的完成状态 */
			Order order = this.orderRepository.findOne( shipment.getOrderId() );
			
			Integer qtyTotalRecentDispatched = order.getQtyTotalItemShipped() + shipment.getQtyTotalItemShipped();
			
			/* 更新订单的［运送数量］ */
			this.orderRepository.updateQtyTotalItemShipped( qtyTotalRecentDispatched, order.getId() );
			
			/* 如果［当前总发货数量］大于等于［订购数量］ */
			if( qtyTotalRecentDispatched >= order.getQtyTotalItemOrdered() )
			{
				Long stepId = order.getShop().getCompleteStep().getId();
				Long processId = order.getShop().getCompleteStep().getProcessId();
				Integer objectType = order.getShop().getCompleteStep().getType();
				this.objectProcessRepository.updateStepId( stepId, order.getId(), processId, objectType);
			}
			
			
			/* 取得操作员信息 */
			Number excuteOperatorId = (Number) review.getDataMap().get("executeOperatorId");
			Long finalExecuteOperatorId = excuteOperatorId.longValue();
			
			/* 给发货单初始化数据 */
			Shipment finalShipment = shipment;
			
			/* 指定发货人 */
			finalShipment.setExecuteOperatorId( finalExecuteOperatorId );
			
//			shipment.setShipfeeCost( shipFeeCost );
			
			/* 所有数据已准备就绪，初始化［取件时间］和［最后更新时间］并完成发货单 */
			/* 2 : 已发出 */
			shipment.setShipStatus( 2 );
			shipment.setPickupTime( new Date() );
			shipment.setLastUpdate( new Date() );
			this.shipmentRepository.save(shipment);
				
			review.getResultMap().put( "generatedShipmentCount", 1 );
		}
		else
		{
			/* 如果没有最终发货单 */
			review.getResultMap().put( "isEmptyFinalShipment", true );
		}
	}

	@Transactional
	public OperationReviewShipmentDTO confirmOperationReviewWhenCompleteShipment(OperationReviewShipmentDTO review)
	{
		Shipment reviewShipment = review.getShipment();
		
		/* 验证 1 ： 是否指定快递公司 */
		if( reviewShipment.getCourierId() != null && ! reviewShipment.getCourierId().equals("") )
		{
			review.getCheckMap().put("emptyCourierError", false);
		}
		else
		{
			review.getCheckMap().put("emptyCourierError", true);
		}
		
		/* 验证 2 ： 是否指定快递单号 */
		if( reviewShipment.getShipNumber() != null && ! reviewShipment.getShipNumber().trim().equals("") )
		{
			review.getCheckMap().put("emptyShipNumberError", false);
		}
		else
		{
			review.getCheckMap().put("emptyShipNumberError", true);
		}
		
		/* 验证 3 ： 订单的收件人姓名是否为空 */
		if( reviewShipment.getReceiveName() != null && ! reviewShipment.getReceiveName().trim().equals("") )
		{
			review.getCheckMap().put("emptyReceiveNameError", false);
		}
		else
		{
			review.getCheckMap().put("emptyReceiveNameError", true);
		}

		/* 验证 4 ： 订单的收件人电话是否为空 */
		if( reviewShipment.getReceivePhone() != null && ! reviewShipment.getReceivePhone().trim().equals("") )
		{
			review.getCheckMap().put("emptyReceivePhoneError", false);
		}
		else
		{
			review.getCheckMap().put("emptyReceivePhoneError", true);
		}

		/* 验证 5 ： 订单的收件人地址是否为空 */
		if( reviewShipment.getReceiveAddress() != null && ! reviewShipment.getReceiveAddress().trim().equals("") )
		{
			review.getCheckMap().put("emptyReceiveAddressError", false);
		}
		else
		{
			review.getCheckMap().put("emptyReceiveAddressError", true);
		}
		
		/* 设置可确认性 */
		this.setConfirmable(review);
		
		
		/* 如果验证全都通过，并且操作类型是 CONFIRM 则执行完成操作 */
		if( review.isConfirmable() &&
			review.getAction().equals(OperationReviewDTO.CONFIRM) )
		{
			/* 执行完成发货单操作 */
			this.executeShipmentCompletion( review );
		}
		
		return review;
		
	}

	@Transactional
	public OperationReviewShipmentDTO confirmOperationReviewWhenCompleteShipments(OperationReviewShipmentDTO review)
	{
		List<Shipment> reviewShipments = review.getShipments();
		
		for( Shipment reviewShipment : reviewShipments )
		{
			/* 验证 1 ： 是否指定快递公司 */
			if( reviewShipment.getCourierId() != null && ! reviewShipment.getCourierId().equals("") )
			{
				review.getCheckMap().put("emptyCourierError", false);
			}
			else
			{
				review.getCheckMap().put("emptyCourierError", true);
			}
			
			/* 验证 2 ： 是否指定快递单号 */
			if( reviewShipment.getShipNumber() != null && ! reviewShipment.getShipNumber().trim().equals("") )
			{
				review.getCheckMap().put("emptyShipNumberError", false);
			}
			else
			{
				review.getCheckMap().put("emptyShipNumberError", true);
			}
			
			/* 验证 3 ： 订单的收件人姓名是否为空 */
			if( reviewShipment.getReceiveName() != null && ! reviewShipment.getReceiveName().trim().equals("") )
			{
				review.getCheckMap().put("emptyReceiveNameError", false);
			}
			else
			{
				review.getCheckMap().put("emptyReceiveNameError", true);
			}

			/* 验证 4 ： 订单的收件人电话是否为空 */
			if( reviewShipment.getReceivePhone() != null && ! reviewShipment.getReceivePhone().trim().equals("") )
			{
				review.getCheckMap().put("emptyReceivePhoneError", false);
			}
			else
			{
				review.getCheckMap().put("emptyReceivePhoneError", true);
			}

			/* 验证 5 ： 订单的收件人地址是否为空 */
			if( reviewShipment.getReceiveAddress() != null && ! reviewShipment.getReceiveAddress().trim().equals("") )
			{
				review.getCheckMap().put("emptyReceiveAddressError", false);
			}
			else
			{
				review.getCheckMap().put("emptyReceiveAddressError", true);
			}
		}
		
		/* 设置可确认性 */
		this.setConfirmable(review);
		
		
		/* 如果验证全都通过，并且操作类型是 CONFIRM 则执行完成操作 */
		if( review.isConfirmable() &&
			review.getAction().equals(OperationReviewDTO.CONFIRM) )
		{
			/* 执行完成发货单操作 */
			this.executeShipmentCompletion( review );
		}
		
		return review;
		
	}

	/* 批量导入发货单复核操作
	 */

	/* 设置是否通过 */
	public void setImportConfirmable( OperationReviewShipmentDTO review )
	{
		/* 如果验证全都通过 */
		if
		(
			! review.getCheckMap().get( "emptyMemoError" ) &&
			! review.getCheckMap().get( "emptyOrderError" )
		)
		{
			review.setConfirmable( true );
		}
		else
		{
			review.setConfirmable( false );
		}
	}

	
	/* 如果验证全都通过，并且操作类型是 CONFIRM 则执行完成操作 */
	@Transactional
	public void executeShipmentImport( OperationReviewShipmentDTO review )
	{
		/* 如果验证通过 */
		Long executeOperatorId = Long.valueOf( (Integer) review.getDataMap().get( "operatorId" ) );

		List<Shipment> reviewShipments = review.getShipments();
		List<Shipment> insertableShipments = new ArrayList<Shipment>();
		
		for( Shipment reviewShipment : reviewShipments )
		{
			if( ! reviewShipment.getIgnoreCheck() )
			{
				insertableShipments.add( reviewShipment );
			}
		}
		
		if( insertableShipments.size() > 0 )
		{
			for( Shipment shipment : insertableShipments )
			{
				shipment.setLastUpdate( new Date() );
				shipment.setExecuteOperatorId( executeOperatorId );
				shipment.setReceiveName( shipment.getReceiveName() != null ? shipment.getReceiveName() : "" );
				shipment.setReceiveEmail( shipment.getReceiveEmail() != null ? shipment.getReceiveEmail() : "" );
				
//				1. 如果发货单号，订单号，发货单状态匹配：
//					状态为［正常］：
//						改发货单快递单号
//						改发货单状态为［已发货］
//						改订单为［配送完成］
//						将 productContent 追加到 memo 上面
//						保存［备注］必填
//					状态为［异常］：
//						改发货单快递单号
//						发货单状态为［异常］
//						改订单状态为［错误］
//						将 productContent 追加到 memo 上面
//						保存［备注］必填
//				2. 如果没有匹配：
//					创建发货单：
//						继续 1. 的流程
				
				boolean isAllMatch = false;

				String sql = " SELECT * FROM t_shipment " +
							 " WHERE id = ?1 " +
							 " AND ship_status = ?2 " +
							 " AND order_id = ?3 ";
				Query query =  em.createNativeQuery( sql, Shipment.class );
				query.setParameter( 1, shipment.getId() );
				query.setParameter( 2, shipment.getShipStatus() );
				query.setParameter( 3, shipment.getOrderId() );
				if( ! query.getResultList().isEmpty() )
				{
					shipment = (Shipment) query.getSingleResult();
					isAllMatch = true;
				}
				
				/* 1. 如果发货单号，订单号，发货单状态匹配：
				 */
				if( isAllMatch )
				{
					boolean isStatusNormal = shipment.getShipStatus().equals( 1 );
					/* a. 状态为［正常］：
					 */
					if( isStatusNormal )
					{
						executeShipmentImportInitUpdate( shipment, 3 );
					}
					/* b. 状态为［异常］：
					 */
					else
					{
						executeShipmentImportInitUpdate( shipment, 5 );
					}
					
					this.shipmentRepository.save( shipment );
				}
				/* 2. 如果没有匹配：
				 */
				else
				{
					shipment.setCreateTime( new Date() );
					shipment.setQtyTotalItemShipped( 0 );
					shipment.setTotalWeight( 0 );
					shipment.setShipfeeCost( new BigDecimal( 0 ) );
					shipment.setOperatorId( executeOperatorId );
					shipment = this.shipmentRepository.save( shipment );
					
					boolean isStatusNormal = shipment.getShipStatus().equals( 1 );
					
					if( isStatusNormal )
					{
						executeShipmentImportInitUpdate( shipment, 3 );
					}
					else
					{
						executeShipmentImportInitUpdate( shipment, 5 );
					}
					
					this.shipmentRepository.save( shipment );
				}
				
			}
		}
		review.getResultMap().put( "insertableShipmentsSize", insertableShipments.size() );
	}

	@Transactional
	public void executeShipmentImportInitUpdate( Shipment shipment, Integer shipStatus )
	{
//		改发货单状态为［已发货］或者［异常］
		shipment.setShipStatus( shipStatus );

//		改订单为［配送完成］
		Order order = this.orderRepository.findOne( shipment.getOrderId() );
		
		List<ObjectProcess> processess = order.getProcesses();
		for ( ObjectProcess op: processess )
		{
			if
			(
				shipStatus == 3 &&
				op.getProcessId().longValue() == order.getShop().getCompleteStep().getProcessId().longValue()
			)
			{
				op.setStep(order.getShop().getCompleteStep());
				
				objectProcessRepository.save(op);
			}
			else if
			(
				shipStatus == 5 &&
				op.getProcessId().longValue() == order.getShop().getErrorStep().getProcessId().longValue()
			)
			{
				op.setStep( order.getShop().getErrorStep() );
				
				objectProcessRepository.save(op);
			}
		}
		
//		将 productContent 追加到 memo 上面，保存［备注］必填
		StringBuffer productContentBuffer = new StringBuffer();
		productContentBuffer.append( shipment.getProductContent() );
		productContentBuffer.append( shipment.getMemo() );
		shipment.setMemo( shipment.getMemo() );
	}

	@Transactional
	public void executeShipmentImportInit( OperationReviewShipmentDTO review )
	{
		List<Shipment> reviewShipments = review.getShipments();
		
		Integer normalStatusCount = 0;
		Integer excetionalStatusCount = 0;
		Integer orderNotMatchCount = 0;

		boolean isEmptyOrderError = false;
		for( Shipment shipment : reviewShipments )
		{
			/* 如果发货单被移出
			 */
			if( ! shipment.getIgnoreCheck() )
			{
				String sql = "SELECT COUNT(1) FROM t_order WHERE id = ?1 ";
				Query query =  em.createNativeQuery( sql );
				query.setParameter( 1, shipment.getOrderId() );
				BigInteger orderCount = (BigInteger) query.getSingleResult();
				if( orderCount.compareTo( BigInteger.ZERO )==0 )
				{
					shipment.getCheckMap().put( "emptyOrderError", true );
					
					orderNotMatchCount++;
					isEmptyOrderError = true;
				}
				/* 如果订单号匹配
				 */
				else
				{
					if( shipment.getShipStatus() != null )
					{
						switch( shipment.getShipStatus() )
						{
							case 1	:	normalStatusCount++; break;
							case 5	:	excetionalStatusCount++; break;
						}
					}
					else
					{
						review.getCheckMap().put( "emptyStatusError", true );
					}
					
					shipment.getCheckMap().put( "emptyOrderError", false );
				}
			}
			else
			{
				shipment.getCheckMap().put( "emptyOrderError", false );
			}
		}
		review.getCheckMap().put( "emptyOrderError", isEmptyOrderError );
		
		review.getResultMap().put( "normalStatusCount", normalStatusCount );
		review.getResultMap().put( "excetionalStatusCount", excetionalStatusCount );
		review.getResultMap().put( "orderNotMatchCount", orderNotMatchCount );
	}
	
	
	@Transactional
	public OperationReviewShipmentDTO confirmOperationReviewWhenImportShipments( OperationReviewShipmentDTO review )
	{
		List<Shipment> reviewShipments = review.getShipments();
		
		this.executeShipmentImportInit( review );

		/* 验证 1 ： 异常发货单是否填写［memo］注释 */
		boolean isExceptionMemoEmpty = false;
		for( Shipment reviewShipment : reviewShipments )
		{
			if
			(
				reviewShipment.getShipStatus().equals( 5 ) &&
				( reviewShipment.getMemo() == null || reviewShipment.getMemo().trim().equals("") ) &&
				! reviewShipment.getIgnoreCheck()
			)
			{
				reviewShipment.getCheckMap().put( "emptyMemoError", true );
				
				isExceptionMemoEmpty = true;
			}
			else
			{
				reviewShipment.getCheckMap().put( "emptyMemoError", false );
			}
		}
		review.getCheckMap().put( "emptyMemoError", isExceptionMemoEmpty );
		
		/* 设置可确认性 */
		this.setImportConfirmable( review );
		
		
		
		/* 如果验证全都通过，并且操作类型是 CONFIRM 则执行完成操作 */
		if
		(
			review.isConfirmable() &&
			review.getAction().equals( OperationReviewDTO.CONFIRM )
		)
		{
			/* 执行完成发货单操作 */
			this.executeShipmentImport( review );
		}
		
		return review;
		
	}
	
	
	
	/*
	 * ShipmentItem
	 */
	
	public ShipmentItem saveShipmentItem(ShipmentItem shipmentItem) {
		return this.shipmentItemRepository.save(shipmentItem);
	}
	
	public void deleteShipmentItem(Long id) {
		this.shipmentItemRepository.delete(id);
	}
	
	public ShipmentItem getShipmentItem(Long id) {
		return this.shipmentItemRepository.findOne(id);
	}
	
	public List<ShipmentItem> getShipmentItems() {
		return this.shipmentItemRepository.findAll();
	}

	public Page<ShipmentItem> getPagedShipmentItems(Pageable pageable) {
		return this.shipmentItemRepository.findAll(pageable);
	}
	
}