package com.sooeez.ecomm.api.mdd.respond;

import java.math.BigDecimal;

public class OrderGood
{
	private Long		goodsId;		// 品号
	private String		goodsName;		// 品名
	private String		goodsSn;		// Ecomm 商品 sku
	private Long		goodsNumber;	// 订购数量
	private BigDecimal	marketPrice;	// 市场价
	private BigDecimal	goodsPrice;		// 商品价格
	private Long		suppliersId;	// 供应商编号

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
}
