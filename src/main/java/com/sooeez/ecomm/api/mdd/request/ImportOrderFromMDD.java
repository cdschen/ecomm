package com.sooeez.ecomm.api.mdd.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.api.mdd.EcommHttp;
import com.sooeez.ecomm.api.mdd.constant.OrderStatus;
import com.sooeez.ecomm.api.mdd.constant.OrderStorage;
import com.sooeez.ecomm.api.mdd.constant.RespondCode;
import com.sooeez.ecomm.api.mdd.respond.OrderGood;
import com.sooeez.ecomm.api.mdd.respond.OrderInfo;
import com.sooeez.ecomm.api.mdd.respond.OrderList;
import com.sooeez.ecomm.domain.Currency;
import com.sooeez.ecomm.domain.ObjectProcess;
import com.sooeez.ecomm.domain.Order;
import com.sooeez.ecomm.domain.OrderItem;
import com.sooeez.ecomm.domain.Process;
import com.sooeez.ecomm.domain.ProcessStep;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.repository.OrderRepository;
import com.sooeez.ecomm.service.ProcessService;

@Service
public class ImportOrderFromMDD
{
	/*
	 * Repository
	 */
	@Autowired
	private OrderRepository	orderRepository;
	/*
	 * Service
	 */
	@Autowired
	private ProcessService	processService;
	@PersistenceContext
	private EntityManager	em;

