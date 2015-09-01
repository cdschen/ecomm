package com.sooeez.ecomm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sooeez.ecomm.ECommApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ECommApplication.class)
@WebAppConfiguration
public class ECommApplicationTests {

	@Test
	public void contextLoads() {
	}

}
