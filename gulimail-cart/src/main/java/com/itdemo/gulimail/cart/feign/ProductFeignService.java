package com.itdemo.gulimail.cart.feign;

import com.itdemo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("gulimail-product")
public interface ProductFeignService {
    @RequestMapping("/product/skuinfo/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
    R info(@PathVariable("skuId") Long skuId);

    @RequestMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    List<String> stringlist(@PathVariable("skuId")Long skuId);

    @GetMapping("/product/skuinfo/{skuId}/getprice")
    R getPrice(@PathVariable("skuId")Long skuId);
}
