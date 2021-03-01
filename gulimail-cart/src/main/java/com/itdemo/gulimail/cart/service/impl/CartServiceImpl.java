package com.itdemo.gulimail.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.itdemo.common.utils.R;
import com.itdemo.gulimail.cart.config.CartIntercepter;
import com.itdemo.gulimail.cart.feign.ProductFeignService;
import com.itdemo.gulimail.cart.service.CartService;
import com.itdemo.gulimail.cart.vo.Cart;
import com.itdemo.gulimail.cart.vo.CartItem;
import com.itdemo.gulimail.cart.vo.SkuInfoVo;
import com.itdemo.gulimail.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    public static final String CART_PREFIX = "gulimail:cart:";

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    private String s;


    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        //如果用户登陆的话 redis中存的是用户id 否则存的是uuid()
        BoundHashOperations<String, Object, Object> ops = getOps();
        String res = (String)ops.get(skuId.toString());
        //查询缓存购物车中是否有此商品
        if(res==null){
            //没有就直接添加
            CartItem cartItem = new CartItem();
            final CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                //远程调用通过skuid查询skuinfo
                R info = productFeignService.info(skuId);
                SkuInfoVo data = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItem.setSkuId(skuId);
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(data.getSkuDefaultImg());
                cartItem.setPrice(data.getPrice());
                cartItem.setTitle(data.getSkuTitle());
            }, threadPoolExecutor);

            CompletableFuture<Void> getSkuAttrTask = CompletableFuture.runAsync(() -> {
                //远程调用通过skuid找到对应的销售属性
                cartItem.setSkuAttr(productFeignService.stringlist(skuId));
            }, threadPoolExecutor);


            CompletableFuture.allOf(getSkuInfoTask,getSkuAttrTask).get();
            String s = JSON.toJSONString(cartItem);
            ops.put(skuId.toString(),s);
            return cartItem;
        }else{
            //有就修改此商品的数量
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount()+num);
            ops.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }

    }

    /*
    * 通过skuId查询购物项
    * */
    @Override
    public CartItem findCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> ops = getOps();
        String s = (String) ops.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(s, CartItem.class);
        return cartItem;
    }

    /*
    * 获取购物车
    * */
    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartIntercepter.threadLocal.get();

        if(userInfoTo.getUserId() != null){
            //登陆了
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            String tempKey = CART_PREFIX + userInfoTo.getUserKey();
            //如果临时购物车有购物项 则把临时购物车的东西和登陆后的购物车合并 并清空临时购物车
            //[合并购物车]
            List<CartItem> cartItems = getCartItems(tempKey);
            if(cartItems!=null){
                for (CartItem cartItem : cartItems) {
                    addToCart(cartItem.getSkuId(),cart.getCountNum());
                }
                //清空购物车
                clearCart(tempKey);
            }
            cart.setItems(getCartItems(cartKey));
        }else{
            //没登陆
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    /*
    * 清空购物车
    * */
    @Override
    public void clearCart(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> ops = getOps();
        CartItem cartItem = findCartItem(skuId);
        cartItem.setCheck(check==1?true:false);
        String s = JSON.toJSONString(cartItem);
        ops.put(skuId.toString(),s);
    }

    /*
    * 购物项增减数量进行修改
    * */
    @Override
    public void countItem(Long skuId, Integer num) {
        CartItem cartItem = findCartItem(skuId);
        cartItem.setCount(num);
        String s = JSON.toJSONString(cartItem);
        BoundHashOperations<String, Object, Object> ops = getOps();
        ops.put(skuId.toString(),s);
    }

    @Override
    public List<CartItem> getCheckedItem() {
        UserInfoTo infoTo = CartIntercepter.threadLocal.get();
        if(infoTo.getUserId()==null){
            return null;
        }else {
            String cartKey = CART_PREFIX+infoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            List<CartItem> collect = cartItems.stream()
                    .filter(obj -> obj.getCheck())
                    .map(obj -> {
                        R r = productFeignService.getPrice(obj.getSkuId());
                        String s = (String) r.get("data");
                        obj.setPrice(new BigDecimal(s));
                        return obj;
                    }).collect(Collectors.toList());
            return collect;
        }
    }


    /*
    * 获取到要操作的购物车
    * */
    public BoundHashOperations<String, Object, Object> getOps() {
        UserInfoTo userInfoTo = CartIntercepter.threadLocal.get();
        String cartkey = "";
        if (userInfoTo.getUserId()!=null) {
            cartkey = CART_PREFIX + userInfoTo.getUserId();
        }else{
            cartkey = CART_PREFIX + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> hashOperations = stringRedisTemplate.boundHashOps(cartkey);
        return hashOperations;
    }


    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if(values!=null && values.size()>0){
            List<CartItem> cartItems = values.stream().map(obj -> {
                String s = obj.toString();
                CartItem item = JSON.parseObject(s, CartItem.class);
                return item;
            }).collect(Collectors.toList());
            return cartItems;
        }else{
            return null;
        }
    }

}
