package com.itdemo.gulimail.product.service.impl;


import com.itdemo.gulimail.product.config.MyThreadConfig;
import com.itdemo.gulimail.product.entity.*;

import com.itdemo.gulimail.product.service.*;
import com.itdemo.gulimail.product.vo.SkuInfoSaleAttrVo;
import com.itdemo.gulimail.product.vo.SkuItemVo;
import com.itdemo.gulimail.product.vo.SpuItemAttrgroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;

import com.itdemo.gulimail.product.dao.SkuInfoDao;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("sku_id",key).or().like("sku_name",key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){

            queryWrapper.eq("catalog_id",catelogId);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(catelogId)){
            queryWrapper.eq("brand_id",brandId);
        }

        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            queryWrapper.ge("price",min);
        }

        String max = (String) params.get("max");

        if(!StringUtils.isEmpty(max)  ){
            try{
                BigDecimal bigDecimal = new BigDecimal(max);

                if(bigDecimal.compareTo(new BigDecimal("0"))==1){
                    queryWrapper.le("price",max);
                }
            }catch (Exception e){

            }

        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> ListBySpuId(Long spuId) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> future01 = CompletableFuture.supplyAsync(() -> {
            //查询skuInfo
            SkuInfoEntity infoEntity = getById(skuId);
            skuItemVo.setInfo(infoEntity);
            return infoEntity;
        }, executor);

        CompletableFuture<Void> future02 = future01.thenAcceptAsync((res) -> {
            //查sku销售属性
            List<SkuInfoSaleAttrVo> attrVo = skuSaleAttrValueService.getAttrBySpuid(res.getSpuId());
            skuItemVo.setSaleAttr(attrVo);
        }, executor);

        CompletableFuture<Void> future03 = future01.thenAcceptAsync((res) -> {
            //查spu介绍信息
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setSpudesc(spuInfoDescEntity);
        }, executor);

        CompletableFuture<Void> future04 = future01.thenAcceptAsync((res) -> {
            //查spu规格参数信息
            List<SpuItemAttrgroupVo> baseAttrs = attrGroupService.getAttrGroupWithAttrBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(baseAttrs);
        }, executor);

        CompletableFuture<List<SkuImagesEntity>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //查图片信息
            List<SkuImagesEntity> imags = skuImagesService.getBySkuId(skuId);
            skuItemVo.setImgs(imags);
            return imags;
        }, executor);


        //等待所有任务完成
        CompletableFuture.allOf(future01,future02,future03,future04,listCompletableFuture).get();

        return skuItemVo;
    }

    @Override
    public SkuInfoEntity getPrice(Long skuId) {
        SkuInfoEntity infoEntity = baseMapper.selectById(skuId);
        return infoEntity;
    }

}