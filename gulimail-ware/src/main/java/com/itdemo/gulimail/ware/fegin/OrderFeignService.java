package com.itdemo.gulimail.ware.fegin;

import com.itdemo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimail-order")
public interface OrderFeignService {
    @GetMapping("/order/order/status/{ordersn}")
    R getOrderByOrdersn(@PathVariable("ordersn")String ordersn);
}
