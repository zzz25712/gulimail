package com.itdemo.gulimail.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.product.entity.AttrGroupEntity;
import com.itdemo.gulimail.product.vo.AttrGroupRelationVo;
import com.itdemo.gulimail.product.vo.AttrGroupWithAttrsVo;
import com.itdemo.gulimail.product.vo.SpuItemAttrgroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 10:47:49
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, long catelogId);

    void attrRelationDelate(List<AttrGroupRelationVo> vos);

    void attrRelationAdd(List<AttrGroupRelationVo> vos);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrByCId(Long catelogId);

    List<SpuItemAttrgroupVo> getAttrGroupWithAttrBySpuId(Long spuId, Long catalogId);

}

