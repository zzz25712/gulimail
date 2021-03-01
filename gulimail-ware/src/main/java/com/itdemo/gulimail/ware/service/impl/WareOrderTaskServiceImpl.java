package com.itdemo.gulimail.ware.service.impl;

import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;
import com.itdemo.gulimail.ware.dao.WareOrderTaskDao;
import com.itdemo.gulimail.ware.entity.WareOrderTaskEntity;
import com.itdemo.gulimail.ware.service.WareOrderTaskService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("wareOrderTaskService")
public class WareOrderTaskServiceImpl extends ServiceImpl<WareOrderTaskDao, WareOrderTaskEntity> implements WareOrderTaskService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskEntity> page = this.page(
                new Query<WareOrderTaskEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskEntity>()
        );

        return new PageUtils(page);
    }

}