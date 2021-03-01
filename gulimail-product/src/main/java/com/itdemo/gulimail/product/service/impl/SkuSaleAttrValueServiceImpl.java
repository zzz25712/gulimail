package com.itdemo.gulimail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;
import com.itdemo.gulimail.product.dao.SkuSaleAttrValueDao;
import com.itdemo.gulimail.product.entity.SkuSaleAttrValueEntity;
import com.itdemo.gulimail.product.service.SkuSaleAttrValueService;
import com.itdemo.gulimail.product.vo.SkuInfoSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;



@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoSaleAttrVo> getAttrBySpuid(Long spuId) {
        List<SkuInfoSaleAttrVo> attrVo = baseMapper.getAttrBySpuid(spuId);
        return attrVo;
    }

    @Override
    public List<String> getsalelistvalueByskuid(Long skuId) {
        SkuSaleAttrValueDao dao = this.baseMapper;
        return dao.getsalelistvalueByskuid(skuId);
    }

}