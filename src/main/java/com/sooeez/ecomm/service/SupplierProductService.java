package com.sooeez.ecomm.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.SupplierProduct;
import com.sooeez.ecomm.repository.SupplierProductRepository;

@Service
public class SupplierProductService {

	@Autowired SupplierProductRepository supplierProductRepository;
	
	// Service
	@PersistenceContext private EntityManager em;
	
	/*
	 * Tag
	 */
	
	public SupplierProduct saveSupplierProduct(SupplierProduct supplierProduct) {
		/* If id equals to null then is add action */
		if( supplierProduct.getId() == null )
		{
			supplierProduct.setCreateTime( new Date() );
		}
		supplierProduct.setLastUpdate( new Date() );
		return this.supplierProductRepository.save( supplierProduct );
	}
	
	public void deleteSupplierProduct(Long id) {
		this.supplierProductRepository.delete(id);
	}
	
	public SupplierProduct getSupplierProduct(Long id) {
		return this.supplierProductRepository.findOne(id);
	}
	
	public List<SupplierProduct> getSupplierProducts(SupplierProduct supplierProduct, Sort sort) {
		return this.supplierProductRepository.findAll( getSupplierProductSpecification( supplierProduct ) , sort);
	}

	public Page<SupplierProduct> getPagedSupplierProducts(SupplierProduct supplierProduct, Pageable pageable) {
		return this.supplierProductRepository.findAll( getSupplierProductSpecification( supplierProduct ) , pageable);
	}
	
	private Specification<SupplierProduct> getSupplierProductSpecification(SupplierProduct supplierProduct)
	{
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			
			/* 采购单详情模糊查询 */
			if( StringUtils.hasText( supplierProduct.getQueryPurchaseOrderItemFuzzySearchParam() ) )
			{
				Subquery<Product> productSubquery = query.subquery( Product.class );
				Root<Product> productRoot = productSubquery.from( Product.class );
				productSubquery.select( productRoot.get("id") );
				productSubquery.where
				(
					cb.or
					(
						/* 模糊匹配产品： */
						/* sku */
						/* barcode */
						/* name */
						cb.like( productRoot.get("sku"), "%" + supplierProduct.getQueryPurchaseOrderItemFuzzySearchParam() + "%" ),
						cb.like( productRoot.get("barcode"), "%" + supplierProduct.getQueryPurchaseOrderItemFuzzySearchParam() + "%" ),
						cb.like( productRoot.get("name"), "%" + supplierProduct.getQueryPurchaseOrderItemFuzzySearchParam() + "%" )
					)
				);
				predicates.add
				(
					cb.or
					(
						/* 或者产品编号匹配 */
						cb.in( root.get("productId") ).value(productSubquery),
							
						/* 模糊匹配供应商产品： */
						/* supplierProductCode */
						/* supplierProductBarcode */
						/* supplierProductName */
						cb.like( root.get("supplierProductCode"), "%" + supplierProduct.getQueryPurchaseOrderItemFuzzySearchParam() + "%" ),
						cb.like( root.get("supplierProductBarcode"), "%" + supplierProduct.getQueryPurchaseOrderItemFuzzySearchParam() + "%" ),
						cb.like( root.get("supplierProductName"), "%" + supplierProduct.getQueryPurchaseOrderItemFuzzySearchParam() + "%" )
					)
				);
			}
			
			if (supplierProduct.getQuerySupplierId() != null)
			{
				predicates.add( cb.equal( root.get("supplierId"), supplierProduct.getQuerySupplierId() ) );
			}
			
			if (supplierProduct.getQueryCreatorId() != null)
			{
				predicates.add( cb.equal( root.get("creatorId"), supplierProduct.getQueryCreatorId() ) );
			}
			
			if ( StringUtils.hasText( supplierProduct.getQueryProductBarcode() ) )
			{
				Subquery<Product> productSubquery = query.subquery( Product.class );
				Root<Product> productRoot = productSubquery.from( Product.class );
				productSubquery.select( productRoot.get("id") );
				productSubquery.where( cb.like( productRoot.get("barcode"), supplierProduct.getQueryProductBarcode() ) );
				predicates.add( cb.in( root.get("productId") ).value(productSubquery) );
			}
			
			if ( StringUtils.hasText( supplierProduct.getQuerySupplierProductCode() ) )
			{
				predicates.add( cb.like( root.get("supplierProductCode"), "%" + supplierProduct.getQuerySupplierProductCode() + "%" ) );
			}
			
			if ( StringUtils.hasText( supplierProduct.getQuerySupplierProductName() ) )
			{
				predicates.add( cb.like( root.get("supplierProductName"), "%" + supplierProduct.getQuerySupplierProductName() + "%" ) );
			}
			
			if (supplierProduct.getQueryCreateTimeStart() != null && supplierProduct.getQueryCreateTimeEnd() != null)
			{
				try {
					predicates.add(cb.between(root.get("createTime"),
							new SimpleDateFormat("yyyy-MM-dd").parse(supplierProduct.getQueryCreateTimeStart()),
							new SimpleDateFormat("yyyy-MM-dd").parse(supplierProduct.getQueryCreateTimeEnd())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (supplierProduct.getQueryCreateTimeStart() != null) {
				try {
					predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), 
							new SimpleDateFormat("yyyy-MM-dd").parse(supplierProduct.getQueryCreateTimeStart())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (supplierProduct.getQueryCreateTimeEnd() != null) {
				try {
					predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), 
							new SimpleDateFormat("yyyy-MM-dd").parse(supplierProduct.getQueryCreateTimeEnd())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			if (supplierProduct.getQueryLastUpdateStart() != null && supplierProduct.getQueryLastUpdateEnd() != null)
			{
				try {
					predicates.add(cb.between(root.get("lastUpdate"),
							new SimpleDateFormat("yyyy-MM-dd").parse(supplierProduct.getQueryLastUpdateStart()),
							new SimpleDateFormat("yyyy-MM-dd").parse(supplierProduct.getQueryLastUpdateEnd())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (supplierProduct.getQueryLastUpdateStart() != null) {
				try {
					predicates.add(cb.greaterThanOrEqualTo(root.get("lastUpdate"), 
							new SimpleDateFormat("yyyy-MM-dd").parse(supplierProduct.getQueryLastUpdateStart())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (supplierProduct.getQueryLastUpdateEnd() != null) {
				try {
					predicates.add(cb.lessThanOrEqualTo(root.get("lastUpdate"), 
							new SimpleDateFormat("yyyy-MM-dd").parse(supplierProduct.getQueryLastUpdateEnd())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
	
}
