package com.itdemo.gulimail.order;

import com.alibaba.cloud.seata.GlobalTransactionAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@EnableRabbit
@SpringBootApplication(exclude = GlobalTransactionAutoConfiguration.class)
public class GulimailOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimailOrderApplication.class, args);
	}

}