	@Transactional
	public void importOrderFromMDD() throws Exception
	{
		List< Order > finalOrders = new ArrayList< Order >();

		Map< String, Object > configMap = new HashMap< String, Object >();

		List< Integer > status = new ArrayList< Integer >();
		status.add( OrderStatus.WAREHOUSE_TRANSIT );
		status.add( OrderStatus.PENDING_PAYMENT );
		configMap.put( "status", status );

		/**
		 * 开始：调用 获取 API 订单数据
		 */

		/*
		 * 获取指定仓库的订单
		 */
		for ( Integer state : status )
		{
			/*
			 * 调用订单编号列表 API
			 */
			EcommHttp orderListHttp = new EcommHttp();
			orderListHttp.getParamsMap().put( "url", "http://www.mdd.co.nz/mddAPI/OrderList.php" );
			orderListHttp.getParamsMap().put( "storage", OrderStorage.NZ_STORAGE );
			orderListHttp.getParamsMap().put( "status", state );

			OrderList orderList = new OrderList( orderListHttp.getJSONObject() );

			/*
			 * 如果调用成功，则获得所有订单编号
			 */
			if ( orderList.getSuccessCode().equals( RespondCode.SUCCESS_TRUE ) )
			{
				/*
				 * 如果有订单编号，则获取订单及详情信息
				 */
				if ( orderList.getContentMap() != null && orderList.getContentMap().size() > 0 )
				{
					List< OrderInfo > orderInfos = new ArrayList< OrderInfo >();

					for ( Map< String, Object > map : orderList.getContentMap() )
					{
						Long externalSn = ( Long ) map.get( "order_id" );
						Long externalLogTime = ( Long ) map.get( "log_time" );
						/*
						 * 如果［没匹配到］或［外部订单日志时间］不一致，则该订单需要被插入或更新至数据库
						 */
						Boolean isNotExistedOrUpdated = false;
						Long ecommOrderId = null;
						String sqlOrder = "SELECT * FROM t_order WHERE external_sn = ?1";
						Query queryOrder = em.createNativeQuery( sqlOrder, Order.class );
						queryOrder.setParameter( 1, externalSn );
						/*
						 * 如果存在该订单
						 */
						if ( queryOrder.getResultList().size() > 0 )
						{
							Order order = ( Order ) queryOrder.getSingleResult();
							/*
							 * 如果订单［外部日志时间］和接收到的［日志时间］不匹配，则该订单再外部系统有更新，
							 * 同时更新该订单信息到我们的系统
							 */
							if ( order.getExternalLogTime() != null &&
								externalLogTime != null && ! order.getExternalLogTime().equals( externalLogTime ) )
							{
								ecommOrderId = order.getId();
								isNotExistedOrUpdated = true;
							}
						}
						else
						{
							isNotExistedOrUpdated = true;
						}
						if ( isNotExistedOrUpdated )
						{
							EcommHttp orderInfoHttp = new EcommHttp();
							orderInfoHttp.getParamsMap().put( "url", "http://www.mdd.co.nz/mddAPI/OrderInfo.php" );
							orderInfoHttp.getParamsMap().put( "order_id", externalSn );
							/*
							 * 默认获取非预订订单
							 */
							orderInfoHttp.getParamsMap().put( "pre", 0 );

							OrderInfo orderInfo = new OrderInfo( orderInfoHttp.getJSONObject() );
							orderInfo.setEcommOrderId( ecommOrderId );
							orderInfo.setLogTime( externalLogTime );

							orderInfos.add( orderInfo );
						}
					}

					/**
					 * 开始：初始化订单及详情信息
					 */
					if ( orderInfos != null && orderInfos.size() > 0 )
					{
						Shop mddShop = new Shop();
						mddShop.setId( 2L );

						// ( id:100:NZD, id:101:RMB )
						// ( 0：纽币,1：人民币 )
						Currency nzdCurrency = new Currency();
						nzdCurrency.setId( 100L );
						Currency rmbCurrency = new Currency();
						rmbCurrency.setId( 101L );

						for ( OrderInfo orderInfo : orderInfos )
						{
							/*
							 * 如果该订单任意一个详情的 sku 不存在于 Ecomm 中，则不予以导入
							 */
							Boolean isAllItemSkuFoundInEcommProduct = true;
							Boolean isDeliveryMethodCorrect = true;

							Order order = new Order();
							order.setId( orderInfo.getEcommOrderId() );

							Process processQuery = new Process();
							processQuery.setObjectType( 1 );
							processQuery.setEnabled( true );
							List< Process > processes = this.processService.getProcesses( processQuery, null );
							if ( processes != null && processes.size() > 0 )
							{
								for ( Process process : processes )
								{
									if ( process.getAutoApply() == true )
									{
										ObjectProcess objectProcess = new ObjectProcess();
										objectProcess.setObjectType( 1 );
										objectProcess.setProcess( process );
										ProcessStep step = new ProcessStep();
										if ( process.getDefaultStepId() != null )
										{
											step.setId( process.getDefaultStepId() );
										}
										else
										{
											step.setId( process.getSteps().get( 0 ).getId() );
										}
										objectProcess.setStep( step );
										order.setProcesses( new ArrayList< ObjectProcess >() );
										order.getProcesses().add( objectProcess );
									}
								}
							}

							Integer qtyTotalItemOrdered = 0;
							Integer weight = 0;
							BigDecimal grandTotal = new BigDecimal( 0 );
							BigDecimal subtotal = new BigDecimal( 0 );
							BigDecimal tax = new BigDecimal( 0 );

							List< OrderItem > orderItems = new ArrayList< OrderItem >();

							if ( orderInfo.getGoods() != null && orderInfo.getGoods().size() > 0 )
							{
								for ( OrderGood orderGood : orderInfo.getGoods() )
								{
									OrderItem orderItem = new OrderItem();
									Integer goodsNumber = orderGood.getGoodsNumber() != null
										? orderGood.getGoodsNumber().intValue() : 0;

									orderItem.setQtyOrdered( goodsNumber );
									orderItem.setExternalSku( orderGood.getGoodsSn() );
									orderItem.setSku( orderGood.getGoodsSn() );
									orderItem.setExternal_name( orderGood.getGoodsName() );

									Boolean isAvailable = false;
									String sqlProduct = "SELECT * FROM t_product WHERE sku = ?1 AND enabled = true";
									Query queryProduct = em.createNativeQuery( sqlProduct, Product.class );
									queryProduct.setParameter( 1, orderItem.getSku() );
									if ( queryProduct.getResultList().size() > 0 )
									{
										// Integer totalStock = 0;
										Product product = ( Product ) queryProduct.getSingleResult();
										orderItem.setName( product.getName() );
										orderItem.setUnitPrice(
											product.getPriceL1() != null ? product.getPriceL1() : new BigDecimal( 0 ) );
										orderItem
											.setUnitWeight( product.getWeight() != null ? product.getWeight() : 0 );
										orderItem.setProductId( product.getId() );
										orderItem.setProduct( product );

										isAvailable = true;
									}
									/*
									 * 如果找到 sku 对应的商品
									 */
									if ( isAvailable )
									{

										/* Accumulate total weight */
										weight += orderItem.getQtyOrdered() * orderItem.getUnitWeight();

										if ( orderItem.getQtyOrdered() != null )
										{
											/* Accumulate total items ordered */
											qtyTotalItemOrdered += orderItem.getQtyOrdered();
										}

										if ( orderItem.getQtyOrdered() != null && orderItem.getUnitPrice() != null )
										{
											/* Accumulate grand total */
											grandTotal = grandTotal.add( orderItem.getUnitPrice()
												.multiply( new BigDecimal( orderItem.getQtyOrdered() ) ) );
											subtotal = subtotal.add( orderItem.getUnitPrice()
												.multiply( new BigDecimal( orderItem.getQtyOrdered() ) ) );

											BigDecimal gst = orderItem.getUnitPrice()
												.multiply( new BigDecimal( orderItem.getQtyOrdered() ) )
												.multiply( new BigDecimal( 3 ) )
												.divide( new BigDecimal( 23 ), 2, BigDecimal.ROUND_DOWN );

											orderItem.setUnitGst( gst );
										}

										orderItems.add( orderItem );
									}
									else
									{
										/*
										 * 如果还是 真，则标为 假
										 */
										if ( isAllItemSkuFoundInEcommProduct )
										{
											isAllItemSkuFoundInEcommProduct = false;
										}
									}
									// goods_id 商品ID
									// market_price 市场价
									// suppliers_id 供货商ID

								}

								if ( order.getShippingFee() != null )
								{
									grandTotal = grandTotal.add( order.getShippingFee() );
								}
								if ( order.getSubtotal() != null )
								{
									/* *3/23 */
									tax = order.getSubtotal().multiply( new BigDecimal( 3 ) )
										.divide( new BigDecimal( 23 ), 2, BigDecimal.ROUND_DOWN );
								}
							}

							/*
							 * 金额
							 */
							order.setItems( orderItems );
							order.setWeight( weight );
							order.setQtyTotalItemOrdered( qtyTotalItemOrdered );
							order.setGrandTotal( grandTotal );
							order.setSubtotal( subtotal );
							order.setTax( tax );

							/*
							 * 其他信息
							 */
							if ( orderInfo.getAddTime() != null )
							{
								order.setExternalCreateTime( orderInfo.getAddTime() );
							}
							order.setExternalLogTime( orderInfo.getLogTime() );
							order.setInternalCreateTime( new Date() );
							order.setLastUpdateTime( new Date() );
							order.setExternalSn( String.valueOf( orderInfo.getOrderId() ) );
							order.setShippingDescription( orderInfo.getShippingName() );
							order.setShippingFee( orderInfo.getShippingFee() );

							// 中国快递: contains( 新西兰-中国 )
							// 新西兰快递: contains( 新西兰-新西兰 ) && contains( 快递邮寄 )
							// 自取: contains( 新西兰-新西兰 ) && contains( 自己取货 )
							// 送货上门: contains( 新西兰-新西兰 ) && contains( 送货上门 )
							if ( orderInfo.getShippingName() != null &&
								! orderInfo.getShippingName().trim().equals( "" ) )
							{
								if ( orderInfo.getShippingName().contains( "新西兰-中国" ) ||
									( orderInfo.getShippingName().contains( "新西兰-新西兰" ) &&
										orderInfo.getShippingName().contains( "快递邮寄" ) ) )
								{
									order.setDeliveryMethod( 1 );
								}
								else if ( orderInfo.getShippingName().contains( "新西兰-新西兰" ) &&
									orderInfo.getShippingName().contains( "自己取货" ) )
								{
									order.setDeliveryMethod( 2 );
								}
								else if ( orderInfo.getShippingName().contains( "新西兰-新西兰" ) &&
									orderInfo.getShippingName().contains( "送货上门" ) )
								{
									order.setDeliveryMethod( 3 );
								}
								else
								{
									isDeliveryMethodCorrect = false;
								}
							}

							order.setReceiveName( orderInfo.getConsignee() );
							order.setReceivePhone( orderInfo.getTel() + ", " + orderInfo.getMobile() );
							order.setReceiveEmail( orderInfo.getEmail() );
							order.setReceiveAddress( orderInfo.getAddress() );
							if ( orderInfo.getZipcode() != null )
							{
								order.setReceivePost( String.valueOf( orderInfo.getZipcode() ) );
							}

							order.setShop( mddShop );

							if ( orderInfo.getBiz() != null && ! orderInfo.getBiz().equals( "" ) )
							{
								Integer currency = orderInfo.getBiz().intValue();
								switch ( currency )
								{
									case 0 :
										order.setCurrency( nzdCurrency );
										break;
									case 1 :
										order.setCurrency( rmbCurrency );
										break;
								}
							}

							order.setMemo( orderInfo.getPostscript() );

							/*
							 * 未指定的字段：
							 */
							// private String receiveCountry;
							// private String receiveProvince;
							// private String receiveCity;
							// private String senderName;
							// private String senderAddress;
							// private String senderPhone;
							// private String senderEmail;
							// private String senderPost;

							/*
							 * 订单所有详情的 sku 都存在于 Ecomm 商品表中，并且发货方式正确
							 */
							// System.out.println(
							// "isAllItemSkuFoundInEcommProduct: " +
							// isAllItemSkuFoundInEcommProduct );
							// System.out.println( "isDeliveryMethodCorrect: " +
							// isDeliveryMethodCorrect );
							if ( isAllItemSkuFoundInEcommProduct && isDeliveryMethodCorrect )
							{
								finalOrders.add( order );
							}
						}
					}
					/**
					 * 结束：初始化订单及详情信息
					 */
				}
			}
		}
		/**
		 * 结束：调用 获取 API 订单数据
		 */

		/**
		 * 开始：调用 插入或更新订单及详情
		 */
		/*
		 * 更新或插入订单
		 */
		if ( finalOrders.size() > 0 )
		{
			this.orderRepository.save( finalOrders );
			// System.out.println( "可导入的订单：" + ( finalOrders != null ?
			// finalOrders.size() : 0 ) );
		}
		/**
		 * 结束：调用 插入或更新订单及详情
		 */
	}
}
