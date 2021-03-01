package com.itdemo.gulimail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 10:47:49
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void savaProductorAttr(List<ProductAttrValueEntity> collect);

    List<ProductAttrValueEntity> ListforSpuByid(Long spuId);

    void updateAttrByspuId(Long spuId, List<ProductAttrValueEntity> entities);
}

