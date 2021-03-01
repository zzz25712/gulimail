package com.itdemo.gulimail.search.feign;

import com.itdemo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimail-product")
public interface ProductFeignService {
    @GetMapping("/product/attr/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    R Attrinfo(@PathVariable("attrId") Long attrId);
}
