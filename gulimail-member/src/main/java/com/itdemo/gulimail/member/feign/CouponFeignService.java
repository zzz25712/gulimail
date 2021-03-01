package com.itdemo.gulimail.member.feign;

import com.itdemo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimail-coupon")
public interface CouponFeignService {
    //写 全路径
    @RequestMapping("/coupon/coupon/member/list")
    public R Membercoupon();
}
