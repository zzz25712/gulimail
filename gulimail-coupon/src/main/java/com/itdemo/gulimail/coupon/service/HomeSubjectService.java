package com.itdemo.gulimail.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.coupon.entity.HomeSubjectEntity;

import java.util.Map;

/**
 * 
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 11:01:29
 */
public interface HomeSubjectService extends IService<HomeSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

