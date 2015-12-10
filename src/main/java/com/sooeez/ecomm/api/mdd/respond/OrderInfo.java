package com.sooeez.ecomm.api.mdd.respond;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sooeez.ecomm.api.mdd.EcommUtils;
import com.sooeez.ecomm.api.mdd.constant.RespondCode;

public class OrderInfo
{
	/*
	 * Respond 基本属性
	 */
	private Long				successCode;
	private Long				errorCode;
	private String				errorDesc;
	private List< String >		data	= new ArrayList< String >();
	/*
	 * OrderInfo 专有属性
	 */
	private Long				ecommOrderId;							// Ecomm订单id
	private Long				orderId;								// 订单id
	private String				orderSn;								// 订单编号
	private Long				userId;									// 用户id
	private Long				orderStatus;							// 订单状态
	private Long				shippingStatus;							// 配送状态
	private Long				payStatus;								// 支付状态
	private Long				afterStatus;							// 售后状态
	private String				consignee;								// 收货人
	private Long				country;								// 收货国家参见地区表
	private Long				province;								// 省份参见地区表
	private Long				city;									// 城市参见地区表
	private Long				district;								// 区参见地区表
	private String				address;								// 详细地址
	private String				zipcode;								// 邮编
	private String				tel;									// 电话
	private String				mobile;									// 手机
	private String				email;									// Email
	private String				postscript;								// 买家留言
	private String				howOos;									// 缺货处理
	private Long				shippingId;								// 配送方式ID
	private String				shippingName;							// 配送方式名称
	private Long				payId;									// 支付方式ID
	private String				payName;								// 支付方式名称
	private BigDecimal			goodsAmount;							// 商品总金额
	private BigDecimal			discount;								// 折扣
	private BigDecimal			shippingFee;							// 配送费用
	private BigDecimal			surplus;								// 使用余额
	private BigDecimal			integralMoney;							// 使用积分
	private BigDecimal			bonus;									// 使用现金券
	private BigDecimal			orderVouchers;							// 使用消费券
	private BigDecimal			shipVouchers;							// 使用运费券
	private BigDecimal			moneyPaid;								// 已支付
	private BigDecimal			orderAmount;							// 应付款
	private Long				biz;									// 币种
	private BigDecimal			huilv;									// 汇率
	private Date				addTime;								// 下单时间
	private Date				confirmTime;							// 确认订单时间
	private Date				payTime;								// 支付时间
	private Date				shippingTime;							// 发货时间
	private Long				isPre;									// 仓库ID
	private List< OrderGood >	goods	= new ArrayList< OrderGood >();	// 订购商品
	/*
	 * 额外属性
	 */
	private Long				logTime;

	public Long getEcommOrderId()
	{
		return ecommOrderId;
	}

	public void setEcommOrderId( Long ecommOrderId )
	{
		this.ecommOrderId = ecommOrderId;
	}

	public Long getSuccessCode()
	{
		return successCode;
	}

	public void setSuccessCode( Long successCode )
	{
		this.successCode = successCode;
	}

	public Long getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode( Long errorCode )
	{
		this.errorCode = errorCode;
	}

	public List< String > getData()
	{
		return data;
	}

	public void setData( List< String > data )
	{
		this.data = data;
	}

	public String getErrorDesc()
	{
		return errorDesc;
	}

	public void setErrorDesc( String errorDesc )
	{
		this.errorDesc = errorDesc;
	}

	public Long getOrderId()
	{
		return orderId;
	}

	public void setOrderId( Long orderId )
	{
		this.orderId = orderId;
	}

	public String getOrderSn()
	{
		return orderSn;
	}

