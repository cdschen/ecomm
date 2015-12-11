package com.sooeez.ecomm.api.mdd.constant;

public class RespondCode
{
	/*
	 * 成功代码
	 */
	public static final Long	SUCCESS_FALSE				= 0L;	// 失败
	public static final Long	SUCCESS_TRUE				= 1L;	// 成功
	/*
	 * 失败代码
	 */
	public static final Long	ERROR_FREE					= 0L;	// 接收成功
	public static final Long	ERROR_PARAMATER				= 1L;	// 传入参数有误
	public static final Long	ERROR_ACCOUNT				= 2L;	// 帐号错误
	public static final Long	ERROR_IP_VERIFICATION		= 3L;	// IP验证错误
	public static final Long	ERROR_MD5KEY_VERIFICATION	= 4L;	// MD5KEY验证错误
	public static final Long	ERROR_REPEATING				= 5L;	// 重复
	public static final Long	ERROR_DOES_NOT_EXIST		= 6L;	// 不存在
	public static final Long	ERROR_API_MAINTAINING		= 998L;	// 接口维护中
	public static final Long	ERROR_SYSTEM_CRASH			= 999L;	// 系统错误,未知
}
