package com.itdemo.gulimail.search.vo;

import com.itdemo.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

@Data
public class ProSearchResult {
    private List<SkuEsModel> products;//总商品
    /**
     * 分页信息
     * */
    private Integer pageNum;//当前页码
    private Integer totalPage;//总页数
    private List<Integer> pageNavs;//总页数转成集合 分页时使用
    private Long total;//总记录数

    private List<BrandVo> brands;//返回所涉及到的品牌信息
    private List<AttrVo> attrs;//返回所涉及到的属性信息
    private List<CatelogVo> catelogs;//返回所涉及到的分类信息

    private List<NavVo> navs;

    @Data
    public static class NavVo{
        private String navValue;
        private String navName;
        private String link;
    }

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private List<String> attrValue;
        private String attrName;
    }

    @Data
    public static class CatelogVo{
        private Long catelogId;
        private String catelogName;
    }

}