	public void setOrderSn( String orderSn )
	{
		this.orderSn = orderSn;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId( Long userId )
	{
		this.userId = userId;
	}

	public Long getOrderStatus()
	{
		return orderStatus;
	}

	public void setOrderStatus( Long orderStatus )
	{
		this.orderStatus = orderStatus;
	}

	public Long getShippingStatus()
	{
		return shippingStatus;
	}

	public void setShippingStatus( Long shippingStatus )
	{
		this.shippingStatus = shippingStatus;
	}

	public Long getPayStatus()
	{
		return payStatus;
	}

	public void setPayStatus( Long payStatus )
	{
		this.payStatus = payStatus;
	}

	public Long getAfterStatus()
	{
		return afterStatus;
	}

	public void setAfterStatus( Long afterStatus )
	{
		this.afterStatus = afterStatus;
	}

	public String getConsignee()
	{
		return consignee;
	}

	public void setConsignee( String consignee )
	{
		this.consignee = consignee;
	}

	public Long getCountry()
	{
		return country;
	}

	public void setCountry( Long country )
	{
		this.country = country;
	}

	public Long getProvince()
	{
		return province;
	}

	public void setProvince( Long province )
	{
		this.province = province;
	}

	public Long getCity()
	{
		return city;
	}

	public void setCity( Long city )
	{
		this.city = city;
	}

	public Long getDistrict()
	{
		return district;
	}

	public void setDistrict( Long district )
	{
		this.district = district;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress( String address )
	{
		this.address = address;
	}

	public String getZipcode()
	{
		return zipcode;
	}

	public void setZipcode( String zipcode )
	{
		this.zipcode = zipcode;
	}

	public String getTel()
	{
		return tel;
	}

	public void setTel( String tel )
	{
		this.tel = tel;
	}

	public String getMobile()
	{
		return mobile;
	}

	public void setMobile( String mobile )
	{
		this.mobile = mobile;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail( String email )
	{
		this.email = email;
	}

	public String getPostscript()
	{
		return postscript;
	}

	public void setPostscript( String postscript )
	{
		this.postscript = postscript;
	}

	public String getHowOos()
	{
		return howOos;
	}

	public void setHowOos( String howOos )
	{
		this.howOos = howOos;
	}

	public Long getShippingId()
	{
		return shippingId;
	}

	public void setShippingId( Long shippingId )
	{
		this.shippingId = shippingId;
	}

	public String getShippingName()
	{
		return shippingName;
	}

	public void setShippingName( String shippingName )
	{
		this.shippingName = shippingName;
	}

	public Long getPayId()
	{
		return payId;
	}

	public void setPayId( Long payId )
	{
		this.payId = payId;
	}

	public String getPayName()
	{
		return payName;
	}

	public void setPayName( String payName )
	{
		this.payName = payName;
	}

	public BigDecimal getGoodsAmount()
	{
		return goodsAmount;
	}

	public void setGoodsAmount( BigDecimal goodsAmount )
	{
		this.goodsAmount = goodsAmount;
	}

	public BigDecimal getDiscount()
	{
		return discount;
	}

	public void setDiscount( BigDecimal discount )
	{
		this.discount = discount;
	}

	public BigDecimal getShippingFee()
	{
		return shippingFee;
	}

	public void setShippingFee( BigDecimal shippingFee )
	{
		this.shippingFee = shippingFee;
	}

	public BigDecimal getSurplus()
	{
		return surplus;
	}

	public void setSurplus( BigDecimal surplus )
	{
		this.surplus = surplus;
	}

	public BigDecimal getIntegralMoney()
	{
		return integralMoney;
	}

	public void setIntegralMoney( BigDecimal integralMoney )
	{
		this.integralMoney = integralMoney;
	}

	public BigDecimal getBonus()
	{
		return bonus;
	}

	public void setBonus( BigDecimal bonus )
	{
		this.bonus = bonus;
	}

	public BigDecimal getOrderVouchers()
	{
		return orderVouchers;
	}

	public void setOrderVouchers( BigDecimal orderVouchers )
	{
		this.orderVouchers = orderVouchers;
	}

	public BigDecimal getShipVouchers()
	{
		return shipVouchers;
	}

	public void setShipVouchers( BigDecimal shipVouchers )
	{
		this.shipVouchers = shipVouchers;
	}

	public BigDecimal getMoneyPaid()
	{
		return moneyPaid;
	}

	public void setMoneyPaid( BigDecimal moneyPaid )
	{
		this.moneyPaid = moneyPaid;
	}

	public BigDecimal getOrderAmount()
	{
		return orderAmount;
	}

	public void setOrderAmount( BigDecimal orderAmount )
	{
		this.orderAmount = orderAmount;
	}

	public Long getBiz()
	{
		return biz;
	}

	public void setBiz( Long biz )
	{
		this.biz = biz;
	}

	public BigDecimal getHuilv()
	{
		return huilv;
	}

	public void setHuilv( BigDecimal huilv )
	{
		this.huilv = huilv;
	}

	public Date getAddTime()
	{
		return addTime;
	}

	public void setAddTime( Date addTime )
	{
		this.addTime = addTime;
	}

	public Date getConfirmTime()
	{
		return confirmTime;
	}

	public void setConfirmTime( Date confirmTime )
	{
		this.confirmTime = confirmTime;
	}

	public Date getPayTime()
	{
		return payTime;
	}

	public void setPayTime( Date payTime )
	{
		this.payTime = payTime;
	}

	public Date getShippingTime()
	{
		return shippingTime;
	}

	public void setShippingTime( Date shippingTime )
	{
		this.shippingTime = shippingTime;
	}

	public Long getIsPre()
	{
		return isPre;
	}

	public void setIsPre( Long isPre )
	{
		this.isPre = isPre;
	}

	public List< OrderGood > getGoods()
	{
		return goods;
	}

	public void setGoods( List< OrderGood > goods )
	{
		this.goods = goods;
	}

	public Long getLogTime()
	{
		return logTime;
	}

	public void setLogTime( Long logTime )
	{
		this.logTime = logTime;
	}

	public OrderInfo()
	{}

	public OrderInfo( JSONObject jsonObject )
	{
		/*
		 * 基本属性赋值
		 */
		this.setBasicAttrs( jsonObject );
		// 打印
		// this.printBasicAttrs();

		/*
		 * 专有属性赋值
		 */
		this.setProprietaryAttrs( jsonObject );
		// 打印
		// this.printProprietaryAttrs();

		JSONArray jsonArray = ( JSONArray ) jsonObject.get( "goods" );

		if ( jsonArray != null && jsonArray.size() > 0 )
		{
			for ( Object obj : jsonArray )
			{
				JSONObject jsonObjectOrderGood = ( JSONObject ) obj;
				OrderGood orderGood = new OrderGood();
				orderGood.setGoodsId( EcommUtils.getLongValue( jsonObjectOrderGood.get( "goods_id" ) ) );
				orderGood.setGoodsName( EcommUtils.getStringValue( jsonObjectOrderGood.get( "goods_name" ) ) );
				orderGood.setGoodsSn( EcommUtils.getStringValue( jsonObjectOrderGood.get( "goods_sn" ) ) );
				orderGood.setGoodsNumber( EcommUtils.getLongValue( jsonObjectOrderGood.get( "goods_number" ) ) );
				orderGood.setMarketPrice( EcommUtils.getDecimalValue( jsonObjectOrderGood.get( "market_price" ) ) );
				orderGood.setGoodsPrice( EcommUtils.getDecimalValue( jsonObjectOrderGood.get( "goods_price" ) ) );
				orderGood.setSuppliersId( EcommUtils.getLongValue( jsonObjectOrderGood.get( "suppliers_id" ) ) );

				this.getGoods().add( orderGood );
			}
		}

		// 打印
		// this.printOrderGoodsAttrs();
	}

	public void setBasicAttrs( JSONObject jsonObject )
	{
		Long respondSuccessCode = EcommUtils.getLongValue( jsonObject.get( "success" ) );
		Long respondErrorCode = EcommUtils.getLongValue( jsonObject.get( "errorcode" ) );
		String respondErrorDesc = EcommUtils.getStringValue( jsonObject.get( "errordesc" ) );

		this.setSuccessCode( respondSuccessCode != null ? respondSuccessCode : RespondCode.ERROR_FREE );
		this.setErrorCode( respondErrorCode != null ? respondErrorCode : RespondCode.ERROR_FREE );
		this.setErrorDesc( respondErrorDesc );
	}

	public void setProprietaryAttrs( JSONObject jsonObject )
	{
		this.setOrderId( EcommUtils.getLongValue( jsonObject.get( "order_id" ) ) );
		this.setOrderSn( EcommUtils.getStringValue( jsonObject.get( "order_sn" ) ) );
		this.setUserId( EcommUtils.getLongValue( jsonObject.get( "user_id" ) ) );
		this.setOrderStatus( EcommUtils.getLongValue( jsonObject.get( "order_status" ) ) );
		this.setShippingStatus( EcommUtils.getLongValue( jsonObject.get( "shipping_status" ) ) );
		this.setPayStatus( EcommUtils.getLongValue( jsonObject.get( "pay_status" ) ) );
		this.setAfterStatus( EcommUtils.getLongValue( jsonObject.get( "after_status" ) ) );
		this.setConsignee( EcommUtils.getStringValue( jsonObject.get( "consignee" ) ) );
		this.setCountry( EcommUtils.getLongValue( jsonObject.get( "country" ) ) );
		this.setProvince( EcommUtils.getLongValue( jsonObject.get( "province" ) ) );
		this.setCity( EcommUtils.getLongValue( jsonObject.get( "city" ) ) );
		this.setDistrict( EcommUtils.getLongValue( jsonObject.get( "district" ) ) );
		this.setAddress( EcommUtils.getStringValue( jsonObject.get( "address" ) ) );
		this.setZipcode( EcommUtils.getStringValue( jsonObject.get( "zipcode" ) ) );
		this.setTel( EcommUtils.getStringValue( jsonObject.get( "tel" ) ) );
		this.setMobile( EcommUtils.getStringValue( jsonObject.get( "mobile" ) ) );
		this.setEmail( EcommUtils.getStringValue( jsonObject.get( "email" ) ) );
		this.setPostscript( EcommUtils.getStringValue( jsonObject.get( "postscript" ) ) );
		this.setHowOos( EcommUtils.getStringValue( jsonObject.get( "how_oos" ) ) );
		this.setShippingId( EcommUtils.getLongValue( jsonObject.get( "shipping_id" ) ) );
		this.setShippingName( EcommUtils.getStringValue( jsonObject.get( "shipping_name" ) ) );
		this.setPayId( EcommUtils.getLongValue( jsonObject.get( "pay_id" ) ) );
		this.setPayName( EcommUtils.getStringValue( jsonObject.get( "pay_name" ) ) );
		this.setGoodsAmount( EcommUtils.getDecimalValue( jsonObject.get( "goods_amount" ) ) );
		this.setDiscount( EcommUtils.getDecimalValue( jsonObject.get( "discount" ) ) );
		this.setShippingFee( EcommUtils.getDecimalValue( jsonObject.get( "shipping_fee" ) ) );
		this.setSurplus( EcommUtils.getDecimalValue( jsonObject.get( "surplus" ) ) );
		this.setIntegralMoney( EcommUtils.getDecimalValue( jsonObject.get( "integral_money" ) ) );
		this.setBonus( EcommUtils.getDecimalValue( jsonObject.get( "bonus" ) ) );
		this.setOrderVouchers( EcommUtils.getDecimalValue( jsonObject.get( "order_vouchers" ) ) );
		this.setShipVouchers( EcommUtils.getDecimalValue( jsonObject.get( "ship_vouchers" ) ) );
		this.setMoneyPaid( EcommUtils.getDecimalValue( jsonObject.get( "money_paid" ) ) );
		this.setOrderAmount( EcommUtils.getDecimalValue( jsonObject.get( "order_amount" ) ) );
		this.setBiz( EcommUtils.getLongValue( jsonObject.get( "biz" ) ) );
		this.setHuilv( EcommUtils.getDecimalValue( jsonObject.get( "huilv" ) ) );
		this.setAddTime( EcommUtils.getDateValue( jsonObject.get( "add_time" ) ) );
		this.setConfirmTime( EcommUtils.getDateValue( jsonObject.get( "confirm_time" ) ) );
		this.setPayTime( EcommUtils.getDateValue( jsonObject.get( "pay_time" ) ) );
		this.setShippingTime( EcommUtils.getDateValue( jsonObject.get( "shipping_time" ) ) );
		this.setIsPre( EcommUtils.getLongValue( jsonObject.get( "is_pre" ) ) );
	}

	public void printBasicAttrs()
	{
		System.out.println( "OrderInfo Basic Attributes BEGIN =====================" );

		System.out.println( "this.getSuccessCode(): " + this.getSuccessCode() );
		System.out.println( "this.getErrorCode(): " + this.getErrorCode() );
		System.out.println( "this.getErrorDesc(): " + this.getErrorDesc() );

		System.out.println( "OrderInfo Basic Attributes END =====================" );
	}

	public void printProprietaryAttrs()
	{
		System.out.println( "OrderInfo Proprietary Attributes BEGIN =====================" );

		System.out.println( "this.getOrderId(): " + this.getOrderId() );
		System.out.println( "this.getOrderSn(): " + this.getOrderSn() );
		System.out.println( "this.getUserId(): " + this.getUserId() );
		System.out.println( "this.getOrderStatus(): " + this.getOrderStatus() );
		System.out.println( "this.getShippingStatus(): " + this.getShippingStatus() );
		System.out.println( "this.getPayStatus(): " + this.getPayStatus() );
		System.out.println( "this.getAfterStatus(): " + this.getAfterStatus() );
		System.out.println( "this.getConsignee(): " + this.getConsignee() );
		System.out.println( "this.getCountry(): " + this.getCountry() );
		System.out.println( "this.getProvince(): " + this.getProvince() );
		System.out.println( "this.getCity(): " + this.getCity() );
		System.out.println( "this.getDistrict(): " + this.getDistrict() );
		System.out.println( "this.getAddress(): " + this.getAddress() );
		System.out.println( "this.getZipcode(): " + this.getZipcode() );
		System.out.println( "this.getTel(): " + this.getTel() );
		System.out.println( "this.getMobile(): " + this.getMobile() );
		System.out.println( "this.getEmail(): " + this.getEmail() );
		System.out.println( "this.getPostscript(): " + this.getPostscript() );
		System.out.println( "this.getHowOos(): " + this.getHowOos() );
		System.out.println( "this.getShippingId(): " + this.getShippingId() );
		System.out.println( "this.getShippingName(): " + this.getShippingName() );
		System.out.println( "this.getPayId(): " + this.getPayId() );
		System.out.println( "this.getPayName(): " + this.getPayName() );
		System.out.println( "this.getGoodsAmount(): " + this.getGoodsAmount() );
		System.out.println( "this.getDiscount(): " + this.getDiscount() );
		System.out.println( "this.getShippingFee(): " + this.getShippingFee() );
		System.out.println( "this.getSurplus(): " + this.getSurplus() );
		System.out.println( "this.getIntegralMoney(): " + this.getIntegralMoney() );
		System.out.println( "this.getBonus(): " + this.getBonus() );
		System.out.println( "this.getOrderVouchers(): " + this.getOrderVouchers() );
		System.out.println( "this.getShipVouchers(): " + this.getShipVouchers() );
		System.out.println( "this.getMoneyPaid(): " + this.getMoneyPaid() );
		System.out.println( "this.getOrderAmount(): " + this.getOrderAmount() );
		System.out.println( "this.getBiz(): " + this.getBiz() );
		System.out.println( "this.getHuilv(): " + this.getHuilv() );
		System.out.println( "this.getAddTime(): " + this.getAddTime() );
		System.out.println( "this.getConfirmTime(): " + this.getConfirmTime() );
		System.out.println( "this.getPayTime(): " + this.getPayTime() );
		System.out.println( "this.getShippingTime(): " + this.getShippingTime() );
		System.out.println( "this.getIsPre(): " + this.getIsPre() );

		System.out.println( "OrderInfo Proprietary Attributes END =====================" );
	}

	public void printOrderGoodsAttrs()
	{
		System.out.println( "OrderInfo OrderGoods Attributes BEGIN =====================" );

		Integer count = 0;
		List< OrderGood > orderGoods = this.getGoods();
		if ( orderGoods != null && orderGoods.size() > 0 )
		{
			for ( OrderGood orderGood : orderGoods )
			{
				System.out.println( "orderGood.getGoodsId(): " + orderGood.getGoodsId() );
				System.out.println( "orderGood.getGoodsName(): " + orderGood.getGoodsName() );
				System.out.println( "orderGood.getGoodsSn(): " + orderGood.getGoodsSn() );
				System.out.println( "orderGood.getGoodsNumber(): " + orderGood.getGoodsNumber() );
				System.out.println( "orderGood.getMarketPrice(): " + orderGood.getMarketPrice() );
				System.out.println( "orderGood.getGoodsPrice(): " + orderGood.getGoodsPrice() );
				System.out.println( "orderGood.getSuppliersId(): " + orderGood.getSuppliersId() );

				count++;
			}
		}

		System.out.println( "一共有 " + count + " 个订购商品" );

		System.out.println( "OrderInfo OrderGoods Attributes END =====================" );
	}
}
