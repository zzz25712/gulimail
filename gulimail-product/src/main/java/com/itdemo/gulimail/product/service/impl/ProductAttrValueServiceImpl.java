package com.itdemo.gulimail.product.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;

import com.itdemo.gulimail.product.dao.ProductAttrValueDao;
import com.itdemo.gulimail.product.entity.ProductAttrValueEntity;
import com.itdemo.gulimail.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void savaProductorAttr(List<ProductAttrValueEntity> collect) {
        this.saveBatch(collect);
    }

    @Override
    public List<ProductAttrValueEntity> ListforSpuByid(Long spuId) {
        List<ProductAttrValueEntity> entities = this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return entities;
    }

    @Transactional
    @Override
    public void updateAttrByspuId(Long spuId, List<ProductAttrValueEntity> entities) {
        //删除之前的属性
        this.remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        //更新新的属性
        List<ProductAttrValueEntity> collect = entities.stream().map(item -> {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            BeanUtils.copyProperties(item, entity);
            entity.setSpuId(spuId);
            return entity;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

}