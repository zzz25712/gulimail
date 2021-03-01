package com.itdemo.gulimail.product.service.impl;


import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itdemo.common.constant.ProductConstant;
import com.itdemo.common.to.HasStockVo;
import com.itdemo.common.to.SkuReduceTo;
import com.itdemo.common.to.SpuBoundTo;
import com.itdemo.common.to.es.SkuEsModel;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;
import com.itdemo.common.utils.R;
import com.itdemo.gulimail.product.dao.SpuInfoDao;
import com.itdemo.gulimail.product.entity.*;
import com.itdemo.gulimail.product.fegin.EsProductUpFeginService;
import com.itdemo.gulimail.product.fegin.SpuBoundFeginService;
import com.itdemo.gulimail.product.fegin.WareSkuFeginService;
import com.itdemo.gulimail.product.service.*;
import com.itdemo.gulimail.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService imagesService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    SpuBoundFeginService spuBoundFeginService;


    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    AttrService attrService;

    @Autowired
    WareSkuFeginService wareSkuFeginService;

    @Autowired
    EsProductUpFeginService esProductUpFeginService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * //TODO 高级部分完善
     * @param vo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);

        //2、保存Spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setDecript(String.join(",",decript));
        descEntity.setSpuId(infoEntity.getId());
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //3、保存spu的图片集 pms_spu_images
        imagesService.saveImages(infoEntity.getId(),vo.getImages());

        //4、保存spu的规格参数;pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
            attrValueEntity.setSpuId(infoEntity.getId());
            attrValueEntity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            attrValueEntity.setAttrName(attrEntity.getAttrName());
            attrValueEntity.setQuickShow(attr.getShowDesc());
            attrValueEntity.setAttrValue(attr.getAttrValues());
            return attrValueEntity;
        }).collect(Collectors.toList());
        attrValueService.savaProductorAttr(collect);

        //5、保存spu的积分信息；gulimall_sms->sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo boundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,boundTo);
        boundTo.setSpuId(infoEntity.getId());
        R r1 = spuBoundFeginService.saveBound(boundTo);
        if(r1.getcode() != 0){
            log.error("远程保存spu失败");
        }

        //5、保存当前spu对应的所有sku信息；

            //5.1）、sku的基本信息；pms_sku_info
        List<Skus> skus = vo.getSkus();
        if(skus != null && skus.size() != 0){
            skus.forEach(sku -> {
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                String skuDefaultImg = "";
                List<Images> images = sku.getImages();
                for(Images img : images){
                    if(img.getDefaultImg() == 1){
                        skuDefaultImg = img.getImgUrl();
                    }
                }
                BeanUtils.copyProperties(sku,skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSkuDefaultImg(skuDefaultImg);
                skuInfoService.save(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();

                //5.2）、sku的图片信息；pms_sku_image
                List<SkuImagesEntity> collect1 = images.stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(img ->{
                    //排除路径为空的图片
                    return !StringUtils.isEmpty(img.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(collect1);

             //5.3）、sku的销售属性信息：pms_sku_sale_attr_value
                List<Attr> attr = sku.getAttr();
                List<SkuSaleAttrValueEntity> collect2 = attr.stream().map(item -> {
                    SkuSaleAttrValueEntity saleAttr = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(item, saleAttr);
                    saleAttr.setSkuId(skuId);
                    return saleAttr;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(collect2);

             //5.4）、sku的优惠、满减等信息； gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReduceTo skuReduceTo = new SkuReduceTo();
                BeanUtils.copyProperties(sku,skuReduceTo);
                skuReduceTo.setSkuId(skuId);
                if(skuReduceTo.getFullCount() > 0 || skuReduceTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    R r = spuBoundFeginService.saveSkuReduceTo(skuReduceTo);
                    if(r.getcode() != 0){
                        log.error("远程保存sku失败");
                    }
                }

            });
        }


    }

    /**
     *    QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

     String key = (String) params.get("key");
     if(!StringUtils.isEmpty(key)){
     wrapper.and((w)->{
     w.eq("id",key).or().like("spu_name",key);
     });
     }
     // status=1 and (id=1 or spu_name like xxx)
     String status = (String) params.get("status");
     if(!StringUtils.isEmpty(status)){
     wrapper.eq("publish_status",status);
     }

     String brandId = (String) params.get("brandId");
     if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
     wrapper.eq("brand_id",brandId);
     }

     String catelogId = (String) params.get("catelogId");
     if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
     wrapper.eq("catalog_id",catelogId);
     }

     /**
     * status: 2
     * key:
     * brandId: 9
     * catelogId: 225
     */

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }
        /**
         * status: 2
         * key:
         * brandId: 9
         * catelogId: 225
         * */
        IPage page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }


    @Override
    public void up(Long spuId) {
        //通过spuid找到所有sku信息
        List<SkuInfoEntity> skus = skuInfoService.ListBySpuId(spuId);

        List<Long> skuids = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        //设置可检索的attr属性
        List<ProductAttrValueEntity> attrs = attrValueService.ListforSpuByid(spuId);
        List<Long> attrids = attrs.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrids);
        Set<Long> idset = new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attr> attrList = attrs.stream().filter(item -> {
            return idset.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attr attr = new SkuEsModel.Attr();
            BeanUtils.copyProperties(item, attr);
            return attr;
        }).collect(Collectors.toList());

        //TODO 调用远程服务查有没有库存  hasStock
        Map<Long, Boolean> stockMap = null;
        try{
            R r = wareSkuFeginService.hasStock(skuids);
            TypeReference<List<HasStockVo>> typeReference = new TypeReference<List<HasStockVo>>(){};
            stockMap = r.getData(typeReference).stream().collect(Collectors.toMap(HasStockVo::getSkuId, HasStockVo::isHasStock));

        }catch (Exception e){
            log.error("仓库远程服务异常，原因为："+e);
        }


        //设置其他属性
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku,esModel);
            //skuprice,skuImg,hostscore
            esModel.setSkuPrice(sku.getPrice());
            esModel.setHotScore(0L);

            //设置hasStock
            if(finalStockMap == null){
                esModel.setHasStock(true);
            }else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }



            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandImg(brand.getLogo());
            esModel.setBrandName(brand.getName());
            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(category.getName());

            esModel.setSkuImg(sku.getSkuDefaultImg());

            esModel.setAttrs(attrList);

            return esModel;
        }).collect(Collectors.toList());
        //TODO 将数据发给es进行保存
        R r = esProductUpFeginService.saveProduct(upProducts);
        if(r.getcode() == 0){
            //保存成功
            //TODO 修改当前spu状态
            baseMapper.updateStatus(spuId, ProductConstant.productStatus.PRO_UP.getCode());
        }else {
            //保存失败
        }
    }

    @Override
    public SpuInfoEntity getSpuinfobySkuid(Long skuId) {
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        SpuInfoEntity spuInfoEntity = getById(skuInfoEntity.getSpuId());
        return spuInfoEntity;
    }


}