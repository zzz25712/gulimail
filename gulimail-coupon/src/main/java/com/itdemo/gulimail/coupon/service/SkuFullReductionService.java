package com.itdemo.gulimail.coupon.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.to.SkuReduceTo;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:36:40
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void saveSkuReduceTo(SkuReduceTo skuReduceTo);
}

