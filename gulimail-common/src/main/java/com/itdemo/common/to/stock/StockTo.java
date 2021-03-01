package com.itdemo.common.to.stock;

import lombok.Data;

@Data
public class StockTo {
    private Long id;
    private StockDetailTo detailTo;
}
