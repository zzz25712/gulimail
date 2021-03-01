package com.itdemo.common.exception;

public class NoStockException extends Exception {
    private Long skuId;
    public NoStockException(Long skuId){
        super("没有"+skuId+"号商品的库存");
    }

    public NoStockException() {
        super("没有库存");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
