package com.itdemo.gulimail.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.itdemo.gulimail.product.entity.ProductAttrValueEntity;
import com.itdemo.gulimail.product.service.ProductAttrValueService;
import com.itdemo.gulimail.product.vo.AttrRespVo;
import com.itdemo.gulimail.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itdemo.gulimail.product.service.AttrService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.R;



/**
 * 商品属性
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 10:47:49
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;


    @RequestMapping("/base/listforspu/{spuId}")
    //@RequiresPermissions("product:attr:list")
    public R list(@PathVariable("spuId") Long spuId){

       List<ProductAttrValueEntity> data = productAttrValueService.ListforSpuByid(spuId);
        return R.ok().put("data", data);
    }


    @RequestMapping("/{type}/list/{catelogId}")
    public R attrBaseList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") long catelogId,
                          @PathVariable("type") String type){
        PageUtils page = attrService.queryBaseAttrPage(params,catelogId,type);
        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }


    @RequestMapping("/update/{spuId}")
    public R updateByspuId(@RequestBody List<ProductAttrValueEntity> entities,
                           @PathVariable("spuId") Long spuId){
        productAttrValueService.updateAttrByspuId(spuId,entities);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrRespVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
