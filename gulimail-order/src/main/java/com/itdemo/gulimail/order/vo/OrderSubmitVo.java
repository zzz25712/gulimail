package com.itdemo.gulimail.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交的数据
 */
@Data
public class OrderSubmitVo {
    private Long attrId;//收货地址的id
    private Integer payType;//支付方式
    private String orderToken;//放重令牌
    private BigDecimal payPrice;//应付金额
    private String node;//用户提交的备注
}
