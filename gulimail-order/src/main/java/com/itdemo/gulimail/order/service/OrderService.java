package com.itdemo.gulimail.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.exception.NoStockException;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.order.entity.OrderEntity;
import com.itdemo.gulimail.order.vo.OrderConfirmVo;
import com.itdemo.gulimail.order.vo.OrderResponVo;
import com.itdemo.gulimail.order.vo.OrderSubmitVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 11:00:15
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo getOrderItemVo() throws ExecutionException, InterruptedException;

    OrderResponVo submitOrder(OrderSubmitVo vo) throws NoStockException;

    OrderEntity getOrderByOrdersn(String ordersn);
}

