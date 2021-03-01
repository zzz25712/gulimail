package com.itdemo.gulimail.ware.service.impl;

import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;
import com.itdemo.common.utils.R;
import com.itdemo.gulimail.ware.dao.WareOrderTaskDetailDao;
import com.itdemo.gulimail.ware.entity.WareOrderTaskDetailEntity;
import com.itdemo.gulimail.ware.service.WareOrderTaskDetailService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.StringUtils;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
           key: 1
           status: 0
           wareId: 1
         * */
        QueryWrapper<WareOrderTaskDetailEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and(w->{
                w.eq("purchase_id",key) .or().eq("sku_id",key);
            });
        }
        if(!StringUtils.isEmpty(status)){
            queryWrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }
        IPage<WareOrderTaskDetailEntity> page = this.page(
                new Query<WareOrderTaskDetailEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}