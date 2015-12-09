package com.sooeez.ecomm.scheduler;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.sooeez.ecomm.api.mdd.EcommHttp;
import com.sooeez.ecomm.api.mdd.constant.OrderStatus;
import com.sooeez.ecomm.api.mdd.constant.OrderStorage;
import com.sooeez.ecomm.api.mdd.constant.RespondCode;
import com.sooeez.ecomm.api.mdd.respond.OrderInfo;
import com.sooeez.ecomm.api.mdd.respond.OrderList;

@Component
@Configurable
@EnableScheduling
public class ImportOrderFromMDD
{
	private void importOrderFromMDD() throws Exception
	{
		/*
		 * 调用订单编号列表 API
		 */
		EcommHttp orderListHttp = new EcommHttp();
		orderListHttp.getParamsMap().put( "url", "http://www.mdd.co.nz/mddAPI/OrderList.php" );
		orderListHttp.getParamsMap().put( "storage", OrderStorage.NZ_STORAGE );
		orderListHttp.getParamsMap().put( "status", OrderStatus.WAREHOUSE_TRANSIT );

		OrderList orderList = new OrderList( orderListHttp.getJSONObject() );

		/*
		 * 如果调用成功，则获得所有订单编号
		 */
		if ( orderList.getSuccessCode().equals( RespondCode.SUCCESS_TRUE ) )
		{
			// System.out.println( "成功调用：OrderList API" );
			// System.out.println( "successCode: " + orderList.getSuccessCode()
			// );
			// System.out.println( "errorCode: " + orderList.getErrorCode() );
			// System.out.println( "dataStr: " + orderList.getDataStr() );

			/*
			 * 如果有订单编号，则获取订单信息
			 */
			if ( orderList.getOrderIds() != null && orderList.getOrderIds().size() > 0 )
			{
				for ( Long orderId : orderList.getOrderIds() )
				{
					EcommHttp orderInfoHttp = new EcommHttp();
					orderInfoHttp.getParamsMap().put( "url", "http://www.mdd.co.nz/mddAPI/OrderInfo.php" );
					orderInfoHttp.getParamsMap().put( "order_id", orderId );
					orderInfoHttp.getParamsMap().put( "pre", 0 );

					OrderInfo orderInfo = new OrderInfo( orderInfoHttp.getJSONObject() );
					// System.out.println( "成功调用：OrderInfo API" );
					// System.out.println( "successCode: " +
					// orderInfo.getSuccessCode() );
					// System.out.println( "errorCode: " +
					// orderInfo.getErrorCode() );
				}
			}
		}
	}

	// 每 10 秒钟执行一次
	// @Scheduled( cron = "*/10 * * * * * " )
	// private void importOrderFromMDDEveryTenSeconds() throws Exception
	// {
	// this.importOrderFromMDD();
	// }

	// 每 5 分钟执行一次
	// @Scheduled( cron = "0 */5 * * * * " )
	// private void importOrderFromMDDByCronEveryFiveMinutes() throws Exception
	// {
	// // this.importOrderFromMDD();
	// }

}
