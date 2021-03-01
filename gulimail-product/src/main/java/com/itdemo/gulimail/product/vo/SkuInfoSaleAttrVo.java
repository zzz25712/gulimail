package com.itdemo.gulimail.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuInfoSaleAttrVo{
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuidVo> attrValues;
}
