package com.sooeez.ecomm.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.domain.ProcessStep;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.domain.Process;
import com.sooeez.ecomm.dto.api.DTO_Product_Partner;
import com.sooeez.ecomm.dto.api.DTO_Product_Self;
import com.sooeez.ecomm.dto.api.general.DTO_Pagination;
import com.sooeez.ecomm.dto.api.general.DTO_Process_Status;
import com.sooeez.ecomm.repository.ProductRepository;

@Service
public class ProductService {
	
	@PersistenceContext 
	private EntityManager em;
	
	/*
	 * Repository
	 */

	@Autowired 
	private ProductRepository productRepository;
	
	@Autowired 
	private ProcessService processService;

	/*
	 * Product
	 */

	@Transactional
	public Product saveProduct(Product product) {
		
		if (product.getId() == null) {
			
			// set create time
			product.setCreateTime(new Date());
			
			// auto apply product's process 
			Process processQuery = new Process();
			processQuery.setObjectType(2);
			processQuery.setEnabled(true);
			List<Process> processes = processService.getProcesses(processQuery, null);
			if (processes != null && processes.size() > 0) {
				for (Process process : processes) {
					if (process.getAutoApply() == true) {
						ObjectProcess objectProcess = new ObjectProcess();
						objectProcess.setObjectType(2);
						objectProcess.setProcess(process);
						ProcessStep step = new ProcessStep();
						if (process.getDefaultStepId() != null) {
							step.setId(process.getDefaultStepId());
						} else {
							step.setId(process.getSteps().get(0).getId());
						}
						objectProcess.setStep(step);
						product.getProcesses().add(objectProcess);
					}
				}
			}
		} else {
			
			// set last update time
			product.setLastUpdate(new Date());
		}

		return productRepository.save(product);
	}

	@Transactional
	public void deleteProduct(Long id) {
		productRepository.delete(id);
	}
	
	public Boolean existsProduct(Product product) {
		return productRepository.count(getProductSpecification(product)) > 0 ? true : false;
	}

	public Product getProduct(Long id) {
		return productRepository.findOne(id);
	}

	public List<Product> getProducts(Product product, Sort sort) {
		return productRepository.findAll(getProductSpecification(product), sort);
	}

	public Page<Product> getPagedProducts(Product product, Pageable pageable) {
		return productRepository.findAll(getProductSpecification(product), pageable);
	}
	
