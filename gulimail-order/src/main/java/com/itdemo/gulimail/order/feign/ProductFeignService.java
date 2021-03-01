package com.itdemo.gulimail.order.feign;

import com.itdemo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimail-product")
public interface ProductFeignService {
    @GetMapping("/product/spuinfo/skuid/{id}")
    R getSpuinfobySkuid(@PathVariable("id")Long skuId);
}
