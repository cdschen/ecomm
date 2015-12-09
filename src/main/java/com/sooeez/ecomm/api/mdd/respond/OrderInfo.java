package com.sooeez.ecomm.api.mdd.respond;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.sooeez.ecomm.api.mdd.constant.RespondCode;

public class OrderInfo
{
	/*
	 * Respond 基本属性
	 */
	private Long			successCode;
	private Long			errorCode;
	private String			errorDesc;
	private List< String >	data	= new ArrayList< String >();
	/*
	 * OrderInfo 专有属性
	 */
	private Long			orderId;							// 订单id
	private String			orderSn;							// 订单编号
	private Long			userId;								// 用户id
	private Long			orderStatus;						// 订单状态
	private Long			shippingStatus;						// 配送状态
	private Long			payStatus;							// 支付状态
	private Long			afterStatus;						// 售后状态
	private String			consignee;							// 收货人
	private Long			country;							// 收货国家 参见地区表
	private Long			province;							// 省份 参见地区表
	private Long			city;								// 城市 参见地区表
	private Long			district;							// 区 参见地区表
	private String			address;							// 详细地址
	private String			zipcode;							// 邮编
	private String			tel;								// 电话
	private String			mobile;								// 手机
	private String			email;								// Email
	private String			postscript;							// 买家留言
	private String			howOos;								// 缺货处理
	private Long			shippingId;							// 配送方式ID
	private String			shippingName;						// 配送方式名称
	private Long			payId;								// 支付方式ID
	private String			payName;							// 支付方式名称
	private BigDecimal		goodsAmount;						// 商品总金额
	private BigDecimal		discount;							// 折扣
	private BigDecimal		shippingFee;						// 配送费用
	private BigDecimal		surplus;							// 使用余额
	private BigDecimal		integralMoney;						// 使用积分
	private BigDecimal		bonus;								// 使用现金券
	private BigDecimal		orderVouchers;						// 使用消费券
	private BigDecimal		shipVouchers;						// 使用运费券
	private BigDecimal		moneyPaid;							// 已支付
	private BigDecimal		orderAmount;						// 应付款
	private Long			biz;								// 币种
	private BigDecimal		huilv;								// 汇率
	private Long			addTime;							// 下单时间
	private Long			confirmTime;						// 确认订单时间
	private Long			payTime;							// 支付时间
	private Long			shippingTime;						// 发货时间
	private Long			isPre;								// 仓库ID
	// private List goods; // 订单商品信息
	private Long			goodsId;							// 商品ID
	private String			goodsName;							// 商品名称
	private String			goodsSn;							// 货号
	private Long			goodsNumber;						// 数量
	private BigDecimal		marketPrice;						// 市场价
	private BigDecimal		goodsPrice;							// 售价
	private Long			suppliersId;						// 供货商ID

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

	public Long getAddTime()
	{
		return addTime;
	}

	public void setAddTime( Long addTime )
	{
		this.addTime = addTime;
	}

	public Long getConfirmTime()
	{
		return confirmTime;
	}

	public void setConfirmTime( Long confirmTime )
	{
		this.confirmTime = confirmTime;
	}

	public Long getPayTime()
	{
		return payTime;
	}

	public void setPayTime( Long payTime )
	{
		this.payTime = payTime;
	}

	public Long getShippingTime()
	{
		return shippingTime;
	}

	public void setShippingTime( Long shippingTime )
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

	public Long getGoodsId()
	{
		return goodsId;
	}

	public void setGoodsId( Long goodsId )
	{
		this.goodsId = goodsId;
	}

	public String getGoodsName()
	{
		return goodsName;
	}

	public void setGoodsName( String goodsName )
	{
		this.goodsName = goodsName;
	}

	public String getGoodsSn()
	{
		return goodsSn;
	}

	public void setGoodsSn( String goodsSn )
	{
		this.goodsSn = goodsSn;
	}

	public Long getGoodsNumber()
	{
		return goodsNumber;
	}

