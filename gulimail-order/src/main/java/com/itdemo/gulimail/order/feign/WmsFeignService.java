package com.itdemo.gulimail.order.feign;

import com.itdemo.common.utils.R;
import com.itdemo.gulimail.order.vo.WareLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("gulimail-ware")
public interface WmsFeignService {
    @PostMapping("/ware/waresku/hasStock")
    R hasStock(@RequestBody List<Long> skuids);

    @GetMapping("/ware/wareinfo/fare")
    R getFare(@RequestParam("attrId")Long attrId);

    @PostMapping("/ware/waresku/lock/stock")
    R lockStock(@RequestBody WareLockVo vo);
}
