package com.itdemo.gulimail.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.coupon.entity.SeckillSessionEntity;

import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:36:40
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

