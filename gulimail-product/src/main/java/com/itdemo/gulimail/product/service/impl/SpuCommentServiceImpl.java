package com.itdemo.gulimail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;
import com.itdemo.gulimail.product.dao.SpuCommentDao;
import com.itdemo.gulimail.product.entity.SpuCommentEntity;
import com.itdemo.gulimail.product.service.SpuCommentService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;


@Service("spuCommentService")
public class SpuCommentServiceImpl extends ServiceImpl<SpuCommentDao, SpuCommentEntity> implements SpuCommentService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuCommentEntity> page = this.page(
                new Query<SpuCommentEntity>().getPage(params),
                new QueryWrapper<SpuCommentEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean saveBatch(Collection<SpuCommentEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<SpuCommentEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<SpuCommentEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(SpuCommentEntity entity) {
        return false;
    }

    @Override
    public SpuCommentEntity getOne(Wrapper<SpuCommentEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<SpuCommentEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<SpuCommentEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

}