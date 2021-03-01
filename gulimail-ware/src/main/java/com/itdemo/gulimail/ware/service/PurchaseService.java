package com.itdemo.gulimail.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.gulimail.ware.entity.PurchaseEntity;
import com.itdemo.gulimail.ware.vo.MergeVo;
import com.itdemo.gulimail.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author lvxiaofei
 * @email sunlightcs@gmail.com
 * @date 2020-08-25 11:09:50
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils unreceiveList(Map<String, Object> params);

    void Merge(MergeVo mergeVo);

    void Receiced(List<Long> ls);

    void Done(PurchaseDoneVo purchaseDoneVo);
}

