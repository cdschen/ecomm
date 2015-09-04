package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.Order;
import com.sooeez.ecomm.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired private OrderRepository orderRepository;

	/*
	 * Order
	 */

	@Transactional
	public Order saveOrder(Order Order) {
		return this.orderRepository.save(Order);
	}

	@Transactional
	public void deleteOrder(Long id) {
		this.orderRepository.delete(id);
	}

	public Order getOrder(Long id) {
		return this.orderRepository.findOne(id);
	}

	public List<Order> getOrders(Order Order) {
		return this.orderRepository.findAll(getOrderSpecification(Order));
	}

	public Page<Order> getPagedOrders(Pageable pageable, Order Order) {
		return this.orderRepository.findAll(getOrderSpecification(Order), pageable);
	}
	
	private Specification<Order> getOrderSpecification(Order order) {
		
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (order.getDeleted()) {
				predicates.add(cb.equal(root.get("deleted"), true));
			} else {
				predicates.add(cb.equal(root.get("deleted"), false));
			}
			if (order.getId() != null) {
				predicates.add(cb.like(root.get("id"), "%" + order.getId() + "%"));
			}
			
			if(order.getInternalCreateTimeStart()!=null && order.getInternalCreateTimeEnd()!=null)
			{
				predicates.add(cb.between(root.get("internalCreateTime"), order.getInternalCreateTimeStart(), order.getInternalCreateTimeEnd()));
			}
			else if (order.getInternalCreateTimeStart() != null)
			{
				predicates.add(cb.greaterThanOrEqualTo(root.get("internalCreateTime"), order.getInternalCreateTimeStart()));
			}
			else if (order.getInternalCreateTimeEnd() != null)
			{
				predicates.add(cb.lessThanOrEqualTo(root.get("internalCreateTime"), order.getInternalCreateTimeEnd()));
			}
			
//			if (product.getProductType() != null) {
//				predicates.add(cb.equal(root.get("productType"), product.getProductType()));
//			}
//			if (product.getStatus() != null) {
//				Subquery<ObjectProcess> objectProcessSubquery = query.subquery(ObjectProcess.class);
//				Root<ObjectProcess> objectProcessRoot = objectProcessSubquery.from(ObjectProcess.class);
//				objectProcessSubquery.select(objectProcessRoot.get("objectId"));
//				objectProcessSubquery.where(objectProcessRoot.get("stepId").in(product.getStatus()));
//				predicates.add(cb.in(root.get("id")).value(objectProcessSubquery));
//			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
}
