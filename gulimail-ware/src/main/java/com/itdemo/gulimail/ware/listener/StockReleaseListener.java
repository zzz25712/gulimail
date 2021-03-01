package com.itdemo.gulimail.ware.listener;

import com.itdemo.common.to.stock.StockTo;
import com.itdemo.gulimail.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;


    @RabbitHandler
    public void handelStockLockedRelease(StockTo to, Message message, Channel channel) throws IOException {
        System.out.println("收到解锁库存的消息"+to);
       try {
           wareSkuService.releaseStock(to);
           //手动确认 消息接收
           channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
       }catch (Exception e){
           //消息拒绝以后重新放回消息队列
           channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
       }

    }

}
