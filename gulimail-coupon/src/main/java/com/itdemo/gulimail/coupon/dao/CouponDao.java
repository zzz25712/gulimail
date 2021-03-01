package com.itdemo.gulimail.coupon.dao;

import com.itdemo.gulimail.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 11:01:29
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
