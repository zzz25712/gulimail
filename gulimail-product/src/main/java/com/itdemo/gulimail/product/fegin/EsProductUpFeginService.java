package com.itdemo.gulimail.product.fegin;

import com.itdemo.common.to.es.SkuEsModel;
import com.itdemo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimail-search")
public interface EsProductUpFeginService {
    @PostMapping("/search/save/product")
    R saveProduct(@RequestBody List<SkuEsModel> skuEsModels);
}
