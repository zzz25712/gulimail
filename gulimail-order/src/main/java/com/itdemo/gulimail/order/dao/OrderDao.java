package com.itdemo.gulimail.order.dao;

import com.itdemo.gulimail.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 11:00:15
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
