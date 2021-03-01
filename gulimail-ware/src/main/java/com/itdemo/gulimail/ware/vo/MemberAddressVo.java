package com.itdemo.gulimail.ware.vo;

import lombok.Data;

@Data
public class MemberAddressVo {
    private Long id;
    /**
     * $column.comments
     */
    private Long memberId;
    /**
     * $column.comments
     */
    private String name;
    /**
     * $column.comments
     */
    private String phone;
    /**
     * $column.comments
     */
    private String postCode;
    /**
     * $column.comments
     */
    private String province;
    /**
     * $column.comments
     */
    private String city;
    /**
     * $column.comments
     */
    private String region;
    /**
     * $column.comments
     */
    private String detailAddress;
    /**
     * $column.comments
     */
    private String areacode;
    /**
     * $column.comments
     */
    private Integer defaultStatus;
}
