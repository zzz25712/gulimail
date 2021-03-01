package com.itdemo.gulimail.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.order.entity.OrderSettingEntity;

import java.util.Map;

/**
 * 订单配置信息
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 11:00:15
 */
public interface OrderSettingService extends IService<OrderSettingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

