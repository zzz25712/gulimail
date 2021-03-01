package com.itdemo.gulimail.product.vo;

import com.itdemo.gulimail.product.entity.SkuImagesEntity;
import com.itdemo.gulimail.product.entity.SkuInfoEntity;
import java.util.List;

import com.itdemo.gulimail.product.entity.SpuInfoDescEntity;
import lombok.Data;

@Data
public class SkuItemVo {
    //1.sku基本信息
    private SkuInfoEntity info;
    //2.sku图片信息
    private List<SkuImagesEntity> imgs;
    //3.sku销售属性组合
    private List<SkuInfoSaleAttrVo> saleAttr;
    //4.spu介绍
    private SpuInfoDescEntity spudesc;
    //5.spu规格参数信息
    private List<SpuItemAttrgroupVo> groupAttrs;


}