	public void setGoodsNumber( Long goodsNumber )
	{
		this.goodsNumber = goodsNumber;
	}

	public BigDecimal getMarketPrice()
	{
		return marketPrice;
	}

	public void setMarketPrice( BigDecimal marketPrice )
	{
		this.marketPrice = marketPrice;
	}

	public BigDecimal getGoodsPrice()
	{
		return goodsPrice;
	}

	public void setGoodsPrice( BigDecimal goodsPrice )
	{
		this.goodsPrice = goodsPrice;
	}

	public Long getSuppliersId()
	{
		return suppliersId;
	}

	public void setSuppliersId( Long suppliersId )
	{
		this.suppliersId = suppliersId;
	}

	public OrderInfo()
	{}

	public OrderInfo( JSONObject jsonObject )
	{
//		System.out.println( "jsonObject: " );
//		System.out.println( jsonObject );
		/*
		 * 基本属性赋值
		 */
		Long respondSuccessCode = ( Long ) jsonObject.get( "success" );
		Long respondErrorCode = ( Long ) jsonObject.get( "errorcode" );
		String respondErrorDesc = ( String ) jsonObject.get( "errordesc" );
		/*
		 * 专有属性赋值
		 */
		Long respondOrderId = ( Long ) jsonObject.get( "order_id" );
//		String 
		// private Long orderId; // 订单id
		// private String orderSn; // 订单编号
		// private Long userId; // 用户id
		// private Long orderStatus; // 订单状态
		// private Long shippingStatus; // 配送状态
		// private Long payStatus; // 支付状态
		// private Long afterStatus; // 售后状态
		// private String consignee; // 收货人
		// private Long country; // 收货国家 参见地区表
		// private Long province; // 省份 参见地区表
		// private Long city; // 城市 参见地区表
		// private Long district; // 区 参见地区表
		// private String address; // 详细地址
		// private String zipcode; // 邮编
		// private String tel; // 电话
		// private String mobile; // 手机
		// private String email; // Email
		// private String postscript; // 买家留言
		// private String howOos; // 缺货处理
		// private Long shippingId; // 配送方式ID
		// private String shippingName; // 配送方式名称
		// private Long payId; // 支付方式ID
		// private String payName; // 支付方式名称
		// private BigDecimal goodsAmount; // 商品总金额
		// private BigDecimal discount; // 折扣
		// private BigDecimal shippingFee; // 配送费用
		// private BigDecimal surplus; // 使用余额
		// private BigDecimal integralMoney; // 使用积分
		// private BigDecimal bonus; // 使用现金券
		// private BigDecimal orderVouchers; // 使用消费券
		// private BigDecimal shipVouchers; // 使用运费券
		// private BigDecimal moneyPaid; // 已支付
		// private BigDecimal orderAmount; // 应付款
		// private Long biz; // 币种
		// private BigDecimal huilv; // 汇率
		// private Long addTime; // 下单时间
		// private Long confirmTime; // 确认订单时间
		// private Long payTime; // 支付时间
		// private Long shippingTime; // 发货时间
		// private Long isPre; // 仓库ID
		// // private List goods; // 订单商品信息
		// private Long goodsId; // 商品ID
		// private String goodsName; // 商品名称
		// private String goodsSn; // 货号
		// private Long goodsNumber; // 数量
		// private BigDecimal marketPrice; // 市场价
		// private BigDecimal goodsPrice; // 售价
		// private Long suppliersId; // 供货商ID

		if ( respondSuccessCode != null )
		{
			this.setSuccessCode( Long.valueOf( respondSuccessCode ) );
		}
		else
		{
			this.setSuccessCode( RespondCode.ERROR_FREE );
		}

		if ( respondErrorCode != null )
		{
			this.setErrorCode( Long.valueOf( respondErrorCode ) );
		}
		else
		{
			this.setErrorCode( RespondCode.ERROR_FREE );
		}

		if ( respondErrorDesc != null )
		{
			this.setErrorDesc( respondErrorDesc );
		}
	}
}
