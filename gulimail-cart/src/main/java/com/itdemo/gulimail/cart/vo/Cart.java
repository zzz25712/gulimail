package com.itdemo.gulimail.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车
 * */
public class Cart {
    List<CartItem> items;
    private Integer countNum;//商品数量
    private Integer typeCount;//商品类型数量
    private BigDecimal totalAmount;//商品总价
    private BigDecimal reduce = new BigDecimal("0.00");//优惠价格

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    /*
    *  遍历商品 获取商品数量
    * */
    public Integer getCountNum() {
        int count = 0;
        if(items!=null && items.size()>0){
            for (CartItem item : items) {
               count += item.getCount();
            }
        }
        return count;
    }

    /*
    *  遍历商品 获取商品类型数量
    * */
    public Integer getTypeCount() {
        int count = 0;
        if(items!=null && items.size()>0){
            for (CartItem item : items) {
                count += 1;
            }
        }
        return count;
    }

    /*
    *  遍历商品 计算所有商品价格
    * */
    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        if(items!=null && items.size()>0){
            for (CartItem item : items) {
                if(item.getCheck()){
                    amount = amount.add(item.getTotalPrice());
                }
            }
        }
        //减去优惠价格
        amount = amount.subtract(reduce);
        return amount;
    }


    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
