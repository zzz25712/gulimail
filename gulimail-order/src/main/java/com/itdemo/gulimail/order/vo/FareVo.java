package com.itdemo.gulimail.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {
    MemberAddressVo address;
    BigDecimal fare;
}