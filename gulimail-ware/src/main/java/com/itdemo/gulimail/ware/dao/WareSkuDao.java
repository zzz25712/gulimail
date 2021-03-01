package com.itdemo.gulimail.ware.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itdemo.gulimail.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void updateStock(@Param("skuId") Long skuId, @Param("WareId") Long WareId, @Param("skuNum") Integer skuNum);

    Long gethasStock(Long skuid);

    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    int lockWare(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);

    int Unlocked(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum, @Param("wareId") Long wareId);
}

