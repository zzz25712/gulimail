package com.itdemo.gulimail.ware.vo;

import lombok.Data;

@Data
public class PurchaseDoneItemVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
