package com.itdemo.gulimail.product.vo;

import com.itdemo.gulimail.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrsVo {
    private Long attrGroupId;

    private String attrGroupName;

    private Integer sort;

    private String descript;

    private String icon;

    private Long catelogId;

    private List<AttrEntity> attrs;
}
