package com.sooeez.ecomm.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.sooeez.ecomm.domain.PurchaseOrderItem;
import com.sooeez.ecomm.domain.Supplier;
import com.sooeez.ecomm.domain.SupplierProductCodeMap;
import com.sooeez.ecomm.repository.PurchaseOrderItemRepository;
import com.sooeez.ecomm.repository.PurchaseOrderRepository;
import com.sooeez.ecomm.repository.SupplierProductCodeMapRepository;

@Service
public class PurchaseOrderService {

	@Autowired PurchaseOrderRepository purchaseOrderRepository;
	
	@Autowired PurchaseOrderItemRepository purchaseOrderItemRepository;
	
	@Autowired SupplierProductCodeMapRepository supplierProductCodeMapRepository;
	
	@PersistenceContext private EntityManager em;
	
	/*
	 * PurchaseOrder
	 */
	
	public PurchaseOrder savePurchaseOrder(PurchaseOrder purchaseOrder) {
		/* If id not null then is edit action */
		if (purchaseOrder.getId() == null) {
			purchaseOrder.setCreateTime( new Date() );
		}
		/* execute no matter create or update */
		purchaseOrder.setLastUpdate( new Date() );
		
		Boolean isSupplierProductCodeChanged = false;
		
		if( purchaseOrder.getItems() != null )
		{
			for (PurchaseOrderItem item : purchaseOrder.getItems())
			{
//				System.out.println("item.getProduct().getName(): " + item.getProduct().getName());
//				System.out.println("item.getSupplierProductCode(): " + item.getSupplierProductCode());
				
				if( item.getSupplierProductCode() != null && ! item.getSupplierProductCode().trim().equals("") )
				{
					/* 1. 获得供应商产品编码信息 */
					String sql = "SELECT * FROM t_supplier_product_code_map " +
								 "WHERE product_id = ?1";
					Query query =  em.createNativeQuery( sql, SupplierProductCodeMap.class );
					query.setParameter( 1, item.getProduct().getId() );
					
					SupplierProductCodeMap supplierProductCodeMap = null;

					/* 2. 供应商产品编码是否存在于数据库中 */
					if( ! query.getResultList().isEmpty() )
					{
						/* 2.1 存在于数据库中，则对比传入的［采购单价］与［默认采购单价］是否一致 */
						supplierProductCodeMap = (SupplierProductCodeMap) query.getSingleResult();
						
//						System.out.println("item.getEstimatePurchaseUnitPrice(): " + item.getEstimatePurchaseUnitPrice());
//						System.out.println("supplierProductCodeMap.getDefaultPurchasePrice(): " + supplierProductCodeMap.getDefaultPurchasePrice());
						
						/* 2.1.1 传入的［采购单价］与［默认采购单价］不一致，则更新［采购单价］至［默认采购单价］ */
						if( item.getEstimatePurchaseUnitPrice() != null && item.getEstimatePurchaseUnitPrice().compareTo( supplierProductCodeMap.getDefaultPurchasePrice() ) != 0 )
						{
							isSupplierProductCodeChanged = true;
							supplierProductCodeMap.setDefaultPurchasePrice( item.getEstimatePurchaseUnitPrice() );
							
							this.supplierProductCodeMapRepository.save( supplierProductCodeMap );
						}
					}
					else
					{
						isSupplierProductCodeChanged = true;
						supplierProductCodeMap = new SupplierProductCodeMap();
						Product product = new Product();
						Supplier supplier = new Supplier();
						
						product.setId( item.getProduct().getId() );
						supplier.setId( purchaseOrder.getSupplier().getId() );

						supplierProductCodeMap.setProduct( product );
						supplierProductCodeMap.setSupplier( supplier );
						supplierProductCodeMap.setDefaultPurchasePrice( item.getEstimatePurchaseUnitPrice() );
						supplierProductCodeMap.setSupplierProductCode( item.getSupplierProductCode() );

						this.supplierProductCodeMapRepository.save( supplierProductCodeMap );
					}
				}
			}
		}
		
		PurchaseOrder returnedPurchaseOrder = this.purchaseOrderRepository.save(purchaseOrder);
		returnedPurchaseOrder.setIsSupplierProductCodeChanged( isSupplierProductCodeChanged );
		
		return returnedPurchaseOrder;
	}
	
	public void deletePurchaseOrder(Long id) {
		this.purchaseOrderRepository.delete(id);
	}
	
	public PurchaseOrder getPurchaseOrder(Long id) {
		PurchaseOrder purchaseOrder = this.purchaseOrderRepository.findOne(id);
		if( purchaseOrder.getItems() != null && purchaseOrder.getItems().size() > 0 )
		{
			for( PurchaseOrderItem purchaseOrderItem : purchaseOrder.getItems() )
			{
				/* 1. 获得供应商产品编码信息 */
				String sql = "SELECT * FROM t_supplier_product_code_map " +
							 "WHERE product_id = ?1";
				Query query =  em.createNativeQuery( sql, SupplierProductCodeMap.class );
				query.setParameter( 1, purchaseOrderItem.getProduct().getId() );
				
				SupplierProductCodeMap supplierProductCodeMap = null;

				/* 2. 供应商产品编码是否存在于数据库中 */
				if( ! query.getResultList().isEmpty() )
				{
					supplierProductCodeMap = (SupplierProductCodeMap) query.getSingleResult();
				}
				purchaseOrderItem.setSupplierProductCodeMap( supplierProductCodeMap );
			}
		}
		return purchaseOrder;
	}
	
	public List<PurchaseOrder> getPurchaseOrders(PurchaseOrder purchaseOrder, Sort sort) {
		return this.purchaseOrderRepository.findAll(getPurchaseOrderSpecification(purchaseOrder), sort);
	}

