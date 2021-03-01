package com.itdemo.gulimail.product.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itdemo.gulimail.product.entity.SkuSaleAttrValueEntity;
import com.itdemo.gulimail.product.vo.SkuInfoSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuInfoSaleAttrVo> getAttrBySpuid(@Param("spuId") Long spuId);

    List<String> getsalelistvalueByskuid(@Param("skuId") Long skuId);
}
