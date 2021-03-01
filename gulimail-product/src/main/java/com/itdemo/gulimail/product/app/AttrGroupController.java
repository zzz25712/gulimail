package com.itdemo.gulimail.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.itdemo.gulimail.product.entity.AttrEntity;
import com.itdemo.gulimail.product.service.AttrService;
import com.itdemo.gulimail.product.service.CategoryService;
import com.itdemo.gulimail.product.vo.AttrGroupRelationVo;
import com.itdemo.gulimail.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.itdemo.gulimail.product.entity.AttrGroupEntity;
import com.itdemo.gulimail.product.service.AttrGroupService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.R;



/**
 * 属性分组
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 10:47:49
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    AttrService attrService;

    ///product/attrgroup/{catelogId}/withattr
    @GetMapping("/{catelogId}/withattr")
    public R withattr(@PathVariable("catelogId")Long catelogId){
        List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrByCId(catelogId);
        return R.ok().put("data",vos);
    }

    @PostMapping("/attr/relation")
    public R attrRelationAdd(@RequestBody List<AttrGroupRelationVo> Vos){
        attrGroupService.attrRelationAdd(Vos);
        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R attrRelationDelate(@RequestBody List<AttrGroupRelationVo> Vos){
        attrGroupService.attrRelationDelate(Vos);
        return R.ok();
    }

    // /product/attrgroup/{attrgroupId}/attr/relation
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") long attrgroupId){
        List<AttrEntity> attrEntities = attrService.listAllAttr(attrgroupId);
        return R.ok().put("data",attrEntities);
    }

    // /product/attrgroup/{attrgroupId}/noattr/relation
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params,
                            @PathVariable("attrgroupId") long attrgroupId){
        PageUtils page = attrService.listSelectAttr(params,attrgroupId);
        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") long catelogId){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] Paths = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(Paths);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
