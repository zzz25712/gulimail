package com.itdemo.gulimail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.product.entity.AttrEntity;
import com.itdemo.gulimail.product.entity.ProductAttrValueEntity;
import com.itdemo.gulimail.product.vo.AttrRespVo;
import com.itdemo.gulimail.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 10:47:49
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrRespVo attr);

    List<AttrEntity> listAllAttr(long attrgroupId);

    PageUtils listSelectAttr(Map<String, Object> params, long attrgroupId);

    List<Long> selectSearchAttrIds(List<Long> attrids);
}

