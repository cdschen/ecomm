package com.sooeez.ecomm.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.Order;
import com.sooeez.ecomm.domain.Shipment;
import com.sooeez.ecomm.domain.ShipmentItem;
import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.dto.OperationReviewDTO;
import com.sooeez.ecomm.dto.OperationReviewShipmentDTO;
import com.sooeez.ecomm.repository.OrderRepository;
import com.sooeez.ecomm.repository.ShipmentItemRepository;
import com.sooeez.ecomm.repository.ShipmentRepository;
import com.sooeez.ecomm.repository.ShopRepository;

@Service
public class ShipmentService {

	@Autowired private ShopRepository shopRepository;

	@Autowired private OrderRepository orderRepository;

	@Autowired private ShipmentRepository shipmentRepository;

	@Autowired private ShipmentItemRepository shipmentItemRepository;
	
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
	public void executeShipmentCompletion(OperationReviewShipmentDTO review)
	{
		/* 假设有可生成发货单的订单 */
		review.getResultMap().put("isEmptyFinalShipment", false);
		
		Shipment shipment = review.getShipment();
		
		/* 如果有可完成的发货单 */
		if( shipment != null )
		{
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
