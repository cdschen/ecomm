package com.sooeez.ecomm.api.mdd.respond;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sooeez.ecomm.api.mdd.EcommUtils;
import com.sooeez.ecomm.api.mdd.constant.RespondCode;

public class OrderList
{
	/*
	 * Respond 基本属性
	 */
	private Long							successCode;
	private Long							errorCode;
	private String							errorDesc;
	/*
	 * OrderInfo 专有属性
	 */
	private List< Map< String, Object > >	contentMap	= new ArrayList< Map< String, Object > >();
	private String							dataStr;

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

	public List< Map< String, Object > > getContentMap()
	{
		return contentMap;
	}

	public void setContentMap( List< Map< String, Object > > contentMap )
	{
		this.contentMap = contentMap;
	}

	public String getDataStr()
	{
		StringBuilder dataBuilder = new StringBuilder();
		for ( Map< String, Object > content : contentMap )
		{
			if ( ! dataBuilder.toString().trim().equals( "" ) )
			{
				dataBuilder.append( ", " );
			}
			dataBuilder.append( content.get( "order_id" ) );
		}
		this.setDataStr( dataBuilder.toString() );

		return dataStr;
	}

	public void setDataStr( String dataStr )
	{
		this.dataStr = dataStr;
	}

	public String getErrorDesc()
	{
		return errorDesc;
	}

	public void setErrorDesc( String errorDesc )
	{
		this.errorDesc = errorDesc;
	}

	public OrderList()
	{}

	public OrderList( JSONObject jsonObject )
	{
		Long respondSuccessCode = EcommUtils.getLongValue( jsonObject.get( "success" ) );
		Long respondErrorCode = EcommUtils.getLongValue( jsonObject.get( "errorcode" ) );
		String respondErrorDesc = EcommUtils.getStringValue( jsonObject.get( "errordesc" ) );
		JSONArray respondData = ( JSONArray ) jsonObject.get( "data" );

		this.setSuccessCode( respondSuccessCode != null ? respondSuccessCode : RespondCode.ERROR_FREE );
		this.setErrorCode( respondErrorCode != null ? respondErrorCode : RespondCode.ERROR_FREE );
		this.setErrorDesc( respondErrorDesc );

		if ( respondData != null && respondData.size() > 0 )
		{
			for ( Object obj : respondData )
			{
				JSONObject jsonObjectOrderList = ( JSONObject ) obj;
				
				Map< String, Object > map = new HashMap< String, Object >();
				map.put( "order_id", EcommUtils.getLongValue( jsonObjectOrderList.get( "order_id" ) ) );
				map.put( "log_time", EcommUtils.getLongValue( jsonObjectOrderList.get( "log_time" ) ) );
				
				this.getContentMap().add( map );
			}
		}
	}
}
