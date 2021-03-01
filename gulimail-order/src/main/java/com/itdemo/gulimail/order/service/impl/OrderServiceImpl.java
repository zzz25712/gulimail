package com.itdemo.gulimail.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.itdemo.common.exception.NoStockException;
import com.itdemo.common.utils.R;
import com.itdemo.common.vo.MemberReponsVo;
import com.itdemo.gulimail.order.Inteceptor.LoginInteceptor;
import com.itdemo.gulimail.order.constant.OrderConstant;
import com.itdemo.gulimail.order.entity.OrderItemEntity;
import com.itdemo.gulimail.order.enume.OrderStatusEnum;
import com.itdemo.gulimail.order.feign.CartFeignService;
import com.itdemo.gulimail.order.feign.MemberFeignService;
import com.itdemo.gulimail.order.feign.ProductFeignService;
import com.itdemo.gulimail.order.feign.WmsFeignService;
import com.itdemo.gulimail.order.service.OrderItemService;
import com.itdemo.gulimail.order.to.OrderCreatTo;
import com.itdemo.gulimail.order.vo.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itdemo.common.utils.PageUtils;
import com.itdemo.common.utils.Query;

import com.itdemo.gulimail.order.dao.OrderDao;
import com.itdemo.gulimail.order.entity.OrderEntity;
import com.itdemo.gulimail.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    public static ThreadLocal<OrderSubmitVo> orderThreadLocal = new ThreadLocal<>();

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    WmsFeignService wmsFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderItemService orderItemService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo getOrderItemVo() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberReponsVo memberReponsVo = LoginInteceptor.threadLocal.get();

        // *** 不同线程使用的request不同 把主线程的request请求放到其他线程中
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        //通过线程池异步进行两次远程查询
        CompletableFuture<Void> getAddressTask = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //远程调用查询所有地址
            List<MemberAddressVo> addressList = memberFeignService.getAddress(memberReponsVo.getId());
            orderConfirmVo.setMemberAddressVos(addressList);
        }, executor);

        CompletableFuture<Void> getItemTask = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //远程查询所有购物车中选中的商品
            List<OrderItemVo> checkedItemList = cartFeignService.getCheckedItem();
            orderConfirmVo.setOrderItemVos(checkedItemList);
        }, executor).thenRunAsync(()->{
            List<OrderItemVo> itemVos = orderConfirmVo.getOrderItemVos();
            List<Long> ids = itemVos.stream().map(item -> {
                return item.getSkuId();
            }).collect(Collectors.toList());
            R r = wmsFeignService.hasStock(ids);
            List<HasStockVo> vos = r.getData(new TypeReference<List<HasStockVo>>() {
            });
            if(vos!=null){
                Map<Long, Boolean> collect = vos.stream().collect(Collectors.toMap(HasStockVo::getSkuId, HasStockVo::isHasStock));
                orderConfirmVo.setStockvo(collect);
            }
        },executor);


        //查询积分信息
        Integer integration = memberReponsVo.getIntegration();
        orderConfirmVo.setIntegration(integration);

        //TODO 防重令牌
        String orderToken = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberReponsVo.getId(),orderToken,30, TimeUnit.MINUTES);
        orderConfirmVo.setOrderToken(orderToken);

        //其他数据自动计算
        CompletableFuture.allOf(getAddressTask,getItemTask).get();

        return orderConfirmVo;
    }

