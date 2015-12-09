package com.sooeez.ecomm.api.mdd.respond;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.sooeez.ecomm.api.mdd.constant.RespondCode;

public class OrderList
{
	/*
	 * Respond 基本属性
	 */
	private Long			successCode;
	private Long			errorCode;
	private String			errorDesc;
	/*
	 * OrderInfo 专有属性
	 */
	private List< Long >	orderIds	= new ArrayList< Long >();
	private String			dataStr;

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

	public List< Long > getOrderIds()
	{
		return orderIds;
	}

	public void setOrderIds( List< Long > orderIds )
	{
		this.orderIds = orderIds;
	}

	public String getDataStr()
	{
		StringBuilder dataBuilder = new StringBuilder();
		for ( Long orderId : orderIds )
		{
			if ( ! dataBuilder.toString().trim().equals( "" ) )
			{
				dataBuilder.append( ", " );
			}
			dataBuilder.append( orderId );
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

	@SuppressWarnings( "unchecked" )
	public OrderList( JSONObject jsonObject )
	{
		Long respondSuccessCode = ( Long ) jsonObject.get( "success" );
		Long respondErrorCode = ( Long ) jsonObject.get( "errorcode" );
		String respondErrorDesc = ( String ) jsonObject.get( "errordesc" );
		List< String > respondData = ( List< String > ) jsonObject.get( "data" );

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

		if ( respondData != null && respondData.size() > 0 )
		{
			for ( Object object : respondData )
			{
				orderIds.add( Long.valueOf( object.toString() ) );
			}
		}
	}
}
