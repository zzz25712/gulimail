package com.itdemo.gulimail.member.exception;

public class MobileeExsitException extends RuntimeException{
    public MobileeExsitException() {
        super("手机号已经存在");
    }
}
