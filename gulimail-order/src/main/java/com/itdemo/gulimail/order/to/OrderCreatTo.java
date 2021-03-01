package com.itdemo.gulimail.order.to;

import com.itdemo.gulimail.order.entity.OrderEntity;
import com.itdemo.gulimail.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreatTo {
     OrderEntity order;
     List<OrderItemEntity> orderItems;
     BigDecimal payPrice;
     BigDecimal fare;
}
