package com.itdemo.gulimail.ware.service.impl;


import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itdemo.common.to.stock.StockDetailTo;
import com.itdemo.common.to.stock.StockTo;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;
import com.itdemo.common.utils.R;
import com.itdemo.gulimail.ware.dao.WareSkuDao;
import com.itdemo.gulimail.ware.entity.WareOrderTaskDetailEntity;
import com.itdemo.gulimail.ware.entity.WareOrderTaskEntity;
import com.itdemo.gulimail.ware.entity.WareSkuEntity;
import com.itdemo.gulimail.ware.exception.NoStockException;
import com.itdemo.gulimail.ware.fegin.OrderFeignService;
import com.itdemo.gulimail.ware.fegin.ProductFeignService;
import com.itdemo.gulimail.ware.service.WareOrderTaskDetailService;
import com.itdemo.gulimail.ware.service.WareOrderTaskService;
import com.itdemo.gulimail.ware.service.WareSkuService;
import com.itdemo.gulimail.ware.vo.HasStockVo;
import com.itdemo.gulimail.ware.vo.OrderItemVo;
import com.itdemo.gulimail.ware.vo.OrderVo;
import com.itdemo.gulimail.ware.vo.WareLockVo;
import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RabbitListener(queues = "stock.release.stock.queue")
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService orderTaskDetailService;

    @Autowired
    OrderFeignService orderFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitHandler
    public void handleStockLockedRelease(StockTo to,Message message){
        System.out.println("收到解锁库存消息");
        Long id = to.getId();
        StockDetailTo detail = to.getDetailTo();
        Long detailId = detail.getId();
        WareOrderTaskDetailEntity byId = orderTaskDetailService.getById(detailId);
        if(byId != null){
            //解锁

        }else{
            //不解锁

        }

    }

    /**
     * 查询订单的锁库存信息
     * 有：锁定库存成功
     *   解锁： 1.有这个订单
     *              订单状态：待支付 不能解锁
     *                        已取消 解锁
     *         2.没有这个订单 必须解锁
     *  没有：库存锁定失败 已经回滚 无需解锁
     *
     *  如果库存解锁失败 保存消息队列中的消息 ack改为手动确认
     * */
    @Override
    public void releaseStock(StockTo to){
        Long id = to.getId();
        StockDetailTo detailTo = to.getDetailTo();
        WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detailTo.getId());
        if(byId != null){
            //工作单存在的话 进行解锁操作
            WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getById(id);
            //通过订单号远程查询订单信息
            R r = orderFeignService.getOrderByOrdersn(orderTaskEntity.getOrderSn());
            if(r.getcode() == 0){
                OrderVo orderVo = r.getData(new TypeReference<OrderVo>() {
                });
                if(orderVo==null || orderVo.getStatus()==4){
                    //该订单不存在
                    //订单状态为已取消 解锁库存
                    //库存详情单状态为解锁 才进行解锁操作
                    if(byId.getLockStatus() == 1){
                        Unlocked(byId.getSkuId(),byId.getSkuNum(),byId.getWareId(),byId.getId());
                    }else{

                    }
                }
            }else{
                throw new RuntimeException("远程调用失败");
            }
        }else{

        }
    }

    /**
     * 解锁库存
     *
     * @param skuId
     * @param skuNum
     * @param wareId
     * @param detailId*/
    private void Unlocked(Long skuId, Integer skuNum, Long wareId, Long detailId){
       int count = wareSkuDao.Unlocked(skuId,skuNum,wareId);
       if(count == 0){
           System.out.println(wareId+"号仓库的"+skuId+"号商品解锁失败");
       }else{
           System.out.println(wareId+"号仓库的"+skuId+"号商品解锁成功");
       }
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(detailId);
        entity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(entity);

    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId: 1
         * wareId: 2
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(wareSkuEntities == null || wareSkuEntities.size() == 0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            try {
                R info = productFeignService.info(skuId);
                Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");

                if(info.getcode() == 0){
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){

            }
            wareSkuDao.insert(wareSkuEntity);
        }else{
            wareSkuDao.updateStock(skuId,wareId,skuNum);
        }
    }

    @Override
    public List<HasStockVo> hasStock(List<Long> skuids) {
        List<HasStockVo> collect = skuids.stream().map(skuid -> {
            HasStockVo hasStockVo = new HasStockVo();
            Long count = baseMapper.gethasStock(skuid);
            hasStockVo.setSkuId(skuid);
            hasStockVo.setHasStock(count==null?false:count>0);
            return hasStockVo;
        }).collect(Collectors.toList());

        return collect;
    }

    @Transactional
    @Override
    public Boolean lockStock(WareLockVo vo) throws NoStockException {
        //创建库存工作单 用于订单失败后库存解锁
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);

        //找到每个商品在哪个仓库有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> wareHasStocks = locks.stream().map(item -> {
            SkuWareHasStock wareHasStock = new SkuWareHasStock();
            wareHasStock.setSkuId(item.getSkuId());
            wareHasStock.setNum(item.getCount());

            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(item.getSkuId());
            wareHasStock.setWareIds(wareIds);
            return wareHasStock;
        }).collect(Collectors.toList());

        for (SkuWareHasStock hasStock : wareHasStocks) {
            Boolean hasLocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareIds();
            if(wareIds == null || wareIds.size() == 0){
                //没有有指定skuid商品数量的仓库
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
             //锁库存
            int count = wareSkuDao.lockWare(skuId,wareId,hasStock.getNum());
            if(count == 0){
             //锁失败 重试下一个仓库
            }else {
                //锁成功
                //创建库存详情工作单
                WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null, skuId, "", hasStock.getNum(), wareOrderTaskEntity.getId(), wareId, 1);
                wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                //创建MQ接收的消息类型 包括工作单详情和工作单id
                StockTo stockTo = new StockTo();
                stockTo.setId(wareOrderTaskEntity.getId());
                StockDetailTo detailTo = new StockDetailTo();
                BeanUtils.copyProperties(wareOrderTaskDetailEntity,detailTo);
                stockTo.setDetailTo(detailTo);
                rabbitTemplate.convertSendAndReceive("stock-event-exchange","stock.locked",stockTo);
                hasLocked = true;
                break;
                }
             }
             if(hasLocked == false){
                throw new NoStockException(skuId);
             }

        }
        return true;
    }

    @Data
    class SkuWareHasStock{
        private Long skuId;
        private Integer num;
        private List<Long> wareIds;
    }

}