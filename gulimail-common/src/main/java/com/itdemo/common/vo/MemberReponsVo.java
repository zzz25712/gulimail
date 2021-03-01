package com.itdemo.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString
@Data
public class MemberReponsVo implements Serializable{

    private Long id;

    private Long levelId;

    private String username;

    private String password;

    private String nickname;

    private String mobile;

    private String email;

    private String header;

    private Integer gender;

    private Date birth;

    private String city;

    private String job;

    private String sign;

    private Integer sourceType;
    /**
     * $column.comments
     */
    private Integer integration;
    /**
     * $column.comments
     */
    private Integer growth;
    /**
     * $column.comments
     */
    private Integer status;
    /**
     * $column.comments
     */
    private Date createTime;

    private String accessToken;

    private String uid;

    private String remindIn;
}
