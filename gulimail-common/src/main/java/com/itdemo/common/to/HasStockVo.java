package com.itdemo.common.to;

import lombok.Data;

@Data
public class HasStockVo {
    private Long skuId;
    private boolean hasStock;
}
