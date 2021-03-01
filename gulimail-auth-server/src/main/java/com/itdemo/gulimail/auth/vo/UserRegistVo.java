package com.itdemo.gulimail.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserRegistVo {
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 6,max = 18,message = "用户名为6-18位的字符")
    private String userName;
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6,max = 18,message = "密码为6-18位的字符")
    private String password;
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$",message = "手机号格式不对")
    private String phone;
    @NotEmpty(message = "验证码不能为空")
    private String code;
}
