package com.itdemo.gulimail.order;


import com.itdemo.gulimail.order.entity.OrderEntity;
import com.itdemo.gulimail.order.entity.OrderItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimailOrderApplicationTests {

	@Autowired
	AmqpAdmin amqpAdmin;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Test
	public void craeatExchange() {
		//DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
		DirectExchange exchange = new DirectExchange("hello-java-exchange", true, false);
		amqpAdmin.declareExchange(exchange);
		log.info("exchange创建完成");
	}

	@Test
	public void creatQuene(){
		//Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
		Queue queue = new Queue("hello.java", true, false, false);
		amqpAdmin.declareQueue(queue);
		log.info("queue创建完成");
	}

	@Test
	public void creatBinding(){
		//Binding(String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments)
		Binding binding = new Binding("hello.java", Binding.DestinationType.QUEUE, "hello-java-exchange", "hello.java", null);
		amqpAdmin.declareBinding(binding);
		log.info("binding创建完成");
	}

	@Test
	public void sendMessage(){
		OrderEntity entity = new OrderEntity();
		entity.setId(1L);
		rabbitTemplate.convertSendAndReceive("hello-java-exchange","hello.java",entity);
		log.info("消息发送成功");
	}

	@Test
	public void sendMessages(){
		for(int i=0; i<10; i++){
			if(i%2==0){
				OrderEntity entity = new OrderEntity();
				entity.setId(1L);
				rabbitTemplate.convertSendAndReceive("hello-java-exchange","hello.java",entity);
				log.info("消息发送成功:{}"+entity);
			}else{
				OrderItemEntity itemEntity = new OrderItemEntity();
				itemEntity.setId(1L);
				rabbitTemplate.convertSendAndReceive("hello-java-exchange","hello.java",itemEntity);
				log.info("消息发送成功:{}"+itemEntity);
			}
			}
	}
}
