package com.sooeez.ecomm.api.mdd;

import java.math.BigDecimal;
import java.util.Date;

public class EcommUtils
{

	public static String getStringValue( Object obj )
	{
		String finalString = null;
		if ( obj instanceof Long )
		{
			finalString = String.valueOf( obj );
		}
		else if ( obj instanceof String )
		{
			finalString = ( String ) obj;
		}
		return finalString;
	}

	public static Long getLongValue( Object obj )
	{
		Long finalLong = null;
		if ( obj instanceof Long )
		{
			finalLong = ( Long ) obj;
		}
		else if ( obj instanceof String )
		{
			finalLong = Long.valueOf( ( String ) obj );
		}
		return finalLong;
	}

	public static BigDecimal getDecimalValue( Object obj )
	{
		BigDecimal finalDecimal = null;
		if ( obj instanceof Long )
		{
			finalDecimal = new BigDecimal( ( Long ) obj );
		}
		else if ( obj instanceof String )
		{
			finalDecimal = new BigDecimal( ( String ) obj );
		}
		return finalDecimal;
	}

	public static Date getDateValue( Object obj )
	{
		Date finalDate = null;
		if ( obj instanceof Long )
		{
			finalDate = new Date( ( ( Long ) obj ) * 1000 );
		}
		else if( obj instanceof String  )
		{
			finalDate = new Date( getLongValue( obj ) * 1000 );
		}
		return finalDate;
	}
}
