package com.sooeez.ecomm.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sooeez.ecomm.api.mdd.request.ImportOrderFromMDD;

@Component
@Configurable
@EnableScheduling
public class ImportOrderFromMDDScheduler
{
	@Autowired
	private ImportOrderFromMDD importOrderFromMDD;

	// 每 10 秒钟执行一次
	// @Scheduled( cron = "*/10 * * * * * " )
	// private void importOrderFromMDDEveryTenSeconds() throws Exception
	// {
	// this.importOrderFromMDD.importOrderFromMDD();
	// }

	// 每 3 分钟执行一次
	@Scheduled( cron = "0 */3 * * * * " )
	private void importOrderFromMDDByCronEveryFiveMinutes() throws Exception
	{
		this.importOrderFromMDD.importOrderFromMDD();
	}

}
