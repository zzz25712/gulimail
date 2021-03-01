package com.itdemo.gulimail.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.itdemo.common.to.es.SkuEsModel;
import com.itdemo.common.utils.R;
import com.itdemo.gulimail.search.config.ElasticConfig;
import com.itdemo.gulimail.search.constant.Esconstant;
import com.itdemo.gulimail.search.feign.ProductFeignService;
import com.itdemo.gulimail.search.service.SearchService;
import com.itdemo.gulimail.search.vo.AttrResponseVo;
import com.itdemo.gulimail.search.vo.ProSearchResult;
import com.itdemo.gulimail.search.vo.SearchParam;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    RestHighLevelClient esclient;

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public ProSearchResult searchObj(SearchParam searchParam) {

        //执行检索

        //准备检索请求
        SearchRequest searchRequest = bulidSearchRequest(searchParam);

        SearchResponse search = null;
        try {
            search = esclient.search(searchRequest, ElasticConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProSearchResult result = bulidSearchResult(search,searchParam);

        //封装检索结果

        return result;
    }

    //封装检索请求
    private SearchRequest bulidSearchRequest(SearchParam searchParam) {


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();//DSL语句的构建
            //bool封装
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            //bool --- must封装 模糊查询
            if (!StringUtils.isEmpty(searchParam.getKeyward())) {
                boolQuery.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyward()));
            }

            //bool --- filter封装
            //按catalogId过滤
            if (searchParam.getCatelog3Id() != null) {
                boolQuery.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatelog3Id()));
            }

            //按brandId过滤
            if (searchParam.getBrandId() != null && searchParam.getBrandId().size() > 0) {
                boolQuery.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
            }

            //按attr过滤
            if (searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0) {
                //attr=1_5寸:8寸
                for (String attr : searchParam.getAttrs()) {
                    BoolQueryBuilder nestedboolQuery = QueryBuilders.boolQuery();
                    String[] s = attr.split("_");
                    String attrId = s[0];
                    String[] attrValue = s[1].split(":");
                    nestedboolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                    nestedboolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
                    boolQuery.filter(QueryBuilders.nestedQuery("attrs", nestedboolQuery, ScoreMode.None));
                }
            }

            //按hasStock过滤
            if(searchParam.getHasStock() != null){
                boolQuery.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1));
            }


            //按价格区间过滤
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");

            if (!StringUtils.isEmpty(searchParam.getSkuPrice())) {
                String[] split = searchParam.getSkuPrice().split("_");
                if (split.length == 2) {
                    rangeQuery.gte(split[0]);
                    rangeQuery.lte(split[1]);
                } else if (searchParam.getSkuPrice().startsWith("_")) {
                    rangeQuery.lte(split[0]);
                } else if (searchParam.getSkuPrice().endsWith("_")) {
                    rangeQuery.gte(split[0]);
                }
                boolQuery.filter(rangeQuery);
            }

            searchSourceBuilder.query(boolQuery);

            //排序
            if (!StringUtils.isEmpty(searchParam.getSort())) {
                //sort=hotScore_asc/desc
                String[] s = searchParam.getSort().split("_");
                SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
                searchSourceBuilder.sort(s[0], order);
            }

            //分页
            searchSourceBuilder.from((searchParam.getPageNum() - 1) * Esconstant.PRODUCT_PAGESIZE);
            searchSourceBuilder.size(Esconstant.PRODUCT_PAGESIZE);

            //高亮
            if (!StringUtils.isEmpty(searchParam.getKeyward())) {
                HighlightBuilder builder = new HighlightBuilder();
                builder.field("skuTitle");
                builder.preTags("<b style=color:red>");
                builder.postTags("</b>");
                searchSourceBuilder.highlighter(builder);
            }

            //聚合分析
            //brand_agg聚合
            TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg").field("brandId").size(100);
            //brand_agg聚合的子聚合
            brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(10));
            brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(10));
            searchSourceBuilder.aggregation(brandAgg);

            //catalog_agg聚合
            TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(100);
            //catalog_agg聚合的子聚合
            catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(10));
            searchSourceBuilder.aggregation(catalogAgg);

            //attr_agg聚合 (nested)
            NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
            //attr_agg聚合的子聚合
            TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(10);
            attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
            attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
            attrAgg.subAggregation(attr_id_agg);
            searchSourceBuilder.aggregation(attrAgg);
            System.out.println("dsl----------"+searchSourceBuilder);

            SearchRequest searchRequest = new SearchRequest(new String[]{Esconstant.PRODUCT_INDEX}, searchSourceBuilder);
            return searchRequest;

    }

    //封装查询结果
    private ProSearchResult bulidSearchResult(SearchResponse search, SearchParam searchParam) {
        ProSearchResult result = new ProSearchResult();

        //封装商品信息
        SearchHits hits = search.getHits();
        List<SkuEsModel> products = new ArrayList<>();
        if(hits.getHits()!=null&&hits.getHits().length>0){
            for (SearchHit hit : hits.getHits()) {
                String source = hit.getSourceAsString();
                SkuEsModel product = JSON.parseObject(source,SkuEsModel.class);
                if(!StringUtils.isEmpty(searchParam.getKeyward())){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String title = skuTitle.getFragments()[0].string();
                    product.setSkuTitle(title);
                }
                products.add(product);
            }
        }

        //封装catelogs分类信息
        ParsedLongTerms catalog_agg = search.getAggregations().get("catalog_agg");
        List<ProSearchResult.CatelogVo> catelogVos = new ArrayList<>();
        for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
            ProSearchResult.CatelogVo catelogVo = new ProSearchResult.CatelogVo();
            String keyAsString = bucket.getKeyAsString();
            catelogVo.setCatelogId(Long.parseLong(keyAsString));

            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            List<? extends Terms.Bucket> buckets = catalog_name_agg.getBuckets();
            String catalog_name = buckets.get(0).getKeyAsString();
            catelogVo.setCatelogName(catalog_name);
            catelogVos.add(catelogVo);
        }

        //封装brands品牌信息
        ParsedLongTerms brand_agg = search.getAggregations().get("brand_agg");
        List<ProSearchResult.BrandVo> brandVos = new ArrayList<>();
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            ProSearchResult.BrandVo brandVo = new ProSearchResult.BrandVo();
            //查品牌id
            long brand_id = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brand_id);

            //查品牌名
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brand_name = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brand_name);

            //查品牌图片
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brand_img = brand_img_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brand_img);

            brandVos.add(brandVo);
        }

        //封装attrs属性信息
        List<ProSearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = search.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            ProSearchResult.AttrVo attrVo = new ProSearchResult.AttrVo();
            //设置属性id
            long attr_id = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attr_id);

            //设置属性名
            Aggregations aggregations = bucket.getAggregations();
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attr_name = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attr_name);

            //设置属性值
            List<String> attr_values = new ArrayList<>();
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            for (Terms.Bucket bucket1 : attr_value_agg.getBuckets()) {
                String attr_value = bucket1.getKeyAsString();
                attr_values.add(attr_value);
            }
            attrVo.setAttrValue(attr_values);

            attrVos.add(attrVo);
        }


        //获取总记录数
        long value = search.getHits().getTotalHits().value;

        //获取总页码数
        long totalPage = value % Esconstant.PRODUCT_PAGESIZE == 0 ? value / Esconstant.PRODUCT_PAGESIZE : (value / Esconstant.PRODUCT_PAGESIZE) + 1;

        //封装pageNavs
        List<Integer> pageNavs = new ArrayList<>();
        for(int i=1; i<=totalPage; i++){
            pageNavs.add(i);
        }

        //封装navs
        if(searchParam.getAttrs()!=null&&searchParam.getAttrs().size()>0){
            List<ProSearchResult.NavVo> navVos = searchParam.getAttrs().stream().map(attr->{
                ProSearchResult.NavVo navVo = new ProSearchResult.NavVo();
                //1_2寸：5寸
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.Attrinfo(Long.parseLong(s[0]));
                AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
                });
                if(r.getcode() == 0){
                    //查询成功设置属性名
                    navVo.setNavName(data.getAttrName());
                }else{
                    //失败设置为属性id
                    navVo.setNavName(s[0]);
                }
                String encode = null;
                try {
                    encode = URLEncoder.encode(attr, "UTF-8");
                    encode = encode.replace("+","%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String replace = searchParam.get_querystring().replace("&attrs=" + encode, "");
                navVo.setLink("http://search.gulimail.com/list.html?"+replace);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }



        //设置值
        result.setProducts(products);
        result.setCatelogs(catelogVos);
        result.setAttrs(attrVos);
        result.setBrands(brandVos);
        result.setPageNum(searchParam.getPageNum());
        result.setTotal(value);
        result.setTotalPage((int)totalPage);
        result.setPageNavs(pageNavs);

        
        
        return result;
    }
}
