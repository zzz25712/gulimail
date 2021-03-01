package com.itdemo.common.exception;

public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    TOO_MANY_REQUEST(10003,"请求过多"),
    VALID_EXCEPTION(10001,"参数格式校验异常"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    SMS_CODE_EXCEPTION(10002,"短信验证码发送频繁，稍后重试"),
    LOGIN_PASSWORD_VALIDE_EXCEPTION(15001,"用户名密码错误"),
    NO_STOCK_EXCEPTION(21000,"库存数不足");
    private Integer code;
    private String message;

    BizCodeEnum(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
