package com.itdemo.gulimail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.product.entity.SkuInfoEntity;
import com.itdemo.gulimail.product.vo.SkuItemVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 10:47:48
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> ListBySpuId(Long spuId);

    SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException;

    SkuInfoEntity getPrice(Long skuId);
}

