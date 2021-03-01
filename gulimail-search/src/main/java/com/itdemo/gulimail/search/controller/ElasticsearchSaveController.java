package com.itdemo.gulimail.search.controller;

import com.itdemo.common.exception.BizCodeEnum;
import com.itdemo.common.to.es.SkuEsModel;
import com.itdemo.common.utils.R;
import com.itdemo.gulimail.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequestMapping("search/save")
@RestController
public class ElasticsearchSaveController {
    @Autowired
    ProductSaveService productSaveService;

    @PostMapping("/product")
    public R saveProduct(@RequestBody List<SkuEsModel> skuEsModels) throws IOException {
        boolean b = false;
        try {
            b = productSaveService.saveIndex(skuEsModels);
        }catch (Exception e){
           return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }
        if(!b){
            return R.ok();
        }else{
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }
    }
}
