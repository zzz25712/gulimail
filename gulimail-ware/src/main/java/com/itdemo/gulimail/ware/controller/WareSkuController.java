package com.itdemo.gulimail.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.itdemo.common.exception.BizCodeEnum;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.R;
import com.itdemo.gulimail.ware.entity.WareSkuEntity;
import com.itdemo.gulimail.ware.exception.NoStockException;
import com.itdemo.gulimail.ware.service.WareSkuService;
import com.itdemo.gulimail.ware.vo.HasStockVo;
import com.itdemo.gulimail.ware.vo.LockResultVo;
import com.itdemo.gulimail.ware.vo.WareLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 商品库存
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/lock/stock")
    public R lockStock(@RequestBody WareLockVo vo){
        try {
            wareSkuService.lockStock(vo);
            return R.ok();
        } catch (NoStockException e) {
            return R.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(),BizCodeEnum.NO_STOCK_EXCEPTION.getMessage());
        }
    }

    @PostMapping("/hasStock")
    public R hasStock(@RequestBody List<Long> skuids){
        List<HasStockVo> vos = wareSkuService.hasStock(skuids);
        return R.ok().setData(vos);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
