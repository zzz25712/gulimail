package com.itdemo.gulimail.product.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.product.entity.SkuSaleAttrValueEntity;
import com.itdemo.gulimail.product.vo.SkuInfoSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuInfoSaleAttrVo> getAttrBySpuid(Long spuId);

    List<String> getsalelistvalueByskuid(Long skuId);
}

