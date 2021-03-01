package com.itdemo.gulimail.coupon.service.impl;

import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;
import com.itdemo.gulimail.coupon.dao.SeckillSkuNoticeDao;
import com.itdemo.gulimail.coupon.entity.SeckillSkuNoticeEntity;
import com.itdemo.gulimail.coupon.service.SeckillSkuNoticeService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("seckillSkuNoticeService")
public class SeckillSkuNoticeServiceImpl extends ServiceImpl<SeckillSkuNoticeDao, SeckillSkuNoticeEntity> implements SeckillSkuNoticeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSkuNoticeEntity> page = this.page(
                new Query<SeckillSkuNoticeEntity>().getPage(params),
                new QueryWrapper<SeckillSkuNoticeEntity>()
        );

        return new PageUtils(page);
    }

}