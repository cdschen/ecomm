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

import com.sooeez.ecomm.domain.PurchaseOrder;
import com.sooeez.ecomm.domain.PurchaseOrderItem;
import com.sooeez.ecomm.domain.Supplier;
import com.sooeez.ecomm.domain.SupplierProduct;
import com.sooeez.ecomm.domain.User;
import com.sooeez.ecomm.repository.PurchaseOrderItemRepository;
import com.sooeez.ecomm.repository.PurchaseOrderRepository;
import com.sooeez.ecomm.repository.SupplierProductRepository;

@Service
public class PurchaseOrderService {

	@Autowired PurchaseOrderRepository purchaseOrderRepository;
	
	@Autowired PurchaseOrderItemRepository purchaseOrderItemRepository;
	
	@Autowired SupplierProductRepository supplierProductRepository;
	
	@PersistenceContext private EntityManager em;
	
	/*
	 * PurchaseOrder
	 */
	
	public List<PurchaseOrder> savePurchaseOrders( PurchaseOrder purchaseOrder )
	{
		return this.purchaseOrderRepository.save( purchaseOrder.getPurchaseOrders() );
	}
	
	public PurchaseOrder savePurchaseOrder(PurchaseOrder purchaseOrder) {
		/* If id equals to null then is add action */
		if (purchaseOrder.getId() == null)
		{
			purchaseOrder.setCreateTime( new Date() );
			purchaseOrder.setStatus( 1 );	// 初始状态为：待收货
		}
		/* execute no matter create or update */
		purchaseOrder.setLastUpdate( new Date() );

		Boolean isSupplierProductCodeChanged = false;
		
		if( purchaseOrder.getItems() != null )
		{
			List<SupplierProduct> allSupplierProducts = new ArrayList<SupplierProduct>();
			
			for ( PurchaseOrderItem item : purchaseOrder.getItems() )
			{
				if
				(
					item.getSupplierProduct() != null &&
					item.getSupplierProduct().getSupplierProductCode() != null
				)
				{
					/* 1. 获得供应商产品编码信息 */
					String sql = "SELECT * FROM t_supplier_product " +
								 "WHERE supplier_product_code = ?1";
					Query query =  em.createNativeQuery( sql, SupplierProduct.class );
					query.setParameter( 1, item.getSupplierProduct().getSupplierProductCode() );
					
					SupplierProduct supplierProduct = null;

					/* 2. 供应商产品编码是否存在于数据库中 */
					if( ! query.getResultList().isEmpty() )
					{
						/* 2.1 存在于数据库中，则对比传入的［采购单价］与［默认采购单价］是否一致 */
						supplierProduct = (SupplierProduct) query.getSingleResult();
						
//						System.out.println("item.getEstimatePurchaseUnitPrice(): " + item.getEstimatePurchaseUnitPrice());
//						System.out.println("supplierProductCodeMap.getDefaultPurchasePrice(): " + supplierProductCodeMap.getDefaultPurchasePrice());
						
						/* 2.1.1 传入的［采购单价］与［默认采购单价］或［供应商编号］与数据库存储的［供应商编号］不一致，则更新［采购单价］或［供应商编号］到数据库 */
						if
						(
							( item.getEstimatePurchaseUnitPrice() != null && supplierProduct.getDefaultPurchasePrice() != null && item.getEstimatePurchaseUnitPrice().compareTo( supplierProduct.getDefaultPurchasePrice() ) != 0 ) ||
							( ! item.getSupplierProduct().getSupplierProductCode().trim().equals( supplierProduct.getSupplierProductCode() ) )
						)
						{
							isSupplierProductCodeChanged = true;
							
							supplierProduct.setDefaultPurchasePrice( item.getEstimatePurchaseUnitPrice() );
							supplierProduct.setSupplierProductCode( item.getSupplierProduct().getSupplierProductCode() );
						}
					}
					else
					{
						isSupplierProductCodeChanged = true;
						
						supplierProduct = new SupplierProduct();
						Supplier supplier = new Supplier();
						User creator = new User();
						
						supplier.setId( purchaseOrder.getSupplier().getId() );
						creator.setId( purchaseOrder.getCreator().getId() );

						supplierProduct.setSupplier( supplier );
						supplierProduct.setSupplierProductName( item.getSupplierProduct() != null ? item.getSupplierProduct().getSupplierProductName().trim() : null );
						supplierProduct.setSupplierProductCode( item.getSupplierProduct() != null ? item.getSupplierProduct().getSupplierProductCode().trim() : null );
						supplierProduct.setDefaultPurchasePrice( item.getEstimatePurchaseUnitPrice() );

						supplierProduct.setCreateTime( new Date() );
						supplierProduct.setLastUpdate( new Date() );
						supplierProduct.setCreator( creator );
					}

					item.setSupplierProduct( null );

					/* 临时存储，在［供应商产品］列表插入及更新完成，再通过临时存储的［采购详情］列表批量更新刚刚插入或更新到［供应商产品］表里的数据 */
					Long currentTimeMillis = System.currentTimeMillis();
					
					item.setCurrentTimeMillis( currentTimeMillis );
					supplierProduct.setCurrentTimeMillis( currentTimeMillis );
					
					allSupplierProducts.add( supplierProduct );
				}
			}
			
			/* 批量插入或更新［供应商产品］ */
			if( allSupplierProducts.size() > 0 )
			{
				allSupplierProducts = this.supplierProductRepository.save( allSupplierProducts );
			}
			

			for ( PurchaseOrderItem item : purchaseOrder.getItems() )
			{
				/* 匹配［采购详情］与［供应商产品］ */
				for( SupplierProduct allSupplierProduct : allSupplierProducts )
				{
					if( item.getCurrentTimeMillis().equals( allSupplierProduct.getCurrentTimeMillis() ) )
					{
						SupplierProduct supplierProduct = new SupplierProduct();
						supplierProduct.setId( allSupplierProduct.getId() );
						
						item.setSupplierProduct( supplierProduct );
					}
				}
			}
		}
		PurchaseOrder returnedPurchaseOrder = this.purchaseOrderRepository.save( purchaseOrder );
		returnedPurchaseOrder.setIsSupplierProductCodeChanged( isSupplierProductCodeChanged );
		return returnedPurchaseOrder;
	}
	
	public void deletePurchaseOrder(Long id) {
		this.purchaseOrderRepository.delete(id);
	}
	
	public PurchaseOrder getPurchaseOrder(Long id) {
		return this.purchaseOrderRepository.findOne(id);
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
	
	public SupplierProduct saveSupplierProduct(SupplierProduct supplierProductCodeMap) {
		return this.supplierProductRepository.save(supplierProductCodeMap);
	}

	public void deleteSupplierProduct(Long id) {
		this.supplierProductRepository.delete(id);
	}

	public SupplierProduct getSupplierProduct(Long id) {
		return this.supplierProductRepository.findOne(id);
	}

	public List<SupplierProduct> getSupplierProducts(Sort sort) {
		return this.supplierProductRepository.findAll(sort);
	}

	public Page<SupplierProduct> getPagedSupplierProducts(Pageable pageable) {
		return this.supplierProductRepository.findAll(pageable);
	}
	
}