	public Page<PurchaseOrder> getPagedPurchaseOrders(PurchaseOrder purchaseOrder, Pageable pageable) {
		return this.purchaseOrderRepository.findAll(getPurchaseOrderSpecification(purchaseOrder), pageable);
	}
	
	private Specification<PurchaseOrder> getPurchaseOrderSpecification(PurchaseOrder purchaseOrder) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (purchaseOrder.getQueryPurchaseOrderId() != null) {
				predicates.add( cb.equal( root.get("id"), purchaseOrder.getQueryPurchaseOrderId() ) );
			}
			if ( purchaseOrder.getQuerySupplierId() != null ) {
				predicates.add( cb.equal(root.get("supplierId"), purchaseOrder.getQuerySupplierId() ) );
			}
			if ( purchaseOrder.getQueryCreatorId() != null ) {
				predicates.add( cb.equal(root.get("creatorId"), purchaseOrder.getQueryCreatorId() ) );
			}
			if (purchaseOrder.getQueryCreateTimeStart() != null && purchaseOrder.getQueryCreateTimeEnd() != null) {
				try {
					predicates.add(cb.between(root.get("createTime"),
							new SimpleDateFormat("yyyy-MM-dd").parse(purchaseOrder.getQueryCreateTimeStart()),
							new SimpleDateFormat("yyyy-MM-dd").parse(purchaseOrder.getQueryCreateTimeEnd())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (purchaseOrder.getQueryCreateTimeStart() != null) {
				try {
					predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), 
							new SimpleDateFormat("yyyy-MM-dd").parse(purchaseOrder.getQueryCreateTimeStart())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (purchaseOrder.getQueryCreateTimeEnd() != null) {
				try {
					predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), 
							new SimpleDateFormat("yyyy-MM-dd").parse(purchaseOrder.getQueryCreateTimeEnd())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
//			if (order.getStatusIds() != null) {
//				Subquery<ObjectProcess> objectProcessSubquery = query.subquery(ObjectProcess.class);
//				Root<ObjectProcess> objectProcessRoot = objectProcessSubquery.from(ObjectProcess.class);
//				objectProcessSubquery.select(objectProcessRoot.get("objectId"));
//				objectProcessSubquery.where(objectProcessRoot.get("stepId").in(order.getStatusIds()));
//				predicates.add(cb.in(root.get("id")).value(objectProcessSubquery));
//			}
//			if (order.getOrderIds() != null && order.getOrderIds().size() > 0) {
//				predicates.add(cb.in(root.get("id")).value(order.getOrderIds()));
//			}
			
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	/*
	 * PurchaseOrderItem
	 */
	
	public PurchaseOrderItem savePurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
		return this.purchaseOrderItemRepository.save(purchaseOrderItem);
	}

	public void deletePurchaseOrderItem(Long id) {
		this.purchaseOrderItemRepository.delete(id);
	}

	public PurchaseOrderItem getPurchaseOrderItem(Long id) {
		return this.purchaseOrderItemRepository.findOne(id);
	}

	public List<PurchaseOrderItem> getPurchaseOrderItems(Sort sort) {
		return this.purchaseOrderItemRepository.findAll(sort);
	}

	public Page<PurchaseOrderItem> getPagedPurchaseOrderItems(Pageable pageable) {
		return this.purchaseOrderItemRepository.findAll(pageable);
	}

	
	/*
	 * SupplierProductCodeMap
	 */
	
	public SupplierProductCodeMap saveSupplierProductCodeMap(SupplierProductCodeMap supplierProductCodeMap) {
		return this.supplierProductCodeMapRepository.save(supplierProductCodeMap);
	}

	public void deleteSupplierProductCodeMap(Long id) {
		this.supplierProductCodeMapRepository.delete(id);
	}

	public SupplierProductCodeMap getSupplierProductCodeMap(Long id) {
		return this.supplierProductCodeMapRepository.findOne(id);
	}

	public List<SupplierProductCodeMap> getSupplierProductCodeMaps(Sort sort) {
		return this.supplierProductCodeMapRepository.findAll(sort);
	}

	public Page<SupplierProductCodeMap> getPagedSupplierProductCodeMaps(Pageable pageable) {
		return this.supplierProductCodeMapRepository.findAll(pageable);
	}

	public SupplierProductCodeMap getSupplierProductCodeMap(String supplierProductCode)
	{
		String sql = "SELECT * FROM t_supplier_product_code_map " +
					 "WHERE supplier_product_code = ?1";
		Query query =  em.createNativeQuery( sql, SupplierProductCodeMap.class );
		query.setParameter( 1, supplierProductCode );
		
		SupplierProductCodeMap supplierProductCodeMap = null;

		/* 2. 供应商产品编码是否存在于数据库中 */
		if( ! query.getResultList().isEmpty() )
		{
			/* 2.1 存在与数据库中，则将获取的信息返回到前端 */
			supplierProductCodeMap = (SupplierProductCodeMap) query.getSingleResult();
		}
		return supplierProductCodeMap;
	}

	public SupplierProductCodeMap getSupplierProductCodeMapByProductId(Long productId)
	{
		String sql = "SELECT * FROM t_supplier_product_code_map " +
					 "WHERE product_id = ?1";
		Query query =  em.createNativeQuery( sql, SupplierProductCodeMap.class );
		query.setParameter( 1, productId );
		
		SupplierProductCodeMap supplierProductCodeMap = null;

		/* 2. 供应商产品编码是否存在于数据库中 */
		if( ! query.getResultList().isEmpty() )
		{
			/* 2.1 存在与数据库中，则将获取的信息返回到前端 */
			supplierProductCodeMap = (SupplierProductCodeMap) query.getSingleResult();
		}
		return supplierProductCodeMap;
	}
	
}
