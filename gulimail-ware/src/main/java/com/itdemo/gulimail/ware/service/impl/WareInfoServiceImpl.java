package com.itdemo.gulimail.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.itdemo.common.utils.R;
import com.itdemo.gulimail.ware.fegin.MemberFeignService;
import com.itdemo.gulimail.ware.vo.FareVo;
import com.itdemo.gulimail.ware.vo.MemberAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;

import com.itdemo.gulimail.ware.dao.WareInfoDao;
import com.itdemo.gulimail.ware.entity.WareInfoEntity;
import com.itdemo.gulimail.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.eq("id",key)
                    .or().like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long attrId) {
        FareVo fareVo = new FareVo();
        //通过地址id远程查询出地址详细信息
        R r = memberFeignService.info(attrId);
        MemberAddressVo memberReceiveAddress = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
        });
        if(memberReceiveAddress!=null){
            String phone = memberReceiveAddress.getPhone();
            fareVo.setAddress(memberReceiveAddress);
            fareVo.setFare(new BigDecimal(phone.substring(phone.length()-1,phone.length())));
        }
        return fareVo;
    }

}