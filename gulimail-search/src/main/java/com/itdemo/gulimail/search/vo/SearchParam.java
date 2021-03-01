package com.itdemo.gulimail.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {
    private String keyward;//全文匹配关键词
    private Long catelog3Id;
    private String sort; //排序
    private Integer hasStock; //1[有库存] 0[无库存]
    private String skuPrice;//价格区间
    private List<Long> brandId;//品牌 可以多选
    private List<String> attrs;
    private Integer pageNum = 1;//页码 默认是第一页
    private String _querystring;//当前url的请求路径参数
}
