package com.itdemo.gulimail.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class SpuItemAttrgroupVo{
    private String groupName;
    private List<Attr> attrs;
}
