package com.itdemo.gulimail.ware.service.impl;

import com.itdemo.common.constant.WareConstant;
import com.itdemo.gulimail.ware.entity.PurchaseDetailEntity;
import com.itdemo.gulimail.ware.service.PurchaseDetailService;
import com.itdemo.gulimail.ware.service.WareSkuService;
import com.itdemo.gulimail.ware.vo.MergeVo;
import com.itdemo.gulimail.ware.vo.PurchaseDoneItemVo;
import com.itdemo.gulimail.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;

import com.itdemo.gulimail.ware.dao.PurchaseDao;
import com.itdemo.gulimail.ware.entity.PurchaseEntity;
import com.itdemo.gulimail.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils unreceiveList(Map<String, Object> params) {

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );

        return new PageUtils(page);
    }

    @Override
    public void Merge(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if(purchaseId == null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        List<Long> item = mergeVo.getItems();
        Long finPurchaseId = purchaseId;

        if(item != null && item.size() > 0){
            List<PurchaseDetailEntity> collect = item.stream().map(w -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(w);
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                detailEntity.setPurchaseId(finPurchaseId);
                return detailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);
            PurchaseEntity entity = new PurchaseEntity();
            entity.setId(finPurchaseId);
            entity.setUpdateTime(new Date());
            entity.setCreateTime(new Date());
            this.updateById(entity);
        }

    }

    @Override
    @Transactional
    public void Receiced(List<Long> ls) {
        //1.确认当前采购状态是否为新建或已分配
        List<PurchaseEntity> collect = ls.stream().map(item -> {
            PurchaseEntity detailEntity = this.getById(item);
            return detailEntity;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            } else {
                return false;
            }
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setCreateTime(new Date());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //2.修改采购单状态
        this.updateBatchById(collect);

        //3.修改采购项状态
        ls.forEach(item -> {
          List<PurchaseDetailEntity> detailEntities = purchaseDetailService.listDetailByPurchaseId(item);
            List<PurchaseDetailEntity> collect1 = detailEntities.stream().map(w -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(w.getId());
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return detailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect1);
        });

    }

    @Override
    public void Done(PurchaseDoneVo purchaseDoneVo) {

        List<PurchaseDoneItemVo> doneVoItems = purchaseDoneVo.getItems();
        //修改采购项状态
        boolean flag = true;
        List<PurchaseDetailEntity> pes = new ArrayList<>();

        for (PurchaseDoneItemVo item: doneVoItems) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if(item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            }else{
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                //入库
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(detailEntity.getSkuId(),detailEntity.getWareId(),detailEntity.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getItemId());
            pes.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(pes);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseDoneVo.getId());
        purchaseEntity.setStatus(flag?WareConstant.PurchaseStatusEnum.FINISH.getCode():WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setCreateTime(new Date());
        this.updateById(purchaseEntity);

    }

}