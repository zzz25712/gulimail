package com.itdemo.gulimail.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ToString
public class OrderConfirmVo {
    @Getter @Setter
    List<MemberAddressVo> memberAddressVos;//会员地址信息

    @Getter @Setter
    List<OrderItemVo> orderItemVos;//购物项信息

    @Getter @Setter
    Integer integration;//会员积分信息

    @Getter @Setter
    Map<Long,Boolean> stockvo;

    @Getter @Setter
    String orderToken;//防止重复提交所带的令牌

   // BigDecimal total;//订单总额

    public Integer getCount(){
        Integer count = new Integer("0");
        if(orderItemVos!=null&&orderItemVos.size()>0){
            for (OrderItemVo itemVo : orderItemVos) {
                count+=itemVo.getCount();
            }
        }
        return count;
    }

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0.00");
        if(orderItemVos!=null&&orderItemVos.size()>0){
            for (OrderItemVo itemVo : orderItemVos) {
                BigDecimal multiply = itemVo.getPrice().multiply(new BigDecimal(itemVo.getCount().toString()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

   // BigDecimal payPrice;//应付总额

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