	private Specification<Product> getProductSpecification(Product product) {
		
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (product.getEnabled() != null) {
				predicates.add(cb.equal(root.get("enabled"), product.getEnabled()));
			}
			if (product.getId() != null) {
				if (product.getCheckUnique() != null && product.getCheckUnique().booleanValue() == true) {
					predicates.add(cb.notEqual(root.get("id"), product.getId()));
				} else {
					predicates.add(cb.equal(root.get("id"), product.getId()));
				}
			}
			if (StringUtils.hasText(product.getSku())) {
				if (product.getCheckUnique() != null && product.getCheckUnique().booleanValue() == true) {
					predicates.add(cb.like(root.get("sku"), product.getSku()));
				} else {
					predicates.add(cb.like(root.get("sku"), "%" + product.getSku() + "%"));
				}
			}
			if (StringUtils.hasText(product.getName())) {
				if (product.getCheckUnique() != null && product.getCheckUnique().booleanValue() == true) {
					predicates.add(cb.like(root.get("name"), product.getName()));
				} else {
					predicates.add(cb.like(root.get("name"), "%" + product.getName() + "%"));
				}
			}
			if (product.getProductType() != null) {
				predicates.add(cb.equal(root.get("productType"), product.getProductType()));
			}
			if (product.getStatusIds() != null) {
				Subquery<ObjectProcess> objectProcessSubquery = query.subquery(ObjectProcess.class);
				Root<ObjectProcess> objectProcessRoot = objectProcessSubquery.from(ObjectProcess.class);
				objectProcessSubquery.select(objectProcessRoot.get("objectId"));
				objectProcessSubquery.where(objectProcessRoot.get("stepId").in((Object[]) product.getStatusIds()));
				predicates.add(cb.in(root.get("id")).value(objectProcessSubquery));
			}
			
			if (StringUtils.hasText(product.getNameOrSku())) {
				predicates.add(cb.or(cb.like(root.get("name"), "%" + product.getNameOrSku() + "%"), 
						cb.like(root.get("sku"), "%" + product.getNameOrSku() + "%")));
				return cb.and(predicates.toArray(new Predicate[predicates.size()]));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
	
	/*
	 * API Product
	 */
	
	@SuppressWarnings("unchecked")
	public void setAPIResponseProductDetail(Shop shop, Product product, DTO_Product_Self productSelf, DTO_Product_Partner productPartner)
	{
		/* 获得库存数量 */
		String inventoryQuantitySQL = "SELECT SUM(quantity) FROM t_inventory " +
									  "WHERE product_id = ?1";
		Query inventoryQuantityQuery = em.createNativeQuery( inventoryQuantitySQL );
		inventoryQuantityQuery.setParameter( 1, product.getId() );
		BigDecimal quantity = (BigDecimal) inventoryQuantityQuery.getSingleResult();
		
		/* 获得库存保质期 */
		String inventoryExpireDateSQL = "SELECT DATE_FORMAT(expire_date, '%Y-%m-%d %T') FROM t_inventory " +
									  	"WHERE product_id = ?1 " +
									  	"ORDER BY expire_date ASC";
		Query inventoryExpireDateQuery = em.createNativeQuery( inventoryExpireDateSQL );
		inventoryExpireDateQuery.setParameter( 1, product.getId() );
		List<String> expire_string_dates = (List<String>) inventoryExpireDateQuery.getResultList();
		
		
		/* 是否自营店 */
		Boolean isSelfRunShop = shop.getType() == 0 ? true : false;
		
		/* 是否自营 */
		if( isSelfRunShop )
		{
			productSelf.setId( product.getId() );
			productSelf.setSku( product.getSku() );
			productSelf.setName( product.getName() );
			productSelf.setDescription( product.getFullDescription() );
			productSelf.setShort_description( product.getShortDescription() );
			productSelf.setWeight( product.getWeight() );
			productSelf.setCreate_time( product.getCreateTime() );
			productSelf.setUpdated_time( product.getLastUpdate() );

			/* 获得流程状态 */
			List<DTO_Process_Status> dtoProcessStatus = new ArrayList<DTO_Process_Status>();
			for ( ObjectProcess objProcess : product.getProcesses() )
			{
				DTO_Process_Status processingState = new DTO_Process_Status();
				processingState.setName( objProcess.getProcess().getName() );
				processingState.setValue( objProcess.getStep().getName() );
				dtoProcessStatus.add( processingState );
			}
			productSelf.setStatus( dtoProcessStatus );

			/* 设置货币名称 */
			productSelf.setCurrecy( product.getCurrency()!=null ? product.getCurrency().getName() : null );
			
			/* 设置库存数量 */
			productSelf.setAvailable_stock( quantity.intValue() );
			
			/* 获得库存保质期 */
			productSelf.setRecent_expire_dates( expire_string_dates );
			
			/* 是自营，获取各等级价格 */
			productSelf.getPrices().put("level1", product.getPriceL1());
			productSelf.getPrices().put("level2", product.getPriceL2());
			productSelf.getPrices().put("level3", product.getPriceL3());
			productSelf.getPrices().put("level4", product.getPriceL4());
			productSelf.getPrices().put("level5", product.getPriceL5());
			productSelf.getPrices().put("level6", product.getPriceL6());
			productSelf.getPrices().put("level7", product.getPriceL7());
			productSelf.getPrices().put("level8", product.getPriceL8());
			productSelf.getPrices().put("level9", product.getPriceL9());
			productSelf.getPrices().put("level10", product.getPriceL10());
		}
		else
		{
			productPartner.setId( product.getId() );
			productPartner.setSku( product.getSku() );
			productPartner.setName( product.getName() );
			productPartner.setDescription( product.getFullDescription() );
			productPartner.setShort_description( product.getShortDescription() );
			productPartner.setWeight( product.getWeight() );
			productPartner.setCreate_time( product.getCreateTime() );
			productPartner.setUpdated_time( product.getLastUpdate() );

			/* 获得流程状态 */
			List<DTO_Process_Status> dtoProcessStatus = new ArrayList<DTO_Process_Status>();
			for ( ObjectProcess objProcess : product.getProcesses() )
			{
				DTO_Process_Status processingState = new DTO_Process_Status();
				processingState.setName( objProcess.getProcess().getName() );
				processingState.setValue( objProcess.getStep().getName() );
				dtoProcessStatus.add( processingState );
			}
			productPartner.setStatus( dtoProcessStatus );

			/* 设置货币名称 */
			productPartner.setCurrecy( product.getCurrency()!=null ? product.getCurrency().getName() : null );
			
			/* 设置库存数量 */
			productPartner.setAvailable_stock( quantity.intValue() );
			
			/* 获得库存保质期 */
			productPartner.setRecent_expire_date( expire_string_dates.get(0) );
			
			/* 是合作，获取店铺对应等级价格 */
			switch ( shop.getPriceLevel() )
			{
				case  1 : productPartner.setPrice( product.getPriceL1() ); break;
				case  2 : productPartner.setPrice( product.getPriceL2() ); break;
				case  3 : productPartner.setPrice( product.getPriceL3() ); break;
				case  4 : productPartner.setPrice( product.getPriceL4() ); break;
				case  5 : productPartner.setPrice( product.getPriceL5() ); break;
				case  6 : productPartner.setPrice( product.getPriceL6() ); break;
				case  7 : productPartner.setPrice( product.getPriceL7() ); break;
				case  8 : productPartner.setPrice( product.getPriceL8() ); break;
				case  9 : productPartner.setPrice( product.getPriceL9() ); break;
				case 10 : productPartner.setPrice( product.getPriceL10() ); break;
				default : break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setAPIRespondProducts(Shop shop, List<DTO_Product_Self> productsSelf, List<DTO_Product_Partner> productsPartner, DTO_Pagination page_context)
	{
		
		/* 1. 获得商品总数 */
		String sqlCount = "SELECT COUNT(*) FROM t_product " +
						  "WHERE id IN (" +
							  "SELECT product_id FROM t_inventory " +
							  "WHERE warehouse_id IN (" +
								  "SELECT warehouse_id FROM t_tunnel_warehouse " +
								  "WHERE tunnel_id IN (" +
									  "SELECT id FROM t_shop_tunnel " +
									  "WHERE shop_id = ?1 " +
								  ")" +
							  ")" +
						  ") AND deleted = false";
		Query queryCount = em.createNativeQuery(sqlCount);
		queryCount.setParameter(1, shop.getId());
		BigInteger total_number = (BigInteger) queryCount.getSingleResult();
		page_context.setTotal_number( total_number );
		
		/* 2. 获得产品信息 */
		String sql = "SELECT * FROM t_product " +
					 "WHERE id IN (" +
						 "SELECT product_id FROM t_inventory " +
						 "WHERE warehouse_id IN (" +
							 "SELECT warehouse_id FROM t_tunnel_warehouse " +
							 "WHERE tunnel_id IN (" +
								 "SELECT id FROM t_shop_tunnel " +
								 "WHERE shop_id = ?1 " +
							 ")" +
						 ")" +
					 ") AND deleted = false " +
					 "LIMIT ?2, ?3";
		Query query =  em.createNativeQuery( sql , Product.class);
		query.setParameter(1, shop.getId());
		query.setParameter(2, (page_context.getPage() <=1 ? 0 : page_context.getPage() - 1) * page_context.getPer_page());
		query.setParameter(3, page_context.getPer_page());
		
		/* 3. 是否有续页 */
		if( page_context.getPage() * page_context.getPer_page() >= page_context.getTotal_number().longValue() )
		{
			page_context.setHas_more_page( false );
		}
		else
		{
			page_context.setHas_more_page( true );
		}
		
		if( !query.getResultList().isEmpty() )
		{
			List<Product> resultList = query.getResultList();
			
			for (Product product : resultList)
			{
				DTO_Product_Self productSelf = new DTO_Product_Self();
				DTO_Product_Partner productPartner = new DTO_Product_Partner();
				
				setAPIResponseProductDetail(shop, product, productSelf, productPartner);
				
				productsSelf.add( productSelf );
				productsPartner.add( productPartner );
			}
		}
	}

	public void setAPIRespondProduct(Shop shop, Long id, String sku, DTO_Product_Self productSelf, DTO_Product_Partner productPartner)
	{
		String sql = "SELECT * FROM t_product " +
					 "WHERE (id = ?2 OR sku = ?3) " +
					 "AND id IN (" +
						 "SELECT product_id FROM t_inventory " +
						 "WHERE warehouse_id IN (" +
							 "SELECT warehouse_id FROM t_tunnel_warehouse " +
							 "WHERE tunnel_id IN (" +
								 "SELECT id FROM t_shop_tunnel " +
								 "WHERE shop_id = ?1 " +
							 ") " +
						 ") " +
					 ") " +
					 "AND deleted = false " +
					 "LIMIT 1";
		Query query =  em.createNativeQuery( sql , Product.class);
		query.setParameter(1, shop.getId());
		query.setParameter(2, id);
		query.setParameter(3, sku);
		
		if( !query.getResultList().isEmpty() )
		{
			Product product = (Product) query.getSingleResult();

			setAPIResponseProductDetail(shop, product, productSelf, productPartner);
		}
	}
	
	/*
	 * END API Product
	 */
	
}