//    @GlobalTransactional  开启seata分布式事务控制
    @Transactional
    @Override
    public OrderResponVo submitOrder(OrderSubmitVo vo) throws NoStockException {
        //共享vo信息
        orderThreadLocal.set(vo);

        OrderResponVo responVo = new OrderResponVo();
        MemberReponsVo memberReponsVo = LoginInteceptor.threadLocal.get();

        String orderToken = vo.getOrderToken();
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        //原子令牌验证（利用Lua脚本 把对比和删除操作结合为一个原子操作）
        Long execute = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberReponsVo.getId()), orderToken);
        if(execute == 1L){
            //验证成功
            //下单 创建订单 验证令牌 验证价格 锁库存......
            OrderCreatTo orderCreatTo = creatOrder();
            BigDecimal payAmount = orderCreatTo.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            //验价 页面传回来的和自己计算的进行比较
            if(Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01){
                //保存订单
                saveOrder(orderCreatTo);
                //锁库存
                WareLockVo lockVo = new WareLockVo();
                lockVo.setOrderSn(orderCreatTo.getOrder().getOrderSn());
                List<OrderItemVo> collect = orderCreatTo.getOrderItems().stream().map(item -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setTitle(item.getSkuName());
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(collect);
                //远程调用锁库存
                R r = wmsFeignService.lockStock(lockVo);
                if(r.getcode() == 0){
                    //锁定成功
                    responVo.setOrder(orderCreatTo.getOrder());
                    responVo.setCode(0);
                    return responVo;
                }else {
                    //锁定失败
                    throw new NoStockException();
                }

            }else {
                responVo.setCode(2);
                return responVo;
            }

        }else{
            //验证失败
            responVo.setCode(1);
            return responVo;
        }
    }

    @Override
    public OrderEntity getOrderByOrdersn(String ordersn) {
        OrderEntity entity = getOne(new QueryWrapper<OrderEntity>().eq("order_sn", ordersn));
        return entity;
    }

    private void saveOrder(OrderCreatTo orderCreatTo) {
        save(orderCreatTo.getOrder());
        //设置订单新的修改时间
        orderCreatTo.getOrder().setModifyTime(new Date());
        orderItemService.saveBatch(orderCreatTo.getOrderItems());

    }

    private OrderCreatTo creatOrder(){
        MemberReponsVo memberReponsVo = LoginInteceptor.threadLocal.get();
        OrderCreatTo orderCreatTo = new OrderCreatTo();

        //创建订单号
        String orderSn = IdWorker.getTimeId();
        //1.创建订单
        OrderEntity orderEntity = bulidOrder(orderSn);
        orderEntity.setMemberId(memberReponsVo.getId());

        //2.创建订单项列表
        List<OrderItemEntity> items = bulidOrderItems(orderSn);

        //3.计算价格 积分相关信息
        computePrice(orderEntity,items);

        orderCreatTo.setFare(orderEntity.getFreightAmount());
        orderCreatTo.setOrder(orderEntity);
        orderCreatTo.setOrderItems(items);

        return orderCreatTo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> items) {
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal promot = new BigDecimal("0.0");
        BigDecimal integra = new BigDecimal("0.0");
        BigDecimal g_growth = new BigDecimal("0.0");
        BigDecimal g_integra = new BigDecimal("0.0");
        for (OrderItemEntity item : items) {
            total = total.add(item.getRealAmount());
            coupon = coupon.add(item.getCouponAmount());
            promot = promot.add(item.getPromotionAmount());
            integra = integra.add(item.getIntegrationAmount());
            g_growth = g_growth.add(new BigDecimal(item.getGiftGrowth().toString()));
            g_integra = g_integra.add(new BigDecimal(item.getGiftIntegration().toString()));
        }
        orderEntity.setTotalAmount(total);
        //计算要付的总价
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promot);
        orderEntity.setIntegrationAmount(integra);
        orderEntity.setGrowth(g_growth.intValue());
        orderEntity.setIntegration(g_integra.intValue());

    }

    /**
     * 创建订单项列表
     *
     *
     * @param orderSn
     * */
    private List<OrderItemEntity> bulidOrderItems(String orderSn) {
        List<OrderItemVo> checkedItem = cartFeignService.getCheckedItem();
        if(checkedItem!=null && checkedItem.size()>0){
            List<OrderItemEntity> collect = checkedItem.stream().map(item -> {
                OrderItemEntity entity = bulidOrderItem(item);
                entity.setOrderSn(orderSn);
                return entity;
            }).collect(Collectors.toList());
            return collect;
        }
       return null;
    }

    /**
     * 创建每一个订单项
     *
     * @param itemVo
     *
     * */
    private OrderItemEntity bulidOrderItem(OrderItemVo itemVo) {
        OrderItemEntity orderItem = new OrderItemEntity();
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal promot = new BigDecimal("0.0");
        BigDecimal integra = new BigDecimal("0.0");
        //1.订单信息和订单号
        //2.spu信息
        R r = productFeignService.getSpuinfobySkuid(itemVo.getSkuId());
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        orderItem.setSpuId(data.getId());
        orderItem.setSpuBrand(data.getBrandId().toString());
        orderItem.setSpuName(data.getSpuName());
        orderItem.setCategoryId(data.getCatalogId());
        //3.sku信息
        orderItem.setSkuId(itemVo.getSkuId());
        orderItem.setSkuName(itemVo.getTitle());
        orderItem.setSkuPic(itemVo.getImage());
        orderItem.setSkuPrice(itemVo.getPrice());
        String s = StringUtils.collectionToDelimitedString(itemVo.getSkuAttr(), ";");
        orderItem.setSkuAttrsVals(s);
        orderItem.setSkuQuantity(itemVo.getCount());

        //4.积分信息
        orderItem.setGiftGrowth(itemVo.getPrice().multiply(new BigDecimal(itemVo.getCount().toString())).intValue());
        orderItem.setGiftIntegration(itemVo.getPrice().multiply(new BigDecimal(itemVo.getCount().toString())).intValue());

        //5.设置金额信息
        orderItem.setCouponAmount(coupon);
        orderItem.setPromotionAmount(promot);
        orderItem.setIntegrationAmount(integra);

        //6.设置应付总额
        BigDecimal orgin = itemVo.getPrice().multiply(new BigDecimal(itemVo.getCount().toString()));
        BigDecimal substract = orgin.subtract(orderItem.getCouponAmount())
                .subtract(orderItem.getPromotionAmount())
                .subtract(orderItem.getIntegrationAmount());
        orderItem.setRealAmount(substract);

        return orderItem;
    }

    /**
     * 创建订单
     *
     * */
    private OrderEntity bulidOrder(String orderSn) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);
        OrderSubmitVo orderSubmitVo = orderThreadLocal.get();
        //远程调用查询fareVo(包括运费和地址信息)
        R fare = wmsFeignService.getFare(orderSubmitVo.getAttrId());
        FareVo fareVo = fare.getData(new TypeReference<FareVo>() {
        });
        //设置运费和地址信息
        orderEntity.setFreightAmount(fareVo.getFare());
        orderEntity.setReceiverDetailAddress(fareVo.getAddress().getDetailAddress());
        orderEntity.setReceiverName(fareVo.getAddress().getName());
        orderEntity.setReceiverPhone(fareVo.getAddress().getPhone());
        orderEntity.setReceiverCity(fareVo.getAddress().getCity());
        orderEntity.setReceiverPostCode(fareVo.getAddress().getPostCode());
        orderEntity.setReceiverProvince(fareVo.getAddress().getProvince());
        orderEntity.setReceiverRegion(fareVo.getAddress().getRegion());

        //设置订单状态
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());

        //设置订单自动确认时间 7天
        orderEntity.setAutoConfirmDay(7);

        //设置订单删除状态（未删除）
        orderEntity.setDeleteStatus(0);
        return orderEntity;
    }

}