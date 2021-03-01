package com.itdemo.gulimail.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.coupon.entity.CouponEntity;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 11:01:29
 */
public interface CouponService extends IService<CouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

