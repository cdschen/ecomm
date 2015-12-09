package com.sooeez.ecomm.api.mdd;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class EcommHttp
{
	String					account		= "Ecomm";
	String					key			= "f12535b111713d1fced74e08994c1882";
	Map< String, Object >	paramsMap	= new HashMap< String, Object >();

	public JSONObject getJSONObject() throws Exception
	{
		StringBuilder finalUrlBuilder = new StringBuilder();
		StringBuilder urlBuilder = new StringBuilder();
		StringBuilder identityBuilder = new StringBuilder();
		StringBuilder paramsBuilder = new StringBuilder();

		for ( Entry< String, Object > entry : paramsMap.entrySet() )
		{
			/*
			 * 剔除 url
			 */
			switch ( entry.getKey() )
			{
				case "url" :
					break;
				case "account" :
					this.account = ( String ) entry.getValue();
					break;
				case "key" :
					this.key = ( String ) entry.getValue();
					break;
				default :
					paramsBuilder.append( "&" + entry.getKey() + "=" + entry.getValue() );
			}
		}
		urlBuilder.append( paramsMap.get( "url" ) + "?" );
		identityBuilder.append( "account=" + this.account + "&key=" + this.key );

		finalUrlBuilder.append( urlBuilder.toString() + identityBuilder.toString() + paramsBuilder.toString() );

		System.out.println( "finalUrlBuilder: " );
		System.out.println( finalUrlBuilder.toString() );

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet( finalUrlBuilder.toString() );
		CloseableHttpResponse response = httpclient.execute( httpget );

		String json = EntityUtils.toString( response.getEntity(), "UTF-8" );

		// System.out.println( "json: " );
		// System.out.println( json );

		JSONParser parser = new JSONParser();
		Object resultObject = parser.parse( json );

		return ( JSONObject ) resultObject;
	}

	public String getAccount()
	{
		return account;
	}

	public void setAccount( String account )
	{
		this.account = account;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey( String key )
	{
		this.key = key;
	}

	public Map< String, Object > getParamsMap()
	{
		return paramsMap;
	}

	public void setParamsMap( Map< String, Object > paramsMap )
	{
		this.paramsMap = paramsMap;
	}

}
