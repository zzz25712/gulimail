package com.itdemo.gulimail.member.exception;

public class UsernameExsitException extends RuntimeException{
    public UsernameExsitException() {
        super("用户名已经存在");
    }
}
