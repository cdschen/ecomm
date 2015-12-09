package com.sooeez.ecomm.api.mdd.constant;

public class OrderStatus
{
	public static final Integer	WAREHOUSE_TRANSIT	= 1;	// 仓库中转
	public static final Integer	PENDING_PAYMENT		= 2;	// 待支付
	public static final Integer	PENDING_PRINTING	= 3;	// 待打印
	public static final Integer	OUT_OF_STOCK		= 4;	// 有缺货
	public static final Integer	DEPLOYING			= 5;	// 配货中
	public static final Integer	EXCEPTION			= 6;	// 问题单
}
