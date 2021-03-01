package com.itdemo.gulimail.product.fegin;

import com.itdemo.common.to.SkuReduceTo;
import com.itdemo.common.to.SpuBoundTo;
import com.itdemo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimail-coupon")
public interface SpuBoundFeginService {

    @PostMapping("/coupon/spubounds/save")
    R saveBound(@RequestBody SpuBoundTo boundTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduceTo(@RequestBody SkuReduceTo skuReduceTo);
}
