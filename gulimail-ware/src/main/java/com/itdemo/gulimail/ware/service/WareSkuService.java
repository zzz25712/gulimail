package com.itdemo.gulimail.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.to.stock.StockTo;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.ware.entity.WareSkuEntity;
import com.itdemo.gulimail.ware.exception.NoStockException;
import com.itdemo.gulimail.ware.vo.HasStockVo;
import com.itdemo.gulimail.ware.vo.WareLockVo;


import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    void releaseStock(StockTo to);

    PageUtils queryPage(Map<String, Object> params);


    void addStock(Long skuId, Long WareId, Integer skuNum);

    List<HasStockVo> hasStock(List<Long> skuids);

    Boolean lockStock(WareLockVo vo) throws NoStockException;
}

