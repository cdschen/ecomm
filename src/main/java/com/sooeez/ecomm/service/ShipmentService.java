package com.sooeez.ecomm.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
				predicates.add( cb.between( root.get( dateFieldName ),
						new SimpleDateFormat("yyyy-MM-dd").parse( dateStart ),
						new SimpleDateFormat("yyyy-MM-dd").parse( dateEnd ) ) );
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
				predicates.add( cb.greaterThanOrEqualTo( root.get( dateFieldName ), 
						new SimpleDateFormat("yyyy-MM-dd").parse( dateStart ) ) );
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
				predicates.add( cb.lessThanOrEqualTo( root.get( dateFieldName ), 
						new SimpleDateFormat("yyyy-MM-dd").parse( dateEnd ) ) );
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
