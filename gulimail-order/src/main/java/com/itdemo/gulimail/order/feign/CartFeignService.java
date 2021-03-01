package com.itdemo.gulimail.order.feign;

import com.itdemo.gulimail.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("gulimail-cart")
public interface CartFeignService {
    @GetMapping("/getCheckedItem")
    List<OrderItemVo> getCheckedItem();
}
