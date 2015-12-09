package com.sooeez.ecomm.api.mdd.constant;

public class PreOrderStatus
{
	public static final Integer	UNCONFIRMED							= 1;	// 未确认
	public static final Integer	PENDING_DEPOSIT_TO_BE_PAID			= 2;	// 待支付定金
	public static final Integer	PENDING_BALANCE_DUE_TO_BE_PAID		= 3;	// 待付尾款
	public static final Integer	PENDING_ARRIVAL						= 4;	// 待到货
	public static final Integer	PENDING_INVENTORY					= 5;	// 待盘点
	public static final Integer	PENDING_DELIVERY_OR_SUB_LIBRARIES	= 6;	// 待提货或分库
	public static final Integer	PENDING_BOX_SINGLE_PAYMENT			= 7;	// 待付箱单款
	public static final Integer	COMPLETED							= 8;	// 已完成
}
