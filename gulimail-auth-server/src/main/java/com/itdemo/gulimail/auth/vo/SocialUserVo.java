package com.itdemo.gulimail.auth.vo;

import lombok.Data;

@Data
public class SocialUserVo {
    private String access_token;
    private String remind_in;
    private Long expires_in;
    private String uid;
    private String isRealName;
}
