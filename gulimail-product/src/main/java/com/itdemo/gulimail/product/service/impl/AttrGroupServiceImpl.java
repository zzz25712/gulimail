package com.itdemo.gulimail.product.service.impl;

import com.itdemo.gulimail.product.dao.AttrAttrgroupRelationDao;
import com.itdemo.gulimail.product.entity.AttrAttrgroupRelationEntity;
import com.itdemo.gulimail.product.entity.AttrEntity;
import com.itdemo.gulimail.product.service.AttrAttrgroupRelationService;
import com.itdemo.gulimail.product.service.AttrService;
import com.itdemo.gulimail.product.vo.AttrGroupRelationVo;
import com.itdemo.gulimail.product.vo.AttrGroupWithAttrsVo;
import com.itdemo.gulimail.product.vo.SkuItemVo;
import com.itdemo.gulimail.product.vo.SpuItemAttrgroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;

import com.itdemo.gulimail.product.dao.AttrGroupDao;
import com.itdemo.gulimail.product.entity.AttrGroupEntity;
import com.itdemo.gulimail.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrAttrgroupRelationService relationService;

    @Autowired
    AttrService attrService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, long catelogId) {
        if(catelogId == 0){
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>()
            );
            return new PageUtils(page);
        }else{
            String key = (String)params.get("key");
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId);
            if(!StringUtils.isEmpty(key)){
                wrapper.and((obj)->{
                    obj.eq("attr_group_id",key).or().like("attr_group_name",key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper  );
            return new PageUtils(page);
        }
    }

    @Override
    public void attrRelationDelate(List<AttrGroupRelationVo> vos) {
        List<AttrAttrgroupRelationEntity> entities = vos.stream().map((vo) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(vo, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(entities);
    }

    @Override
    public void attrRelationAdd(List<AttrGroupRelationVo> vos) {
        List<AttrAttrgroupRelationEntity> entities = vos.stream().map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationService.saveBatch(entities);
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrByCId(Long catelogId) {
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(item -> {
            AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item, vo);
            List<AttrEntity> attrEntities = attrService.listAllAttr(item.getAttrGroupId());
            vo.setAttrs(attrEntities);
            return vo;
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<SpuItemAttrgroupVo> getAttrGroupWithAttrBySpuId(Long spuId, Long catalogId) {
        AttrGroupDao baseMapper = getBaseMapper();
        List<SpuItemAttrgroupVo> list = baseMapper.getAttrGroupWithAttrBySpuId(spuId,catalogId);
        return list;
    }

}