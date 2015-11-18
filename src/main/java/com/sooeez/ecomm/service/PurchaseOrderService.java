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

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.PurchaseOrder;
import com.sooeez.ecomm.domain.PurchaseOrderItem;
import com.sooeez.ecomm.domain.Shipment;
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
	
	public PurchaseOrder savePurchaseOrder(PurchaseOrder purchaseOrder)
	{
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
	
	@SuppressWarnings("unchecked")
	public List<PurchaseOrder> getPurchaseOrdersByIds( Long[] ids )
	{
		List<PurchaseOrder> purchaseOrders = null;
		StringBuffer purchaseOrderIdsBuffer = new StringBuffer();
		for( Long purchaseOrderId : ids )
		{
			if( ! purchaseOrderIdsBuffer.toString().equals("") )
			{
				purchaseOrderIdsBuffer.append(", ");
			}
			purchaseOrderIdsBuffer.append( purchaseOrderId );
		}

		/* 1. 获得采购单集合 */
		String sqlPurchaseOrders = "SELECT * FROM t_purchase_order WHERE id IN( " + purchaseOrderIdsBuffer.toString() + " )";
		Query queryPurchaseOrders = em.createNativeQuery( sqlPurchaseOrders, Shipment.class );
		purchaseOrders = queryPurchaseOrders.getResultList();
		
		return purchaseOrders;
	}
	
	public void addPurchaseOrderToCell( PurchaseOrder purchaseOrder, Workbook workbook, Sheet sheet )
	{
    	/* 创建［发货单大标题］
    	 */
    	Row bigTitleRow = sheet.getRow( 0 );
    	Cell bigTitleRowCell = bigTitleRow.getCell( 0 );
    	bigTitleRowCell.setCellValue( "MDD --  " + purchaseOrder.getSupplier().getName() + "  Order   " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( new Date() ) );
    	
    	String[] contactContents =
    	{
    		"MDD", "", "Magic Group", "Magic Group Ltd (MDD).   Unit 1, 48 Ellice Road, Wairau Valley, Auckland",
    		"0800 - 999 899  or 09-9729611", "Candy ZHANG", "027 652 8888", "candy@mdd.co.nz", "Please Deliver between 11am~7pm.   Thanks for your help  :)"
    	};
    	for( int i = 0; i < contactContents.length; i++ )
    	{
        	Row contactContentRow = sheet.getRow( i + 1 );
        	Cell contactContentCell = contactContentRow.getCell( 1 );
//        	contactContentCell.setCellStyle( contactContentStyle );
        	contactContentCell.setCellValue( contactContents[ i ] );
    	}

		/* 初始化［发货单大标题］字体
		 */
    	Font itemContentFont = workbook.createFont();
    	itemContentFont.setColor( HSSFColor.BLACK.index );
    	itemContentFont.setBold( false );
    	itemContentFont.setFontName("宋体");
    	itemContentFont.setFontHeightInPoints( (short) 12 );
    	
    	Font itemContentRedFont = workbook.createFont();
    	itemContentRedFont.setColor( HSSFColor.RED.index );
    	itemContentRedFont.setBold( true );
    	itemContentRedFont.setFontName("宋体");
    	itemContentRedFont.setFontHeightInPoints( (short) 12 );

    	/* 初始化［发货单大标题］样式
    	 */
    	CellStyle itemContentCommonStyle = workbook.createCellStyle();
    	itemContentCommonStyle.setBorderBottom( CellStyle.BORDER_THIN );
    	itemContentCommonStyle.setBorderLeft( CellStyle.BORDER_THIN );
    	itemContentCommonStyle.setBorderRight( CellStyle.BORDER_THIN );
    	itemContentCommonStyle.setBorderTop( CellStyle.BORDER_THIN );
    	
    	CellStyle itemContentLeftStyle = workbook.createCellStyle();
    	itemContentLeftStyle.cloneStyleFrom( itemContentCommonStyle );
    	itemContentLeftStyle.setFont( itemContentFont );
    	itemContentLeftStyle.setAlignment( CellStyle.ALIGN_LEFT );
    	
    	CellStyle itemContentCenterStyle = workbook.createCellStyle();
    	itemContentCenterStyle.cloneStyleFrom( itemContentCommonStyle );
    	itemContentCenterStyle.setFont( itemContentFont );
    	itemContentCenterStyle.setAlignment( CellStyle.ALIGN_CENTER );
    	
    	CellStyle itemContentRedFontCenterStyle = workbook.createCellStyle();
    	itemContentRedFontCenterStyle.cloneStyleFrom( itemContentCommonStyle );
    	itemContentRedFontCenterStyle.setFont( itemContentRedFont );
    	itemContentRedFontCenterStyle.setAlignment( CellStyle.ALIGN_CENTER );
    	
    	CellStyle itemContentYellowBGCenterStyle = workbook.createCellStyle();
    	itemContentYellowBGCenterStyle.cloneStyleFrom( itemContentCommonStyle );
    	itemContentYellowBGCenterStyle.setFont( itemContentFont );
    	itemContentYellowBGCenterStyle.setAlignment( CellStyle.ALIGN_CENTER );
    	itemContentYellowBGCenterStyle.setFillForegroundColor( HSSFColor.YELLOW.index );
    	itemContentYellowBGCenterStyle.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
    	
    	CellStyle itemContentRedFontYellowBGCenterStyle = workbook.createCellStyle();
    	itemContentRedFontYellowBGCenterStyle.cloneStyleFrom( itemContentCommonStyle );
    	itemContentRedFontYellowBGCenterStyle.setFont( itemContentRedFont );
    	itemContentRedFontYellowBGCenterStyle.setAlignment( CellStyle.ALIGN_CENTER );
    	itemContentRedFontYellowBGCenterStyle.setFillForegroundColor( HSSFColor.YELLOW.index );
    	itemContentRedFontYellowBGCenterStyle.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
    	
    	
    	/* 添加采购单详情
    	 */
    	if( purchaseOrder.getItems() != null && purchaseOrder.getItems().size() > 0 )
    	{
    		Integer totalStartIndex = purchaseOrder.getItems().size() + 12;
    		BigDecimal totalExcGST = new BigDecimal( 0 );
    		for( int i = 0; i < purchaseOrder.getItems().size(); i++ )
    		{
    			PurchaseOrderItem item = purchaseOrder.getItems().get( i );
    			
	        	Row contactContentRow = sheet.createRow( i + 12 );
	        	
	        	String code = item.getSupplierProduct() != null ? item.getSupplierProduct().getSupplierProductCode() : "";
	        	String description = item.getSupplierProduct() != null ? item.getSupplierProduct().getSupplierProductName() : "";
	       
	        	/* Code( supplier_product_code ), Description( supplier_product_name ) 居左
	        	 */
	        	setCell( contactContentRow, itemContentLeftStyle, code, 0 );
	        	setCell( contactContentRow, itemContentLeftStyle, description, 1 );
	        	
	        	/* Chinese Name( short_name ), size( 留空 ), Barcode( product.barcode || supplierProduct.barcode ),
	        	 * RRP inc( 留空 ), Std W/S Exc( 不含税采购价，estimate_purchase_unit_price ), Std W/S Inc( 含税采购价，estimate_purchase_unit_price * 1.15 )
	        	 * Order Qty( 留空 ), Carton Size( 留空 ), Total( 采购数量，purchase_qty ＊ 不含税采购价，estimate_purchase_unit_price ) 居中
	        	 * 
	        	 * ［含税采购价 红字体］，［Order Qty 黄背景］
	        	 */
	        	
	        	String chineseName = item.getSupplierProduct() != null && item.getSupplierProduct().getProduct() != null ? item.getSupplierProduct().getProduct().getShortName() : "";
	        	String size = "";
	        	String barcode = item.getSupplierProduct() != null && item.getSupplierProduct().getProduct() != null ? item.getSupplierProduct().getProduct().getBarcode() :
	        					 item.getSupplierProduct() != null ? item.getSupplierProduct().getSupplierProductBarcode(): "";
	        	String rrp_inc = "";
	        	BigDecimal excGST = item.getEstimatePurchaseUnitPrice() != null ? item.getEstimatePurchaseUnitPrice() : new BigDecimal( 0 );
	        	BigDecimal incGST = excGST.multiply( new BigDecimal( 115 ) ).divide( new BigDecimal( 100 ) );
	        	String std_w_s_exc = excGST.toString();
	        	String std_w_s_inc = incGST.toString().substring( 0, incGST.toString().indexOf(".") + 3 );
	        	String order_qty = item.getPurchaseQty() != null ? item.getPurchaseQty().toString() : "0";
	        	String carton_size = "";
	        	Long purchaseQty = item.getPurchaseQty() != null ? item.getPurchaseQty() : 0L;
	        	BigDecimal subTotal = excGST.multiply( new BigDecimal( purchaseQty ) );
	        	String total = subTotal.toString().substring( 0, subTotal.toString().indexOf(".") + 3 );

	        	setCell( contactContentRow, itemContentCenterStyle, chineseName, 2 );
	        	setCell( contactContentRow, itemContentCenterStyle, size, 3 );
	        	setCell( contactContentRow, itemContentCenterStyle, barcode, 4 );
	        	setCell( contactContentRow, itemContentCenterStyle, rrp_inc, 5 );
	        	setCell( contactContentRow, itemContentCenterStyle, std_w_s_exc, 6 );
	        	setCell( contactContentRow, itemContentRedFontCenterStyle, std_w_s_inc, 7 );
	        	setCell( contactContentRow, itemContentYellowBGCenterStyle, order_qty, 8 );
	        	setCell( contactContentRow, itemContentCenterStyle, carton_size, 9 );
	        	setCell( contactContentRow, itemContentCenterStyle, total, 10 );
	        	
	        	totalExcGST = totalExcGST.add( subTotal );
    		}

			/* 创建采购单详情总计
			 */
        	Row totalFirstRow = sheet.createRow( totalStartIndex );
    		setCell( totalFirstRow, itemContentCenterStyle, "Exc. GST", 9);
    		setCell( totalFirstRow, itemContentCenterStyle, totalExcGST.toString(), 10);
    		
    		BigDecimal gst = totalExcGST.multiply( new BigDecimal( 15 ) ).divide( new BigDecimal( 100 ) );
    		Row totalSecondRow = sheet.createRow( totalStartIndex + 1 );
    		setCell( totalSecondRow, itemContentCenterStyle, "GST", 9);
    		setCell( totalSecondRow, itemContentCenterStyle, gst.toString().substring( 0, gst.toString().indexOf(".") + 3 ), 10);
    		
    		BigDecimal total = totalExcGST.add( gst );
    		Row totalThirdRow = sheet.createRow( totalStartIndex + 2 );
    		setCell( totalThirdRow, itemContentRedFontYellowBGCenterStyle, "Total", 9);
    		setCell( totalThirdRow, itemContentRedFontYellowBGCenterStyle, total.toString().substring( 0, total.toString().indexOf(".") + 3 ), 10);
    	}
    	
    	
	}
	
	public void setCell( Row row, CellStyle cellStyle, String cellValue, Integer index )
	{
    	Cell cell = row.createCell( index );
    	cell.setCellStyle( cellStyle );
    	cell.setCellValue( cellValue );
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
