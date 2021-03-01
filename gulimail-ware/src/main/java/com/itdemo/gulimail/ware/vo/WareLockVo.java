package com.itdemo.gulimail.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareLockVo {
    private String orderSn;
    private List<OrderItemVo> locks;
}
