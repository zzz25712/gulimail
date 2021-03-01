package com.itdemo.gulimail.order.vo;

import com.itdemo.gulimail.order.entity.OrderEntity;
import lombok.Data;

@Data
public class OrderResponVo {
    private OrderEntity order;
    private Integer code;//0成功 1失败
}
